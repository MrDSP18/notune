package com.music.echo.notune.sync

import android.content.Context
import com.music.echo.notune.privacy.security.SecureStorageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.models.AppEvent
import echo.music.iad1tya.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
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

    private fun saveOutboxToDisk() {
        try {
            val list = synchronized(pendingSyncQueue) { pendingSyncQueue.toList() }
            val array = JSONArray()
            for (item in list) {
                val obj = JSONObject().apply {
                    put("eventId", item.eventId)
                    put("eventType", item.eventType)
                    put("payloadJson", item.payloadJson)
                    put("timestamp", item.timestamp)
                }
                array.put(obj)
            }
            outboxFile.writeText(array.toString(), Charsets.UTF_8)
        } catch (e: Exception) {
            Timber.e(e, "Failed to persist sync outbox to disk")
        }
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
    }

    /**
     * Attempts to push queued outbox events to the backend REST API if online.
     * Retains queue locally if offline or server is unreachable.
     */
    fun triggerPendingSync() {
        scope.launch {
            val sessionToken = secureStorageManager.getSecret("user_session_token")

            val eventsToSync = synchronized(pendingSyncQueue) {
                pendingSyncQueue.toList()
            }

            if (eventsToSync.isEmpty()) {
                _syncState.value = SyncStatus.IDLE
                return@launch
            }

            _syncState.value = SyncStatus.SYNCING
            try {
                // Network HTTP dispatch attempt to NØTUNE Cloud Backend API
                val backendUrlStr = "https://notune.dharansundarapandiyan24.workers.dev/api/v1/sync"
                val url = URL(backendUrlStr)
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 3000
                    readTimeout = 3000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    if (sessionToken != null) {
                        setRequestProperty("Authorization", "Bearer $sessionToken")
                    }
                }

                // Construct complete batch payload with serialized events
                val eventsArray = JSONArray()
                for (event in eventsToSync) {
                    val eventObj = JSONObject().apply {
                        put("eventId", event.eventId)
                        put("eventType", event.eventType)
                        put("payload", event.payloadJson)
                        put("timestamp", event.timestamp)
                    }
                    eventsArray.put(eventObj)
                }

                val jsonBody = JSONObject().apply {
                    put("batchId", java.util.UUID.randomUUID().toString())
                    put("eventsCount", eventsToSync.size)
                    put("events", eventsArray)
                    put("clientTimestamp", System.currentTimeMillis())
                }.toString()

                connection.outputStream.use { os ->
                    os.write(jsonBody.toByteArray(Charsets.UTF_8))
                }

                val responseCode = connection.responseCode
                if (responseCode in 200..299) {
                    synchronized(pendingSyncQueue) {
                        pendingSyncQueue.removeAll(eventsToSync)
                        _pendingQueueCount.value = pendingSyncQueue.size
                    }
                    saveOutboxToDisk()
                    _syncState.value = SyncStatus.IDLE
                    Timber.i("Successfully synced %d events to NØTUNE Cloud API.", eventsToSync.size)
                } else {
                    _syncState.value = SyncStatus.OFFLINE
                    Timber.w("Server returned response code %d. Retaining events in local outbox queue.", responseCode)
                }
            } catch (e: Exception) {
                _syncState.value = SyncStatus.OFFLINE
                Timber.w("Network connection unavailable. Retaining %d events in local outbox queue.", eventsToSync.size)
            }
        }
    }
}
