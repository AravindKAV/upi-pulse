package com.upipulse.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.upipulse.data.repository.TransactionRepository
import com.upipulse.ingestion.parser.IngestPayload
import com.upipulse.ingestion.parser.ParserRegistry
import com.upipulse.ingestion.parser.PayloadType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant

@HiltWorker
class TransactionIngestWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val parserRegistry: ParserRegistry,
    private val repository: TransactionRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val body = inputData.getString(KEY_BODY) ?: return Result.failure()
        val sourcePackage = inputData.getString(KEY_SOURCE_PACKAGE)
        val sender = inputData.getString(KEY_SENDER)
        val postedAt = inputData.getLong(KEY_POSTED_AT, 0L).takeIf { it > 0 }?.let { Instant.ofEpochMilli(it) }
            ?: Instant.now()
        val type = PayloadType.valueOf(inputData.getString(KEY_TYPE) ?: PayloadType.SMS.name)
        val payload = IngestPayload(
            rawText = body,
            sourcePackage = sourcePackage,
            sender = sender,
            postedAt = postedAt,
            type = type
        )
        val transaction = parserRegistry.parse(payload) ?: return Result.success()
        repository.upsertTransactions(listOf(transaction))
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "transaction_ingest"
        private const val KEY_BODY = "body"
        private const val KEY_SOURCE_PACKAGE = "package"
        private const val KEY_SENDER = "sender"
        private const val KEY_POSTED_AT = "postedAt"
        private const val KEY_TYPE = "type"

        fun enqueue(context: Context, payload: IngestPayload) {
            val data = workDataOf(
                KEY_BODY to payload.rawText,
                KEY_SOURCE_PACKAGE to payload.sourcePackage,
                KEY_SENDER to payload.sender,
                KEY_POSTED_AT to payload.postedAt.toEpochMilli(),
                KEY_TYPE to payload.type.name
            )
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .build()
            val request = OneTimeWorkRequestBuilder<TransactionIngestWorker>()
                .setConstraints(constraints)
                .setInputData(data)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME + payload.postedAt.toEpochMilli(), ExistingWorkPolicy.APPEND, request)
        }
    }
}