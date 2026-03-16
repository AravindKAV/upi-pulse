package com.upipulse.domain.usecase

import com.upipulse.data.repository.ExpenseRepository
import com.upipulse.data.sample.SampleDataSource
import javax.inject.Inject

class SeedSampleDataUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(force: Boolean = false) {
        // We only ensure categories exist so the app functions correctly.
        // We no longer seed dummy transactions or bank accounts for production use.
        repository.ensureCategories(SampleDataSource.categories())
    }
}
