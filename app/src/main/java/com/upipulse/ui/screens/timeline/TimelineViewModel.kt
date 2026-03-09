package com.upipulse.ui.screens.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upipulse.domain.model.Merchant
import com.upipulse.domain.model.Transaction
import com.upipulse.domain.model.TransactionDirection
import com.upipulse.domain.model.TransactionSource
import com.upipulse.domain.usecase.ObserveTransactionsUseCase
import com.upipulse.domain.usecase.UpsertTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface TimelineUiState {
    data object Loading : TimelineUiState
    data class Ready(val transactions: List<Transaction>) : TimelineUiState
    data class Error(val message: String) : TimelineUiState
}

sealed interface TimelineEvent {
    data object TransactionSaved : TimelineEvent
    data class Error(val message: String) : TimelineEvent
}

@HiltViewModel
class TimelineViewModel @Inject constructor(
    observeTransactionsUseCase: ObserveTransactionsUseCase,
    private val upsertTransactionUseCase: UpsertTransactionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<TimelineUiState>(TimelineUiState.Loading)
    val state: StateFlow<TimelineUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<TimelineEvent>()
    val events: SharedFlow<TimelineEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            observeTransactionsUseCase()
                .onEach { list -> _state.value = TimelineUiState.Ready(list) }
                .catch { throwable -> _state.value = TimelineUiState.Error(throwable.message.orEmpty()) }
                .collect { }
        }
    }

    fun addManualTransaction(input: ManualTransactionInput) {
        viewModelScope.launch {
            runCatching {
                val transaction = input.toTransaction()
                upsertTransactionUseCase(transaction)
                _events.emit(TimelineEvent.TransactionSaved)
            }.onFailure { throwable ->
                _events.emit(TimelineEvent.Error(throwable.message.orEmpty()))
            }
        }
    }
}

data class ManualTransactionInput(
    val merchant: String,
    val amount: Double,
    val category: String,
    val direction: TransactionDirection
) {
    fun toTransaction(): Transaction = Transaction(
        referenceId = "manual-${"%x".format(System.currentTimeMillis())}",
        merchant = Merchant(id = merchant.lowercase(), name = merchant.ifBlank { "Manual" }),
        category = category.ifBlank { "Others" },
        amount = amount,
        currency = "INR",
        direction = direction,
        timestamp = Instant.now(),
        source = TransactionSource.MANUAL,
        rawDescription = "Manual entry"
    )
}