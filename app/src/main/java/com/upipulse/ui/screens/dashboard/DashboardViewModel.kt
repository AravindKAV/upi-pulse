package com.upipulse.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upipulse.domain.model.DashboardAnalytics
import com.upipulse.domain.usecase.ObserveDashboardAnalyticsUseCase
import com.upipulse.domain.usecase.ObserveTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Ready(val analytics: DashboardAnalytics, val isDemo: Boolean) : DashboardUiState
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    observeDashboardAnalyticsUseCase: ObserveDashboardAnalyticsUseCase,
    observeTransactionsUseCase: ObserveTransactionsUseCase
) : ViewModel() {
    val uiState: StateFlow<DashboardUiState> = combine(
        observeDashboardAnalyticsUseCase(),
        observeTransactionsUseCase()
    ) { analytics, transactions ->
        // No longer streaming demo data if empty. 
        // Dashboard will naturally show 0 values and empty lists from the 'analytics' object.
        DashboardUiState.Ready(analytics, isDemo = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState.Loading)
}
