package com.upipulse.work

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.upipulse.R
import com.upipulse.ingestion.parser.IngestPayload
import com.upipulse.ingestion.parser.PayloadType
import java.time.Instant

class TransactionIngestService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val payload = intent?.let { deserializePayload(it) }
        if (payload != null) {
            TransactionIngestWorker.enqueue(applicationContext, payload)
        }
        startForeground(NOTIFICATION_ID, buildNotification())
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf(startId)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val channelId = ensureChannel()
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Processing UPI notifications")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    private fun ensureChannel(): String {
        val channelId = "upi_ingest"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, "UPI ingestion", NotificationManager.IMPORTANCE_MIN)
        notificationManager.createNotificationChannel(channel)
        return channelId
    }

    private fun deserializePayload(intent: Intent): IngestPayload? {
        val body = intent.getStringExtra(EXTRA_BODY) ?: return null
        val sourcePackage = intent.getStringExtra(EXTRA_PACKAGE)
        val sender = intent.getStringExtra(EXTRA_SENDER)
        val postedAt = intent.getLongExtra(EXTRA_POSTED_AT, 0L).takeIf { it > 0 }?.let { Instant.ofEpochMilli(it) }
            ?: Instant.now()
        val type = intent.getStringExtra(EXTRA_TYPE)?.let { PayloadType.valueOf(it) } ?: PayloadType.SMS
        return IngestPayload(body, sourcePackage, sender, postedAt, type)
    }

    companion object {
        private const val EXTRA_BODY = "body"
        private const val EXTRA_PACKAGE = "package"
        private const val EXTRA_SENDER = "sender"
        private const val EXTRA_POSTED_AT = "postedAt"
        private const val EXTRA_TYPE = "type"
        private const val NOTIFICATION_ID = 42

        fun start(context: Context, payload: IngestPayload) {
            val intent = Intent(context, TransactionIngestService::class.java).apply {
                putExtra(EXTRA_BODY, payload.rawText)
                putExtra(EXTRA_PACKAGE, payload.sourcePackage)
                putExtra(EXTRA_SENDER, payload.sender)
                putExtra(EXTRA_POSTED_AT, payload.postedAt.toEpochMilli())
                putExtra(EXTRA_TYPE, payload.type.name)
            }
            context.startForegroundService(intent)
        }
    }
}