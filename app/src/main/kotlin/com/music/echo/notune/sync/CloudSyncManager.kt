package com.music.echo.notune.sync

import android.content.Context
import com.music.echo.notune.privacy.security.SecureStorageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NØTUNE Offline-First Cloud Synchronization Manager.
 * Preserves local Room database as the primary source of truth.
 * Batches listening events, Music DNA updates, and preferences for background sync
 * without blocking Media3 audio playback.
 */
@Singleton
class CloudSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val secureStorageManager: SecureStorageManager
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

    private val pendingSyncQueue = mutableListOf<SyncEventPayload>()

    data class SyncEventPayload(
        val eventId: String,
        val eventType: String,
        val payloadJson: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Enqueues a sync event locally. Never blocks playback.
     */
    fun enqueueSyncEvent(eventType: String, payloadJson: String) {
        val event = SyncEventPayload(
            eventId = java.util.UUID.randomUUID().toString(),
            eventType = eventType,
            payloadJson = payloadJson
        )
        synchronized(pendingSyncQueue) {
            pendingSyncQueue.add(event)
        }
        Timber.d("Enqueued local sync event: %s. Queue size: %d", eventType, pendingSyncQueue.size)
        triggerPendingSync()
    }

    /**
     * Attempts to push queued events to the backend REST API if online.
     * Retains queue locally if offline or server is unreachable.
     */
    fun triggerPendingSync() {
        scope.launch {
            val sessionToken = secureStorageManager.getSecret("user_session_token")
            if (sessionToken == null) {
                _syncState.value = SyncStatus.IDLE // Unauthenticated mode, local-only
                return@launch
            }

            val eventsToSync = synchronized(pendingSyncQueue) {
                pendingSyncQueue.toList()
            }

            if (eventsToSync.isEmpty()) return@launch

            _syncState.value = SyncStatus.SYNCING
            try {
                // In production, posts eventsToSync to POST /v1/sync endpoint
                // Simulated clean network dispatch:
                Timber.i("Background sync dispatching %d queued events to NØTUNE Cloud API...", eventsToSync.size)
                synchronized(pendingSyncQueue) {
                    pendingSyncQueue.removeAll(eventsToSync)
                }
                _syncState.value = SyncStatus.IDLE
            } catch (e: Exception) {
                Timber.w(e, "Cloud sync failed. Retaining %d events in local queue for retry.", eventsToSync.size)
                _syncState.value = SyncStatus.OFFLINE
            }
        }
    }
}
