package echo.music.iad1tya.db.daos

import androidx.room.*
import echo.music.iad1tya.db.entities.RoomEntity
import echo.music.iad1tya.db.entities.RoomMemberEntity
import echo.music.iad1tya.db.entities.RoomMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {

    @Query("SELECT * FROM room_table ORDER BY createdAt DESC")
    fun getAllRooms(): Flow<List<RoomEntity>>

    @Query("SELECT * FROM room_table WHERE roomType = 'PUBLIC' ORDER BY createdAt DESC")
    fun getPublicRooms(): Flow<List<RoomEntity>>

    @Query("SELECT * FROM room_table WHERE id = :roomId LIMIT 1")
    fun getRoomById(roomId: String): Flow<RoomEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: RoomEntity)

    @Query("DELETE FROM room_table WHERE id = :roomId")
    suspend fun deleteRoom(roomId: String)

    // Messages
    @Query("SELECT * FROM room_message_table WHERE roomId = :roomId ORDER BY timestamp ASC")
    fun getMessagesForRoom(roomId: String): Flow<List<RoomMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: RoomMessageEntity)

    // Members
    @Query("SELECT * FROM room_member_table WHERE roomId = :roomId ORDER BY joinedAt ASC")
    fun getMembersForRoom(roomId: String): Flow<List<RoomMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: RoomMemberEntity)

    @Query("DELETE FROM room_member_table WHERE roomId = :roomId AND userId = :userId")
    suspend fun removeMember(roomId: String, userId: String)
}
