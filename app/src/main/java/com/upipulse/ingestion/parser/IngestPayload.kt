package com.upipulse.ingestion.parser

import java.time.Instant

data class IngestPayload(
    val rawText: String,
    val sourcePackage: String?,
    val sender: String?,
    val postedAt: Instant,
    val type: PayloadType
)

enum class PayloadType { SMS, NOTIFICATION }