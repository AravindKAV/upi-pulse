package com.upipulse.data.repository

import com.upipulse.domain.model.DashboardSummary
import com.upipulse.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeTransactions(limit: Int = 200): Flow<List<Transaction>>
    fun observeDashboardSummary(): Flow<DashboardSummary>
    suspend fun upsertTransactions(transactions: List<Transaction>)
}