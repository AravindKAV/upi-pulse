package com.upipulse.domain.usecase

import com.upipulse.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ResetSampleDataUseCase @Inject constructor(
    private val repository: ExpenseRepository,
    private val seedSampleDataUseCase: SeedSampleDataUseCase
) {
    suspend operator fun invoke() {
        // Clear all real user data
        repository.clearTransactions()
        val accounts = repository.observeAccounts().first()
        accounts.forEach { repository.deleteAccount(it) }
        
        // Re-ensure core categories exist
        seedSampleDataUseCase(force = true)
    }
}
