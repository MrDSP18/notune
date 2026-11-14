package com.music.echo.notune.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

/**
 * Background WorkManager worker for guaranteed delivery of offline listening and library sync events.
 * Keeps retrying transient failures so pending events are not abandoned after a fixed attempt count.
 */
@HiltWorker
class CloudSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val cloudSyncManager: CloudSyncManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Timber.d("CloudSyncWorker: Starting scheduled sync run (attempt %d)", runAttemptCount + 1)
            val success = cloudSyncManager.syncPendingEventsNow()
            if (success) {
                Timber.i("CloudSyncWorker: Successfully synchronized pending outbox events.")
                Result.success()
            } else {
                Timber.w("CloudSyncWorker: Sync returned false. Scheduling retry.")
                Result.retry()
            }
        } catch (e: Exception) {
            Timber.e(e, "CloudSyncWorker: Exception during cloud sync attempt %d", runAttemptCount + 1)
            Result.retry()
        }
    }
}
