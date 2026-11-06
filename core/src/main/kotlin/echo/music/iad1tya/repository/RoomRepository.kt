package echo.music.iad1tya.repository

import echo.music.iad1tya.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface RoomRepository {
    val connectionState: StateFlow<RoomConnectionState>
    val currentRoom: StateFlow<Room?>
    val syncState: StateFlow<RoomSyncState>
    
    suspend fun createRoom(name: String, type: RoomType): Result<Room>
    suspend fun joinRoom(roomCode: String): Result<Room>
    suspend fun leaveRoom(): Result<Unit>
    
    suspend fun play()
    suspend fun pause()
    suspend fun seekTo(positionMs: Long)
    suspend fun next()
    suspend fun previous()
    
    suspend fun addToQueue(metadata: MediaMetadata)
    suspend fun removeFromQueue(itemId: String)
    suspend fun moveQueueItem(itemId: String, newIndex: Int)
    
    suspend fun sendChatMessage(message: String): Result<Unit>
    suspend fun sendReaction(reaction: String): Result<Unit>
}
