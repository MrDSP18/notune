package com.music.echo.notune.memory

import com.music.echo.notune.privacy.PrivateSessionManager
import echo.music.iad1tya.db.DatabaseDao
import kotlinx.coroutines.flow.first
import java.time.ZoneOffset
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

data class MemoryInsight(
    val id: String,
    val title: String,
    val description: String,
    val songId: String,
    val songTitle: String,
    val artistName: String,
    val icon: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Singleton
class NotuneMemoryManager @Inject constructor(
    private val databaseDao: DatabaseDao,
    private val privateSessionManager: PrivateSessionManager
) {

    suspend fun generateContextualMemories(): List<MemoryInsight> {
        if (privateSessionManager.isPrivateSession.value) {
            return emptyList()
        }

        val insights = mutableListOf<MemoryInsight>()
        val historyEvents = databaseDao.events().first()

        if (historyEvents.isEmpty()) return emptyList()

        // 1. Late Night Listening memory
        val lateNightPlays = historyEvents.filter { eventWithSong ->
            val timestamp = eventWithSong.event.timestamp.toInstant(ZoneOffset.UTC).toEpochMilli()
            val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            hour in 0..4
        }
        val topLateNight = lateNightPlays.groupBy { it.song.song.id }.maxByOrNull { it.value.size }
        if (topLateNight != null && topLateNight.value.size >= 3) {
            val sample = topLateNight.value.first().song
            insights.add(
                MemoryInsight(
                    id = "late_night_${sample.song.id}",
                    title = "Late Night Companion",
                    description = "You played this ${topLateNight.value.size} times during late night sessions.",
                    songId = sample.song.id,
                    songTitle = sample.song.title,
                    artistName = sample.artists.joinToString { it.name }.ifEmpty { "Unknown Artist" },
                    icon = "🌙"
                )
            )
        }

        // 2. Heavy Replay Memory
        val replayGroups = historyEvents.groupBy { it.song.song.id }.filter { it.value.size >= 5 }
        val topReplayed = replayGroups.maxByOrNull { it.value.size }
        if (topReplayed != null) {
            val sample = topReplayed.value.first().song
            insights.add(
                MemoryInsight(
                    id = "heavy_replay_${sample.song.id}",
                    title = "Recent Obsession",
                    description = "Played ${topReplayed.value.size} times recently. This track is on repeat!",
                    songId = sample.song.id,
                    songTitle = sample.song.title,
                    artistName = sample.artists.joinToString { it.name }.ifEmpty { "Unknown Artist" },
                    icon = "🔥"
                )
            )
        }

        return insights
    }
}
