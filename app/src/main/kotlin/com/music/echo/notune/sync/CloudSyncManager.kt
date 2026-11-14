package com.music.echo.notune.sync

import android.content.Context
import android.util.AtomicFile
import com.music.echo.notune.privacy.security.SecureStorageManager
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.models.AppEvent
import echo.music.iad1tya.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NØTUNE Offline-First Cloud Synchronization Manager.
 * Preserves local Room database as the primary source of truth.
 * Queues listening events, posts, likes, messages, and preferences in a persistent local outbox
 * for reliable background sync without blocking audio playback.
 */
@Singleton
class CloudSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val secureStorageManager: SecureStorageManager,
    private val eventRepository: EventRepository
) {
    enum class SyncStatus {
        IDLE,
        SYNCING,
        OFFLINE,
        ERROR
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private val _syncState = MutableStateFlow(SyncStatus.IDLE)
    val syncState: StateFlow<SyncStatus> = _syncState.asStateFlow()

    private val _pendingQueueCount = MutableStateFlow(0)
    val pendingQueueCount: StateFlow<Int> = _pendingQueueCount.asStateFlow()

    private val pendingSyncQueue = mutableListOf<SyncEventPayload>()
    private val syncMutex = Mutex()
    private val outboxFile by lazy { File(context.filesDir, "notune_sync_outbox.json") }

    data class SyncEventPayload(
        val eventId: String,
        val eventType: String,
        val payloadJson: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    init {
        synchronized(pendingSyncQueue) {
            pendingSyncQueue.addAll(loadOutboxFromDisk())
            _pendingQueueCount.value = pendingSyncQueue.size
        }
        observeAppEvents()
    }

    private fun loadOutboxFromDisk(): List<SyncEventPayload> {
        return try {
            if (!outboxFile.exists()) return emptyList()
            val content = outboxFile.readText(Charsets.UTF_8)
            if (content.isBlank()) return emptyList()
            val array = JSONArray(content)
            val list = mutableListOf<SyncEventPayload>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    SyncEventPayload(
                        eventId = obj.optString("eventId"),
                        eventType = obj.optString("eventType"),
                        payloadJson = obj.optString("payloadJson"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (e: Exception) {
            Timber.w(e, "Failed to load sync outbox from disk")
            emptyList()
        }
    }

    private fun persistOutbox(events: List<SyncEventPayload>): Boolean {
        return try {
            val array = JSONArray()
            for (item in events) {
                val obj = JSONObject().apply {
                    put("eventId", item.eventId)
                    put("eventType", item.eventType)
                    put("payloadJson", item.payloadJson)
                    put("timestamp", item.timestamp)
                }
                array.put(obj)
            }
            val atomicFile = AtomicFile(outboxFile)
            val stream = atomicFile.startWrite()
            try {
                stream.write(array.toString().toByteArray(Charsets.UTF_8))
                atomicFile.finishWrite(stream)
            } catch (e: Exception) {
                atomicFile.failWrite(stream)
                throw e
            }
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to persist sync outbox atomically")
            false
        }
    }

    private fun saveOutboxToDisk(): Boolean = synchronized(pendingSyncQueue) {
        persistOutbox(pendingSyncQueue.toList())
    }

    private fun observeAppEvents() {
        scope.launch {
            eventRepository.events.collect { event ->
                when (event) {
                    is AppEvent.SongLiked -> {
                        val payload = JSONObject().apply {
                            put("songId", event.song.id)
                            put("title", event.song.title)
                            put("action", "LIKE")
                        }.toString()
                        enqueueSyncEvent("SONG_FAVORITE_TOGGLED", payload)
                    }
                    is AppEvent.SongUnliked -> {
                        val payload = JSONObject().apply {
                            put("songId", event.song.id)
                            put("action", "UNLIKE")
                        }.toString()
                        enqueueSyncEvent("SONG_FAVORITE_TOGGLED", payload)
                    }
                    is AppEvent.PlaybackCompleted -> {
                        val payload = JSONObject().apply {
                            put("songId", event.song.id)
                            put("playTimeMs", event.playTimeMs)
                            put("action", "PLAYBACK_FINISHED")
                        }.toString()
                        enqueueSyncEvent("PLAYBACK_HISTORY_LOGGED", payload)
                    }
                    else -> { /* Ignore non-synced transient events */ }
                }
            }
        }
    }

    /**
     * Enqueues a sync event locally in the persistent outbox. Never blocks playback.
     */
    fun enqueueSyncEvent(eventType: String, payloadJson: String) {
        val event = SyncEventPayload(
            eventId = java.util.UUID.randomUUID().toString(),
            eventType = eventType,
            payloadJson = payloadJson
        )
        synchronized(pendingSyncQueue) {
            pendingSyncQueue.add(event)
            _pendingQueueCount.value = pendingSyncQueue.size
        }
        saveOutboxToDisk()
        Timber.d("Enqueued local sync event: %s. Outbox queue size: %d", eventType, pendingSyncQueue.size)
        triggerPendingSync()
        scheduleBackgroundSync()
    }

    /**
     * Schedules reliable background synchronization via WorkManager with network constraints.
     */
    fun scheduleBackgroundSync() {
        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val syncWork = OneTimeWorkRequestBuilder<CloudSyncWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "notune_cloud_sync_worker",
                ExistingWorkPolicy.KEEP,
                syncWork
            )
        } catch (e: Exception) {
            Timber.w(e, "Failed to schedule background WorkManager sync")
        }
    }

    /**
     * Attempts to push queued outbox events to the backend REST API if online.
     * Retains queue locally if offline or server is unreachable.
     */
    fun triggerPendingSync() {
        scope.launch {
            syncPendingEventsNow()
        }
    }

    /**
     * Synchronizes all currently pending outbox events against the NØTUNE Cloud Sync API.
     * Returns true if successfully transmitted and acknowledged by the server.
     */
    suspend fun syncPendingEventsNow(): Boolean = syncMutex.withLock {
        withContext(Dispatchers.IO) {
            val sessionToken = secureStorageManager.getSecret("user_session_token")
            val eventsToSync = synchronized(pendingSyncQueue) {
                pendingSyncQueue.toList()
            }

            if (eventsToSync.isEmpty()) {
                _syncState.value = SyncStatus.IDLE
                return@withContext true
            }

            _syncState.value = SyncStatus.SYNCING
            var connection: HttpURLConnection? = null
            try {
                val eventIds = eventsToSync.map { it.eventId }.sorted()
                val batchId = UUID.nameUUIDFromBytes(
                    eventIds.joinToString("|").toByteArray(Charsets.UTF_8)
                ).toString()
                val url = URL("https://notune.dharansundarapandiyan24.workers.dev/api/v1/sync")
                connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 4000
                    readTimeout = 4000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("X-Sync-Protocol-Version", "1.0")
                    setRequestProperty("X-Idempotency-Key", batchId)
                    if (sessionToken != null) {
                        setRequestProperty("Authorization", "Bearer $sessionToken")
                    }
                }

                val eventsArray = JSONArray()
                for (event in eventsToSync) {
                    eventsArray.put(JSONObject().apply {
                        put("eventId", event.eventId)
                        put("eventType", event.eventType)
                        put("payload", event.payloadJson)
                        put("timestamp", event.timestamp)
                    })
                }

                val jsonBody = JSONObject().apply {
                    put("batchId", batchId)
                    put("protocolVersion", "1.0")
                    put("eventsCount", eventsToSync.size)
                    put("events", eventsArray)
                    put("clientTimestamp", System.currentTimeMillis())
                }.toString()

                connection.outputStream.use { it.write(jsonBody.toByteArray(Charsets.UTF_8)) }
                val responseCode = connection.responseCode
                if (responseCode !in 200..299) {
                    _syncState.value = SyncStatus.OFFLINE
                    Timber.w("Sync endpoint returned HTTP %d; retaining local events", responseCode)
                    return@withContext false
                }

                val responseText = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val response = JSONObject(responseText)
                val acknowledged = response.optJSONArray("acknowledgedEventIds")
                    ?: response.optJSONArray("acceptedEventIds")
                if (acknowledged == null) {
                    _syncState.value = SyncStatus.ERROR
                    Timber.w("Sync endpoint returned success without explicit event acknowledgements")
                    return@withContext false
                }

                val acknowledgedIds = buildSet {
                    for (i in 0 until acknowledged.length()) {
                        val id = acknowledged.optString(i)
                        if (id.isNotBlank()) add(id)
                    }
                }
                val batchIds = eventsToSync.mapTo(mutableSetOf()) { it.eventId }
                val safeAcknowledgements = acknowledgedIds.intersect(batchIds)
                if (safeAcknowledgements.isEmpty()) {
                    _syncState.value = SyncStatus.ERROR
                    Timber.w("Sync endpoint acknowledged none of the submitted events")
                    return@withContext false
                }

                val persisted = synchronized(pendingSyncQueue) {
                    val remaining = pendingSyncQueue.filterNot { it.eventId in safeAcknowledgements }
                    if (!persistOutbox(remaining)) {
                        false
                    } else {
                        pendingSyncQueue.removeAll { it.eventId in safeAcknowledgements }
                        _pendingQueueCount.value = pendingSyncQueue.size
                        true
                    }
                }
                if (!persisted) {
                    _syncState.value = SyncStatus.ERROR
                    return@withContext false
                }

                if (pendingSyncQueue.isEmpty()) {
                    _syncState.value = SyncStatus.IDLE
                    Timber.i("Server acknowledged all %d submitted sync events", safeAcknowledgements.size)
                    true
                } else {
                    _syncState.value = SyncStatus.OFFLINE
                    Timber.w("Server acknowledged %d/%d events; retaining the remainder", safeAcknowledgements.size, eventsToSync.size)
                    false
                }
            } catch (e: Exception) {
                _syncState.value = SyncStatus.OFFLINE
                Timber.w("Sync attempt failed; retaining %d events in local outbox", eventsToSync.size)
                false
            } finally {
                connection?.disconnect()
            }
        }
    }
}
