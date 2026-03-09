package com.upipulse.ingestion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import com.upipulse.ingestion.parser.IngestPayload
import com.upipulse.ingestion.parser.PayloadType
import com.upipulse.work.TransactionIngestWorker
import java.time.Instant

class SmsTransactionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val body = messages.joinToString(separator = "\n") { it.displayMessageBody }
        val sender = messages.firstOrNull()?.displayOriginatingAddress
        val timestamp = messages.mapNotNull { smsTimestamp(it) }.maxOrNull()?.let { Instant.ofEpochMilli(it) }
            ?: Instant.now()
        val payload = IngestPayload(
            rawText = body,
            sourcePackage = null,
            sender = sender,
            postedAt = timestamp,
            type = PayloadType.SMS
        )
        TransactionIngestWorker.enqueue(context, payload)
    }

    private fun smsTimestamp(message: SmsMessage): Long? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) message.timestampMillis else null
}