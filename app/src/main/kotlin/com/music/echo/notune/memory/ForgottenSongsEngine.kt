package com.music.echo.notune.memory

import echo.music.iad1tya.db.DatabaseDao
import echo.music.iad1tya.db.entities.Song
import kotlinx.coroutines.flow.first
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

data class ForgottenSongCandidate(
    val song: Song,
    val daysUnplayed: Long,
    val totalHistoricalPlays: Int
)

@Singleton
class ForgottenSongsEngine @Inject constructor(
    private val databaseDao: DatabaseDao
) {
    suspend fun getForgottenSongs(thresholdDays: Long = 90L, limit: Int = 15): List<ForgottenSongCandidate> {
        val historyEvents = databaseDao.events().first()
        val now = System.currentTimeMillis()
        val thresholdMillis = TimeUnit.DAYS.toMillis(thresholdDays)

        val songPlayStats = historyEvents.groupBy { it.song.song.id }

        val forgottenList = mutableListOf<ForgottenSongCandidate>()

        for ((_, events) in songPlayStats) {
            val totalPlays = events.size
            if (totalPlays < 3) continue // Only songs with meaningful affinity

            val lastPlayedTime = events.maxOfOrNull { it.event.timestamp.toInstant(ZoneOffset.UTC).toEpochMilli() } ?: 0L
            val elapsedMillis = now - lastPlayedTime

            if (elapsedMillis >= thresholdMillis) {
                val days = TimeUnit.MILLISECONDS.toDays(elapsedMillis)
                val sampleSong = events.first().song
                forgottenList.add(
                    ForgottenSongCandidate(
                        song = sampleSong,
                        daysUnplayed = days,
                        totalHistoricalPlays = totalPlays
                    )
                )
            }
        }

        return forgottenList.sortedByDescending { it.totalHistoricalPlays }.take(limit)
    }
}
