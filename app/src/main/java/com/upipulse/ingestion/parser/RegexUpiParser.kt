package com.upipulse.ingestion.parser

import com.upipulse.domain.model.Merchant
import com.upipulse.domain.model.Transaction
import com.upipulse.domain.model.TransactionDirection
import com.upipulse.domain.model.TransactionSource
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegexUpiParser @Inject constructor() : TransactionPayloadParser {

    private val debitRegex = Regex(
        pattern = "(?i)(?:rs\.?|inr)\\s*([0-9,.]+).*?(?:paid to|debited(?: to)?|spent at)\\s*([A-Za-z0-9 .@_-]+).*?(?:utr|ref(?:erence)? no\.?)\\s*([A-Za-z0-9]+)",
        option = RegexOption.MULTILINE
    )

    private val creditRegex = Regex(
        pattern = "(?i)(?:rs\.?|inr)\\s*([0-9,.]+).*?(?:received from|credited(?: from)?|cashback from)\\s*([A-Za-z0-9 .@_-]+).*?(?:utr|ref(?:erence)? no\.?)\\s*([A-Za-z0-9]+)",
        option = RegexOption.MULTILINE
    )

    override fun canParse(payload: IngestPayload): Boolean {
        val candidates = listOf("upi", "cashback", "gpay", "phonepe", "paytm", "amazon pay", "bhim")
        val body = payload.rawText.lowercase()
        return candidates.any { it in body }
    }

    override fun parse(payload: IngestPayload): Transaction? {
        val body = payload.rawText
        val debitMatch = debitRegex.find(body)
        val creditMatch = creditRegex.find(body)
        val match = debitMatch ?: creditMatch ?: return null
        val amount = match.groupValues[1].replace(",", "").toDoubleOrNull() ?: return null
        val merchantName = match.groupValues[2].trim()
        val referenceId = match.groupValues.getOrNull(3)?.ifBlank { UUID.randomUUID().toString() }
            ?: UUID.randomUUID().toString()
        val direction = if (debitMatch != null) TransactionDirection.DEBIT else TransactionDirection.CREDIT
        val category = inferCategory(merchantName, direction)
        return Transaction(
            referenceId = referenceId,
            merchant = Merchant(id = merchantName.lowercase(), name = merchantName),
            category = category,
            amount = amount,
            currency = "INR",
            direction = direction,
            timestamp = payload.postedAt,
            source = if (payload.type == PayloadType.SMS) TransactionSource.SMS else TransactionSource.NOTIFICATION,
            rawDescription = payload.rawText,
            metadata = buildMap {
                payload.sender?.let { put("sender", it) }
                payload.sourcePackage?.let { put("sourcePackage", it) }
            }
        )
    }

    private fun inferCategory(merchantName: String, direction: TransactionDirection): String {
        val normalized = merchantName.lowercase()
        if (direction == TransactionDirection.CREDIT) return "Cashback"
        return when {
            listOf("swiggy", "zomato", "eat", "restaurant").any(normalized::contains) -> "Food"
            listOf("uber", "ola", "rapido").any(normalized::contains) -> "Transport"
            listOf("amazon", "flipkart", "myntra").any(normalized::contains) -> "Shopping"
            listOf("electric", "power", "gas", "bharat").any(normalized::contains) -> "Utilities"
            else -> "Others"
        }
    }
}