package com.music.echo.notune.intelligence.context

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import com.music.echo.notune.intelligence.memory.MemoryCategory
import com.music.echo.notune.intelligence.memory.UserMemoryEngine
import com.music.echo.notune.intelligence.personalization.TasteProfileStore
import com.music.echo.notune.intelligence.queue.AdaptiveQueueEngine
import com.music.echo.notune.intelligence.queue.QueueTrack
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

data class PlaybackStateSnapshot(
    val currentTrackId: String? = null,
    val currentTitle: String? = null,
    val currentArtist: String? = null,
    val currentAlbum: String? = null,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isPlaying: Boolean = false,
    val queueSize: Int = 0,
    val upcomingTracks: List<QueueTrack> = emptyList()
)

data class SessionStateSnapshot(
    val recentPlayedCount: Int = 0,
    val recentSkipsCount: Int = 0,
    val recentCompletionsCount: Int = 0,
    val sessionLanguage: String? = null,
    val sessionEnergy: Float = 0.5f,
    val discoveryTolerance: Float = 0.5f
)

data class UserStateSnapshot(
    val favoriteArtists: List<String> = emptyList(),
    val favoriteGenres: List<String> = emptyList(),
    val topLanguages: List<String> = emptyList(),
    val recentHistoryTrackIds: List<String> = emptyList()
)

data class EnvironmentStateSnapshot(
    val timeOfDay: TimeOfDay = TimeOfDay.AFTERNOON,
    val hourOfDay: Int = 12,
    val batteryPercentage: Float = 100f,
    val isOffline: Boolean = false,
    val networkType: String = "WIFI"
)

data class RoomStateSnapshot(
    val activeRoomId: String? = null,
    val isHost: Boolean = false,
    val memberCount: Int = 0
)

data class NotuneContextSnapshot(
    val playback: PlaybackStateSnapshot,
    val session: SessionStateSnapshot,
    val user: UserStateSnapshot,
    val environment: EnvironmentStateSnapshot,
    val room: RoomStateSnapshot,
    val timestampMs: Long = System.currentTimeMillis()
)

@Singleton
class UnifiedContextEngine @Inject constructor(
    @ApplicationContext private val context: Context?,
    private val adaptiveQueueEngine: AdaptiveQueueEngine,
    private val tasteProfileStore: TasteProfileStore,
    private val userMemoryEngine: UserMemoryEngine
) {

    fun captureSnapshot(
        currentTrack: QueueTrack? = null,
        isPlaying: Boolean = false,
        activeRoomId: String? = null
    ): NotuneContextSnapshot {
        val queueState = adaptiveQueueEngine.queueState.value
        val dna = tasteProfileStore.getDnaSnapshot()

        // Playback state
        val playback = PlaybackStateSnapshot(
            currentTrackId = currentTrack?.id ?: queueState.currentlyPlaying?.id,
            currentTitle = currentTrack?.title ?: queueState.currentlyPlaying?.title,
            currentArtist = currentTrack?.artistName ?: queueState.currentlyPlaying?.artistName,
            currentAlbum = null,
            positionMs = 0L,
            durationMs = 0L,
            isPlaying = isPlaying,
            queueSize = queueState.upcomingQueue.size,
            upcomingTracks = queueState.upcomingQueue
        )

        // Session state
        val memories = userMemoryEngine.getMemories()
        val session = SessionStateSnapshot(
            recentPlayedCount = queueState.playedHistory.size,
            recentSkipsCount = memories.count { it.category == MemoryCategory.SESSION && it.key.contains("skipped", ignoreCase = true) },
            recentCompletionsCount = memories.count { it.category == MemoryCategory.SESSION && it.key.contains("completed", ignoreCase = true) },
            sessionLanguage = dna.sessionTaste.currentLanguage,
            sessionEnergy = dna.sessionTaste.currentEnergy,
            discoveryTolerance = dna.discoveryProfile.explorationRate
        )

        // User state
        val user = UserStateSnapshot(
            favoriteArtists = dna.coreTaste.artists.keys.toList(),
            favoriteGenres = dna.coreTaste.genres.keys.toList(),
            topLanguages = dna.coreTaste.languages.keys.toList(),
            recentHistoryTrackIds = queueState.playedHistory.map { it.id }
        )

        // Environment state
        val now = LocalDateTime.now()
        val hour = now.hour
        val timeOfDay = when (hour) {
            in 5..11 -> TimeOfDay.MORNING
            in 12..16 -> TimeOfDay.AFTERNOON
            in 17..21 -> TimeOfDay.EVENING
            else -> TimeOfDay.NIGHT
        }

        val batteryIntent = try {
            context?.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        } catch (e: Exception) { null }

        val batteryPct = batteryIntent?.let { intent ->
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level >= 0 && scale > 0) (level * 100f / scale) else 100f
        } ?: 100f

        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val nw = cm?.activeNetwork
        val caps = cm?.getNetworkCapabilities(nw)
        val isOffline = caps == null || (!caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) && !caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        val networkType = when {
            isOffline -> "OFFLINE"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "CELLULAR"
            else -> "ETHERNET"
        }

        val environment = EnvironmentStateSnapshot(
            timeOfDay = timeOfDay,
            hourOfDay = hour,
            batteryPercentage = batteryPct,
            isOffline = isOffline,
            networkType = networkType
        )

        // Room state
        val room = RoomStateSnapshot(
            activeRoomId = activeRoomId,
            isHost = activeRoomId != null,
            memberCount = if (activeRoomId != null) 1 else 0
        )

        return NotuneContextSnapshot(
            playback = playback,
            session = session,
            user = user,
            environment = environment,
            room = room
        )
    }
}
