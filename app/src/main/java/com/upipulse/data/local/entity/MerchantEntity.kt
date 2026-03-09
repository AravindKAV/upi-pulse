package com.upipulse.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "merchants")
data class MerchantEntity(
    @PrimaryKey val merchantId: String,
    val displayName: String,
    val upiHandle: String? = null,
    val categoryHint: String? = null
)