package com.upipulse.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upipulse.domain.model.DashboardSummary
import com.upipulse.domain.usecase.ObserveDashboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Ready(val summary: DashboardSummary) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    observeDashboardUseCase: ObserveDashboardUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<DashboardUiState> = MutableStateFlow(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeDashboardUseCase()
                .onEach { summary -> _uiState.value = DashboardUiState.Ready(summary) }
                .catch { throwable -> _uiState.value = DashboardUiState.Error(throwable.message.orEmpty()) }
                .collect { }
        }
    }
}