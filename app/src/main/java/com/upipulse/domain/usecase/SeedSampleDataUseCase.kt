package com.upipulse.domain.usecase

import com.upipulse.data.repository.ExpenseRepository
import com.upipulse.data.sample.SampleDataSource
import javax.inject.Inject

class SeedSampleDataUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(force: Boolean = false) {
        // Always ensure categories exist so the app functions correctly.
        repository.ensureCategories(SampleDataSource.categories())
        
        if (force) {
            // When user resets, we clear everything to provide a clean state.
            repository.clearTransactions()
            // We should also have a way to clear accounts if they want a total reset.
            // For now, clearing transactions will reset the dashboard spend to 0.
        }
    }
}
