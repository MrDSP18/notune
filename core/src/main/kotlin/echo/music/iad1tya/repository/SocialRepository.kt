package echo.music.iad1tya.repository

import echo.music.iad1tya.models.*
import kotlinx.coroutines.flow.Flow

interface SocialRepository {
    fun getFriends(): Flow<List<SocialUser>>
    fun getFriendRequests(): Flow<List<SocialUser>>
    fun getSuggestedFriends(): Flow<List<SocialUser>>
    
    suspend fun sendFriendRequest(userId: String): Result<Unit>
    suspend fun acceptFriendRequest(userId: String): Result<Unit>
    suspend fun rejectFriendRequest(userId: String): Result<Unit>
    suspend fun removeFriend(userId: String): Result<Unit>
    suspend fun blockUser(userId: String): Result<Unit>
    
    fun getFeed(): Flow<List<SocialPost>>
    suspend fun createPost(type: PostType, content: PostContent, caption: String): Result<Unit>
    suspend fun likePost(postId: String): Result<Unit>
    suspend fun unlikePost(postId: String): Result<Unit>
}
