package com.upipulse.domain.usecase

import com.upipulse.data.repository.TransactionRepository
import com.upipulse.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Transaction>> = repository.observeTransactions()
}