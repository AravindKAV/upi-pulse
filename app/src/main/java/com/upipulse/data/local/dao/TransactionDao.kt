package com.upipulse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.upipulse.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(transactions: List<TransactionEntity>)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int = 200): Flow<List<TransactionEntity>>

    @Query(
        "SELECT SUM(CASE WHEN direction = 'DEBIT' THEN amount ELSE 0 END) AS totalDebit, " +
            "SUM(CASE WHEN direction = 'CREDIT' THEN amount ELSE 0 END) AS totalCredit, " +
            "AVG(amount) AS averageTicket FROM transactions"
    )
    fun observeTotals(): Flow<TotalsProjection>

    @Query(
        "SELECT merchantName AS merchantName, merchantId AS merchantId, SUM(amount) AS total, COUNT(*) AS count " +
            "FROM transactions WHERE direction = 'DEBIT' GROUP BY merchantName, merchantId " +
            "ORDER BY total DESC LIMIT :limit"
    )
    fun observeMerchantSpend(limit: Int = 5): Flow<List<MerchantSpendProjection>>

    @Query(
        "SELECT category AS category, SUM(amount) AS total FROM transactions WHERE direction = 'DEBIT' " +
            "GROUP BY category ORDER BY total DESC"
    )
    fun observeCategorySpend(): Flow<List<CategorySpendProjection>>

    data class TotalsProjection(
        val totalDebit: Double?,
        val totalCredit: Double?,
        val averageTicket: Double?
    )

    data class MerchantSpendProjection(
        val merchantName: String,
        val merchantId: String?,
        val total: Double,
        val count: Int
    )

    data class CategorySpendProjection(
        val category: String,
        val total: Double
    )
}