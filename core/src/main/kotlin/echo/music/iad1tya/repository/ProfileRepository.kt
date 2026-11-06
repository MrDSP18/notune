package echo.music.iad1tya.repository

import echo.music.iad1tya.models.*
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getMyProfile(): Flow<SocialUser?>
    suspend fun updateProfile(
        displayName: String?,
        bio: String?,
        avatarUrl: String?,
        isPrivate: Boolean?
    ): Result<Unit>
    
    fun getProfile(userId: String): Flow<SocialUser?>
    
    fun getMyPresence(): Flow<UserPresence?>
    suspend fun updatePresence(state: PresenceState): Result<Unit>
}
