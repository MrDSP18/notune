package com.music.echo.notune.rooms.sync

import echo.music.iad1tya.models.RoomConnectionState
import echo.music.iad1tya.repository.PlaybackRepository
import echo.music.iad1tya.repository.RoomRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class RoomSyncEngine @Inject constructor(
    private val roomRepository: RoomRepository,
    private val playbackRepository: PlaybackRepository,
    @echo.music.iad1tya.di.ApplicationScope private val scope: CoroutineScope
) {
    private companion object {
        const val DRIFT_THRESHOLD_MS = 1000L
        const val SYNC_INTERVAL_MS = 2000L
    }

    init {
        scope.launch {
            combine(
                roomRepository.currentRoom,
                roomRepository.connectionState
            ) { room, state -> room to state }.collectLatest { (room, state) ->
                if (room != null && state == RoomConnectionState.CONNECTED) {
                    runSyncLoop(room.roomId)
                }
            }
        }
    }

    private suspend fun runSyncLoop(roomId: String) {
        while (true) {
            val room = roomRepository.currentRoom.value ?: break
            if (room.roomId != roomId) break
            
            val remoteState = room.playbackState
            val localState = playbackRepository.playbackState.value
            
            // 1. Synchronize Track
            if (remoteState.currentTrackId != localState.currentSong?.id) {
                Timber.tag("RoomSync").d("Track mismatch. Remote: ${remoteState.currentTrackId}, Local: ${localState.currentSong?.id}")
                // Logic to trigger track change via PlaybackRepository
                // For now, we assume the room host or client logic handles track selection
            }
            
            // 2. Synchronize Play/Pause
            if (remoteState.isPlaying != localState.isPlaying) {
                Timber.tag("RoomSync").d("Play state mismatch. Remote: ${remoteState.isPlaying}")
                if (remoteState.isPlaying) playbackRepository.play() else playbackRepository.pause()
            }
            
            // 3. Synchronize Position (Drift Correction)
            if (remoteState.isPlaying) {
                val serverNow = System.currentTimeMillis() // This should be synced server time
                val timeSinceUpdate = serverNow - remoteState.lastUpdateServerTime
                val expectedPosition = remoteState.positionMs + timeSinceUpdate
                
                val drift = Math.abs(expectedPosition - localState.position)
                if (drift > DRIFT_THRESHOLD_MS) {
                    Timber.tag("RoomSync").d("High drift detected: ${drift}ms. Seeking to $expectedPosition")
                    playbackRepository.seekTo(expectedPosition)
                }
            }
            
            delay(SYNC_INTERVAL_MS)
        }
    }
}
