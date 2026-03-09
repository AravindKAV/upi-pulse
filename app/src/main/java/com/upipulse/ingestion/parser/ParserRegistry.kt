package com.upipulse.ingestion.parser

import com.upipulse.domain.model.Transaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParserRegistry @Inject constructor(
    private val parsers: Set<@JvmSuppressWildcards TransactionPayloadParser>
) {
    fun parse(payload: IngestPayload): Transaction? =
        parsers.firstOrNull { it.canParse(payload) }?.parse(payload)
}