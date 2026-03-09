package com.upipulse.data.repository

import com.upipulse.data.local.dao.BudgetDao
import com.upipulse.data.local.dao.TransactionDao
import com.upipulse.data.local.entity.TransactionEntity
import com.upipulse.di.IoDispatcher
import com.upipulse.domain.model.CategorySpend
import com.upipulse.domain.model.DashboardSummary
import com.upipulse.domain.model.Merchant
import com.upipulse.domain.model.MerchantSpend
import com.upipulse.domain.model.Transaction
import com.upipulse.domain.model.TransactionDirection
import com.upipulse.domain.model.TransactionSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TransactionRepository {

    override fun observeTransactions(limit: Int): Flow<List<Transaction>> =
        transactionDao.observeRecent(limit)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)

    override fun observeDashboardSummary(): Flow<DashboardSummary> =
        combine(
            transactionDao.observeTotals(),
            transactionDao.observeMerchantSpend(),
            transactionDao.observeCategorySpend(),
            budgetDao.observeBudgets()
        ) { totals, merchantProjections, categoryProjections, budgets ->
            val budgetMap = budgets.associateBy { it.category }
            DashboardSummary(
                totalOutflow = totals.totalDebit ?: 0.0,
                totalInflow = totals.totalCredit ?: 0.0,
                avgTicketSize = totals.averageTicket ?: 0.0,
                topMerchants = merchantProjections.map {
                    MerchantSpend(
                        merchant = Merchant(
                            id = it.merchantId,
                            name = it.merchantName
                        ),
                        amount = it.total,
                        transactions = it.count
                    )
                },
                categoryBreakdown = categoryProjections.map { projection ->
                    CategorySpend(
                        category = projection.category,
                        amount = projection.total,
                        budget = budgetMap[projection.category]?.monthlyLimit
                    )
                },
                cashbackTotal = merchantProjections
                    .filter { it.merchantName.contains("cashback", ignoreCase = true) }
                    .sumOf { it.total }
            )
        }.flowOn(ioDispatcher)

    override suspend fun upsertTransactions(transactions: List<Transaction>) {
        withContext(ioDispatcher) {
            val entities = transactions.map { it.toEntity() }
            transactionDao.upsert(entities)
        }
    }

    private fun TransactionEntity.toDomain(): Transaction = Transaction(
        id = id,
        referenceId = referenceId,
        merchant = Merchant(id = merchantId, name = merchantName),
        category = category,
        amount = amount,
        currency = currency,
        direction = direction,
        timestamp = timestamp,
        source = source,
        rawDescription = rawDescription,
        metadata = metadata
    )

    private fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
        id = id,
        referenceId = referenceId,
        merchantName = merchant.name,
        merchantId = merchant.id,
        category = category,
        amount = amount,
        currency = currency,
        direction = direction,
        timestamp = timestamp,
        source = source,
        rawDescription = rawDescription,
        metadata = metadata
    )
}