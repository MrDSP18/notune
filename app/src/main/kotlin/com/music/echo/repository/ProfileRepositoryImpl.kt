package com.music.echo.repository

import com.music.echo.notune.identity.NotuneAccountRepository
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.UserPresenceEntity
import echo.music.iad1tya.models.*
import echo.music.iad1tya.repository.ProfileRepository
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val accountRepository: NotuneAccountRepository,
    private val database: MusicDatabase
) : ProfileRepository {

    override fun getMyProfile(): Flow<SocialUser?> = accountRepository.account.map { account ->
        SocialUser(
            id = account.userId,
            username = account.displayName.lowercase().replace(" ", "_"),
            displayName = account.displayName,
            avatarUrl = account.avatarUrl,
            bio = "NØTUNE Enthusiast",
            friendshipStatus = FriendshipStatus.ACCEPTED // My own profile
        )
    }

    override suspend fun updateProfile(
        displayName: String?,
        bio: String?,
        avatarUrl: String?,
        isPrivate: Boolean?
    ): Result<Unit> = runCatching {
        // Implementation would call backend and update local state
        // For now, we update the local account repo
        displayName?.let {
            accountRepository.signInWithProvider(
                userId = accountRepository.account.value.userId,
                displayName = it,
                email = accountRepository.account.value.email,
                avatarUrl = avatarUrl ?: accountRepository.account.value.avatarUrl,
                provider = accountRepository.account.value.provider
            )
        }
    }

    override fun getProfile(userId: String): Flow<SocialUser?> {
        // For now, just a mock/stub that returns something if it's the current user
        return getMyProfile().map { if (it?.id == userId) it else null }
    }

    override fun getMyPresence(): Flow<UserPresence?> {
        return flowOf(
            UserPresence(
                userId = accountRepository.account.value.userId,
                state = PresenceState.ONLINE,
                lastActive = LocalDateTime.now()
            )
        )
    }

    override suspend fun updatePresence(state: PresenceState): Result<Unit> = runCatching {
        // Logic to update presence in backend/local DB
    }
}
