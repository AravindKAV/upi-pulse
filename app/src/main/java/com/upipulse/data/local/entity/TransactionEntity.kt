package com.upipulse.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.upipulse.domain.model.TransactionDirection
import com.upipulse.domain.model.TransactionSource
import java.time.Instant

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val referenceId: String,
    val merchantName: String,
    val merchantId: String?,
    val category: String,
    val amount: Double,
    val currency: String,
    val direction: TransactionDirection,
    val timestamp: Instant,
    val source: TransactionSource,
    val rawDescription: String,
    val metadata: Map<String, String> = emptyMap(),
    val createdAt: Instant = Instant.now()
)