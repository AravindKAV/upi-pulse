package com.upipulse.domain.usecase

import com.upipulse.data.repository.TransactionRepository
import com.upipulse.domain.model.Transaction
import javax.inject.Inject

class UpsertTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        repository.upsertTransactions(listOf(transaction))
    }
}