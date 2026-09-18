package com.music.echo.repository

import com.music.echo.notune.identity.NotuneAccountRepository
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.*
import echo.music.iad1tya.models.*
import echo.music.iad1tya.repository.SocialRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialRepositoryImpl @Inject constructor(
    private val database: MusicDatabase,
    private val accountRepository: NotuneAccountRepository,
    @echo.music.iad1tya.di.ApplicationScope private val scope: CoroutineScope
) : SocialRepository {

    override fun getFriends(): Flow<List<SocialUser>> = database.socialDao.getFriends().map { entities ->
        entities.map { entity: FriendshipEntity ->
            val userId = if (entity.user1Id == accountRepository.account.value.userId) entity.user2Id else entity.user1Id
            SocialUser(
                id = userId,
                username = "user_$userId",
                displayName = "User $userId",
                avatarUrl = null,
                bio = null,
                friendshipStatus = FriendshipStatus.ACCEPTED
            )
        }
    }

    override fun getFriendRequests(): Flow<List<SocialUser>> = database.socialDao.getFriendRequests().map { entities ->
        entities.map { entity: FriendshipEntity ->
             SocialUser(
                id = entity.user1Id,
                username = "requester_${entity.user1Id}",
                displayName = "Requester ${entity.user1Id}",
                avatarUrl = null,
                bio = null,
                friendshipStatus = FriendshipStatus.PENDING_RECEIVED
            )
        }
    }

    override fun getSuggestedFriends(): Flow<List<SocialUser>> = flowOf(emptyList())

    override suspend fun sendFriendRequest(userId: String): Result<Unit> = runCatching {
        val myId = accountRepository.account.value.userId
        val friendship = FriendshipEntity(
            id = "${myId}_$userId",
            user1Id = myId,
            user2Id = userId,
            status = "PENDING"
        )
        database.socialDao.insertFriendship(friendship)
        // Queue outbox action
        database.socialDao.insertPendingAction(
            PendingSocialActionEntity(
                actionType = "SEND_REQUEST",
                targetId = userId
            )
        )
    }

    override suspend fun acceptFriendRequest(userId: String): Result<Unit> = runCatching {
        val myId = accountRepository.account.value.userId
        val friendship = FriendshipEntity(
            id = "${userId}_$myId",
            user1Id = userId,
            user2Id = myId,
            status = "ACCEPTED"
        )
        database.socialDao.insertFriendship(friendship)
        database.socialDao.insertPendingAction(
            PendingSocialActionEntity(
                actionType = "ACCEPT_REQUEST",
                targetId = userId
            )
        )
    }

    override suspend fun rejectFriendRequest(userId: String): Result<Unit> = runCatching {
        val myId = accountRepository.account.value.userId
        database.socialDao.deleteFriendship(myId, userId)
        database.socialDao.insertPendingAction(
            PendingSocialActionEntity(
                actionType = "REJECT_REQUEST",
                targetId = userId
            )
        )
    }

    override suspend fun removeFriend(userId: String): Result<Unit> = runCatching {
        val myId = accountRepository.account.value.userId
        database.socialDao.deleteFriendship(myId, userId)
        database.socialDao.insertPendingAction(
            PendingSocialActionEntity(
                actionType = "REMOVE_FRIEND",
                targetId = userId
            )
        )
    }

    override suspend fun blockUser(userId: String): Result<Unit> = runCatching {
        // Implementation for blocking
    }

    override fun getFeed(): Flow<List<SocialPost>> = database.socialDao.getFeed().map { entities ->
        entities.map { entity: SocialPostEntity ->
            SocialPost(
                id = entity.id,
                author = SocialUser(entity.authorId, entity.authorName, entity.authorName, entity.authorAvatarUrl, null),
                type = PostType.valueOf(entity.postType),
                content = PostContent.Mood("🎵", "Vibing"),
                caption = entity.caption,
                likesCount = entity.likesCount,
                repostsCount = entity.repostsCount,
                createdAt = entity.createdAt
            )
        }
    }

    override suspend fun createPost(type: PostType, content: PostContent, caption: String): Result<Unit> = runCatching {
        val myAccount = accountRepository.account.value
        val post = SocialPostEntity(
            id = java.util.UUID.randomUUID().toString(),
            authorId = myAccount.userId,
            authorName = myAccount.displayName,
            authorAvatarUrl = myAccount.avatarUrl,
            postType = type.name,
            caption = caption
        )
        database.socialDao.insertPost(post)
        database.socialDao.insertPendingAction(
            PendingSocialActionEntity(
                actionType = "CREATE_POST",
                targetId = post.id,
                payload = caption
            )
        )
    }

    override suspend fun likePost(postId: String): Result<Unit> = runCatching {
        database.socialDao.insertPendingAction(
            PendingSocialActionEntity(
                actionType = "LIKE_POST",
                targetId = postId
            )
        )
    }

    override suspend fun unlikePost(postId: String): Result<Unit> = runCatching {
        // implementation
    }
}
