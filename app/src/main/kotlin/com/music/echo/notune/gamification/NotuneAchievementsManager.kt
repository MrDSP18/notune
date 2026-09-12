package com.music.echo.notune.gamification

import echo.music.iad1tya.db.DatabaseDao
import kotlinx.coroutines.flow.first
import java.time.ZoneOffset
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean,
    val unlockedDateText: String? = null
)

@Singleton
class NotuneAchievementsManager @Inject constructor(
    private val databaseDao: DatabaseDao
) {
    suspend fun getAchievements(): List<AchievementBadge> {
        val historyEvents = databaseDao.events().first()
        val totalPlays = historyEvents.size
        val distinctArtists = historyEvents.flatMap { it.song.artists }.map { it.name }.distinct().size

        val lateNightCount = historyEvents.count { eventWithSong ->
            val timestamp = eventWithSong.event.timestamp.toInstant(ZoneOffset.UTC).toEpochMilli()
            val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
            cal.get(Calendar.HOUR_OF_DAY) in 0..4
        }

        return listOf(
            AchievementBadge(
                id = "night_owl",
                title = "Night Owl 🌙",
                description = "Played music after midnight 10+ times.",
                icon = "🌙",
                isUnlocked = lateNightCount >= 10
            ),
            AchievementBadge(
                id = "deep_listener",
                title = "Deep Listener 🎧",
                description = "Listened to over 50 songs in total.",
                icon = "🎧",
                isUnlocked = totalPlays >= 50
            ),
            AchievementBadge(
                id = "explorer",
                title = "Explorer 🧭",
                description = "Discovered 15+ different artists.",
                icon = "🧭",
                isUnlocked = distinctArtists >= 15
            ),
            AchievementBadge(
                id = "flow_rider",
                title = "FLOW Rider 🔥",
                description = "Let NØTUNE FLOW generate your continuous queue.",
                icon = "🔥",
                isUnlocked = totalPlays >= 5
            )
        )
    }
}
