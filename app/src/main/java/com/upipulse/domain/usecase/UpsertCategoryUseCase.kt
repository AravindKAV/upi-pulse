package com.upipulse.domain.usecase

import com.upipulse.data.repository.ExpenseRepository
import com.upipulse.domain.model.Category
import javax.inject.Inject

class UpsertCategoryUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(category: Category): Category = repository.upsertCategory(category)
}
