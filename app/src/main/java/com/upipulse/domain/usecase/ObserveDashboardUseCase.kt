package com.upipulse.domain.usecase

import com.upipulse.data.repository.TransactionRepository
import com.upipulse.domain.model.DashboardSummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDashboardUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<DashboardSummary> = repository.observeDashboardSummary()
}