package com.upipulse.ingestion.parser

import com.upipulse.domain.model.Transaction

interface TransactionPayloadParser {
    fun canParse(payload: IngestPayload): Boolean
    fun parse(payload: IngestPayload): Transaction?
}