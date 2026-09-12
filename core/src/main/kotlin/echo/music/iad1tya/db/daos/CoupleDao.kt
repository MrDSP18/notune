
package echo.music.iad1tya.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import echo.music.iad1tya.db.entities.CoupleReactionEntity
import echo.music.iad1tya.db.entities.CoupleSessionEntity
import echo.music.iad1tya.db.entities.MemoryCapsuleEntity
import echo.music.iad1tya.db.entities.OurSongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoupleDao {

    // ─── Couple Session ───────────────────────────────────────────────────────
    @Query("SELECT * FROM couple_session WHERE isActive = 1 LIMIT 1")
    fun activeSession(): Flow<CoupleSessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: CoupleSessionEntity)

    @Update
    suspend fun updateSession(session: CoupleSessionEntity)

    @Query("UPDATE couple_session SET isActive = 0 WHERE id = :sessionId")
    suspend fun deactivateSession(sessionId: String)

    @Query("DELETE FROM couple_session WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String)

    // ─── Our Songs ────────────────────────────────────────────────────────────
    @Query("SELECT * FROM our_song WHERE sessionId = :sessionId ORDER BY addedAt DESC")
    fun ourSongs(sessionId: String): Flow<List<OurSongEntity>>

    @Query("SELECT * FROM our_song WHERE sessionId = :sessionId AND isPrimary = 1 LIMIT 1")
    fun primaryOurSong(sessionId: String): Flow<OurSongEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOurSong(song: OurSongEntity)

    @Query("DELETE FROM our_song WHERE id = :id")
    suspend fun removeOurSong(id: String)

    @Query("UPDATE our_song SET isPrimary = 0 WHERE sessionId = :sessionId")
    suspend fun clearPrimary(sessionId: String)

    @Query("UPDATE our_song SET isPrimary = 1 WHERE id = :id")
    suspend fun setPrimary(id: String)

    // ─── Memory Capsules ─────────────────────────────────────────────────────
    @Query("SELECT * FROM memory_capsule WHERE sessionId = :sessionId ORDER BY createdAt DESC")
    fun capsules(sessionId: String): Flow<List<MemoryCapsuleEntity>>

    @Query(
        """SELECT * FROM memory_capsule 
           WHERE sessionId = :sessionId 
             AND (unlockAt IS NULL OR unlockAt <= :now)
           ORDER BY createdAt DESC"""
    )
    fun openableCapsules(sessionId: String, now: java.time.LocalDateTime): Flow<List<MemoryCapsuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapsule(capsule: MemoryCapsuleEntity)

    @Query("UPDATE memory_capsule SET isOpened = 1, openedAt = :openedAt WHERE id = :id")
    suspend fun markOpened(id: String, openedAt: java.time.LocalDateTime)

    @Query("DELETE FROM memory_capsule WHERE id = :id")
    suspend fun deleteCapsule(id: String)

    // ─── Reactions ────────────────────────────────────────────────────────────
    @Query("SELECT * FROM couple_reaction WHERE sessionId = :sessionId AND songId = :songId ORDER BY reactedAt DESC")
    fun reactionsForSong(sessionId: String, songId: String): Flow<List<CoupleReactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReaction(reaction: CoupleReactionEntity)

    @Query("DELETE FROM couple_reaction WHERE id = :id")
    suspend fun deleteReaction(id: String)

    @Query("DELETE FROM couple_reaction WHERE sessionId = :sessionId AND songId = :songId AND isLocal = 1")
    suspend fun clearLocalReactions(sessionId: String, songId: String)
}
