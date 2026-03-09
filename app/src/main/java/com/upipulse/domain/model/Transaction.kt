package com.upipulse.domain.model

import java.time.Instant

data class Transaction(
    val id: Long = 0L,
    val referenceId: String,
    val merchant: Merchant,
    val category: String,
    val amount: Double,
    val currency: String,
    val direction: TransactionDirection,
    val timestamp: Instant,
    val source: TransactionSource,
    val rawDescription: String,
    val metadata: Map<String, String> = emptyMap()
)

enum class TransactionDirection { DEBIT, CREDIT }

data class Merchant(
    val id: String?,
    val name: String,
    val upiHandle: String? = null
)

enum class TransactionSource {
    SMS,
    NOTIFICATION,
    MANUAL
}