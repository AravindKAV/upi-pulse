package com.upipulse.ui.screens.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upipulse.domain.model.Transaction
import com.upipulse.domain.usecase.ObserveTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TimelineUiState {
    data object Loading : TimelineUiState
    data class Ready(val transactions: List<Transaction>) : TimelineUiState
    data class Error(val message: String) : TimelineUiState
}

@HiltViewModel
class TimelineViewModel @Inject constructor(
    observeTransactionsUseCase: ObserveTransactionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<TimelineUiState>(TimelineUiState.Loading)
    val state: StateFlow<TimelineUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeTransactionsUseCase()
                .onEach { list -> _state.value = TimelineUiState.Ready(list) }
                .catch { throwable -> _state.value = TimelineUiState.Error(throwable.message.orEmpty()) }
                .collect { }
        }
    }
}