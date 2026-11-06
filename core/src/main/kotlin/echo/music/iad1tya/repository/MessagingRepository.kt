package echo.music.iad1tya.repository

import echo.music.iad1tya.models.*
import kotlinx.coroutines.flow.Flow

interface MessagingRepository {
    fun getConversations(): Flow<List<SocialUser>>
    fun getMessages(userId: String): Flow<List<Message>>
    
    suspend fun sendMessage(
        userId: String,
        type: MessageType,
        content: String? = null,
        mediaMetadata: MediaMetadata? = null,
        momentTimestamp: Long? = null
    ): Result<Unit>
    
    suspend fun markAsRead(userId: String): Result<Unit>
    suspend fun deleteMessage(messageId: String): Result<Unit>
}
