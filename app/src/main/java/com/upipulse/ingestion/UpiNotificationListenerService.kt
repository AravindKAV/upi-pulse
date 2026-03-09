package com.upipulse.ingestion

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.os.bundleOf
import com.upipulse.ingestion.parser.IngestPayload
import com.upipulse.ingestion.parser.PayloadType
import com.upipulse.work.TransactionIngestWorker
import java.time.Instant

class UpiNotificationListenerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        if (!SUPPORTED_PACKAGES.contains(packageName)) return
        val extras = sbn.notification.extras ?: bundleOf()
        val text = listOf(
            extras.getCharSequence("android.text"),
            extras.getCharSequence("android.bigText"),
            extras.getCharSequence("android.subText"),
            extras.getCharSequence("android.infoText")
        ).joinToString("\n") { it?.toString().orEmpty() }.trim()
        if (text.isEmpty()) return
        val payload = IngestPayload(
            rawText = text,
            sourcePackage = packageName,
            sender = extras.getString("android.title"),
            postedAt = Instant.ofEpochMilli(sbn.postTime),
            type = PayloadType.NOTIFICATION
        )
        TransactionIngestWorker.enqueue(applicationContext, payload)
    }

    companion object {
        private val SUPPORTED_PACKAGES = setOf(
            "com.google.android.apps.nbu.paisa.user", // Google Pay
            "com.phonepe.app",
            "net.one97.paytm",
            "in.org.npci.upiapp",
            "in.amazon.mShop.android.shopping",
            "com.amazon.pay",
            "com.truecaller"
        )
    }
}