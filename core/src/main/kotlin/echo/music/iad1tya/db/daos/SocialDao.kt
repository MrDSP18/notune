package echo.music.iad1tya.db.daos

import androidx.room.*
import echo.music.iad1tya.db.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialDao {
    @Query("SELECT * FROM social_user")
    fun getAllUsers(): Flow<List<SocialUserEntity>>

    @Query("SELECT * FROM social_user WHERE id = :userId")
    fun getUserById(userId: String): Flow<SocialUserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: SocialUserEntity)

    @Query("SELECT * FROM friendship WHERE status = 'ACCEPTED'")
    fun getFriends(): Flow<List<FriendshipEntity>>

    @Query("SELECT * FROM friendship WHERE status = 'PENDING'")
    fun getFriendRequests(): Flow<List<FriendshipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendship(friendship: FriendshipEntity)

    @Query("DELETE FROM friendship WHERE (user1Id = :u1 AND user2Id = :u2) OR (user1Id = :u2 AND user2Id = :u1)")
    suspend fun deleteFriendship(u1: String, u2: String)

    @Query("SELECT * FROM social_post_v2 ORDER BY createdAt DESC")
    fun getFeed(): Flow<List<SocialPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: SocialPostEntity)

    @Query("SELECT * FROM music_message WHERE senderId = :userId OR receiverId = :userId ORDER BY createdAt ASC")
    fun getMessagesWithUser(userId: String): Flow<List<MusicMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MusicMessageEntity)

    @Query("SELECT * FROM pending_social_action ORDER BY createdAt ASC")
    fun getPendingActions(): Flow<List<PendingSocialActionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingAction(action: PendingSocialActionEntity)

    @Delete
    suspend fun deletePendingAction(action: PendingSocialActionEntity)
}
