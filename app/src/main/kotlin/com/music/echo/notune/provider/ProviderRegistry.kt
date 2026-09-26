package echo.music.iad1tya.notune.provider

import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import echo.music.iad1tya.repository.LocalMediaRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class LocalMediaStoreProvider @Inject constructor(
    private val localMediaRepository: LocalMediaRepository
) : MusicProvider {
    override val providerId: String = "local_mediastore"
    override val providerName: String = "User Library"

    override suspend fun isAvailable(): Boolean = true

    override suspend fun search(query: String): List<UnifiedTrack> {
        val songs = localMediaRepository.getSongs().first()
        return songs
            .filter { it.title.contains(query, ignoreCase = true) || it.artists.any { a -> a.contains(query, ignoreCase = true) } }
            .map { song ->
                UnifiedTrack(
                    id = "local_${song.id}",
                    title = song.title,
                    artist = song.artists.joinToString(),
                    album = song.albumTitle,
                    durationSeconds = song.durationSeconds,
                    playbackType = PlaybackType.USER_LIBRARY,
                    providerName = providerName,
                    providerTrackId = song.id,
                    rights = TrackRights(isStreamable = true, isDownloadable = true)
                )
            }
    }

    override suspend fun getTrackDetails(trackId: String): UnifiedTrack? {
        val cleanId = trackId.removePrefix("local_")
        val song = localMediaRepository.getSongs().first().find { it.id == cleanId } ?: return null
        return UnifiedTrack(
            id = "local_${song.id}",
            title = song.title,
            artist = song.artists.joinToString(),
            album = song.albumTitle,
            durationSeconds = song.durationSeconds,
            playbackType = PlaybackType.USER_LIBRARY,
            providerName = providerName,
            providerTrackId = song.id,
            rights = TrackRights(isStreamable = true, isDownloadable = true)
        )
    }

    override suspend fun resolvePlaybackUri(trackId: String): Result<String> {
        val cleanId = trackId.removePrefix("local_")
        val song = localMediaRepository.getSongs().first().find { it.id == cleanId }
            ?: return Result.failure(Exception("Local track not found"))
        return Result.success("content://media/external/audio/media/$cleanId")
    }
}

@Singleton
class YouTubeInnerTubeProvider @Inject constructor() : MusicProvider {
    override val providerId: String = "innertube_api"
    override val providerName: String = "YouTube Music Engine"

    override suspend fun isAvailable(): Boolean = true

    override suspend fun search(query: String): List<UnifiedTrack> {
        val results = YouTube.search(query, YouTube.SearchFilter.FILTER_SONG).getOrNull()
        val songs = results?.items?.filterIsInstance<SongItem>() ?: emptyList()
        return songs.map { song ->
            UnifiedTrack(
                id = "yt_${song.id}",
                title = song.title,
                artist = song.artists.joinToString { it.name },
                album = song.album?.name,
                thumbnailUri = song.thumbnail,
                durationSeconds = song.duration ?: 0,
                playbackType = PlaybackType.AUTHORIZED_STREAM,
                providerName = providerName,
                providerTrackId = song.id,
                rights = TrackRights(isStreamable = true, isDownloadable = false)
            )
        }
    }

    override suspend fun getTrackDetails(trackId: String): UnifiedTrack? {
        val cleanId = trackId.removePrefix("yt_")
        val song = YouTube.search(cleanId, YouTube.SearchFilter.FILTER_SONG).getOrNull()?.items?.firstOrNull() as? SongItem ?: return null
        return UnifiedTrack(
            id = "yt_${song.id}",
            title = song.title,
            artist = song.artists.joinToString { it.name },
            album = song.album?.name,
            thumbnailUri = song.thumbnail,
            durationSeconds = song.duration ?: 0,
            playbackType = PlaybackType.AUTHORIZED_STREAM,
            providerName = providerName,
            providerTrackId = song.id,
            rights = TrackRights(isStreamable = true, isDownloadable = false)
        )
    }

    override suspend fun resolvePlaybackUri(trackId: String): Result<String> {
        val cleanId = trackId.removePrefix("yt_")
        return YouTube.player(cleanId, client = com.music.innertube.models.YouTubeClient.ANDROID_VR_1_65_10).mapCatching { playerResponse ->
            playerResponse.streamingData?.adaptiveFormats?.firstOrNull()?.url
                ?: throw Exception("Audio stream URL not found")
        }
    }
}

@Singleton
class ProviderRegistry @Inject constructor(
    private val localProvider: LocalMediaStoreProvider,
    private val ytProvider: YouTubeInnerTubeProvider
) {
    private val providers = listOf<MusicProvider>(localProvider, ytProvider)

    suspend fun searchUnifiedCatalog(query: String): List<UnifiedTrack> {
        val combined = mutableListOf<UnifiedTrack>()
        providers.forEach { provider ->
            try {
                if (provider.isAvailable()) {
                    combined.addAll(provider.search(query))
                }
            } catch (e: Exception) {
                Timber.w("Provider ${provider.providerId} search failed: ${e.message}")
            }
        }
        // Deduplicate tracks by normalized title + artist
        return combined.distinctBy { "${it.title.lowercase()}_${it.artist.lowercase()}" }
    }

    fun getProvider(providerId: String): MusicProvider? {
        return providers.find { it.providerId == providerId }
    }
}
