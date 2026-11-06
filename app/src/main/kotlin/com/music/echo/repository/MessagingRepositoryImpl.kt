package com.music.echo.repository

import com.music.echo.notune.identity.NotuneAccountRepository
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.MusicMessageEntity
import echo.music.iad1tya.models.*
import echo.music.iad1tya.repository.MessagingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagingRepositoryImpl @Inject constructor(
    private val database: MusicDatabase,
    private val accountRepository: NotuneAccountRepository,
    @echo.music.iad1tya.di.ApplicationScope private val scope: CoroutineScope
) : MessagingRepository {

    override fun getConversations(): Flow<List<SocialUser>> = flowOf(emptyList())

    override fun getMessages(userId: String): Flow<List<Message>> = database.socialDao.getMessagesWithUser(userId).map { entities ->
        entities.map { entity: MusicMessageEntity ->
            Message(
                id = entity.id,
                senderId = entity.senderId,
                receiverId = entity.receiverId,
                type = MessageType.valueOf(entity.messageType),
                status = MessageStatus.SENT,
                content = entity.textContent,
                createdAt = entity.createdAt
            )
        }
    }

    override suspend fun sendMessage(
        userId: String,
        type: MessageType,
        content: String?,
        mediaMetadata: MediaMetadata?,
        momentTimestamp: Long?
    ): Result<Unit> = runCatching {
        val myId = accountRepository.account.value.userId
        val message = MusicMessageEntity(
            id = UUID.randomUUID().toString(),
            senderId = myId,
            receiverId = userId,
            messageType = type.name,
            textContent = content,
            songId = mediaMetadata?.id,
            songTitle = mediaMetadata?.title,
            artistName = mediaMetadata?.artists?.joinToString { it.name },
            thumbnailUrl = mediaMetadata?.thumbnailUrl,
            timestampMoment = momentTimestamp?.toString()
        )
        database.socialDao.insertMessage(message)
        // Also queue in outbox if needed
    }

    override suspend fun markAsRead(userId: String): Result<Unit> = runCatching {
        // update status in DB
    }

    override suspend fun deleteMessage(messageId: String): Result<Unit> = runCatching {
        // delete from DB
    }
}
