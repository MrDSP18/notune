package echo.music.iad1tya.notune.flow

import echo.music.iad1tya.constants.ArtistSongSortType
import echo.music.iad1tya.constants.SongSortType
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.models.MediaMetadata
import echo.music.iad1tya.models.toMediaMetadata
import echo.music.iad1tya.notune.MusicDnaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

import com.music.echo.notune.personalization.repository.TasteProfileRepository

@Singleton
class FlowCandidateGenerator @Inject constructor(
    private val database: MusicDatabase,
    private val musicDnaRepository: MusicDnaRepository,
    private val tasteProfileRepository: TasteProfileRepository
) {

    suspend fun generateCandidates(
        currentTrack: MediaMetadata?,
        mode: FlowMode,
        contextMode: FlowContextMode,
        limit: Int = 50
    ): List<FlowCandidate> = withContext(Dispatchers.IO) {
        val candidateMap = mutableMapOf<String, FlowCandidate>()

        // 1. Current Artist / Related Track Candidates
        if (currentTrack != null) {
            try {
                val relatedSongs = database.relatedSongs(currentTrack.id)
                for (song in relatedSongs) {
                    val metadata = song.toMediaMetadata()
                    candidateMap[metadata.id] = FlowCandidate(
                        mediaMetadata = metadata,
                        candidateSource = "Related Track",
                        isLocal = true
                    )
                }
            } catch (_: Exception) {}

            // Same Artist Candidates
            try {
                val currentArtistId = currentTrack.artists.firstOrNull()?.id
                if (currentArtistId != null) {
                    val artistSongs = database.artistSongs(currentArtistId, ArtistSongSortType.CREATE_DATE, true).first()
                    for (song in artistSongs.take(10)) {
                        if (song.id != currentTrack.id) {
                            val metadata = song.toMediaMetadata()
                            candidateMap[metadata.id] = FlowCandidate(
                                mediaMetadata = metadata,
                                candidateSource = "Same Artist",
                                isLocal = true
                            )
                        }
                    }
                }
            } catch (_: Exception) {}
        }

        // 2. Favorites & Liked Songs
        try {
            val likedSongs = database.likedSongs(SongSortType.CREATE_DATE, true).first()
            for (song in likedSongs.take(25)) {
                val metadata = song.toMediaMetadata()
                if (!candidateMap.containsKey(metadata.id)) {
                    candidateMap[metadata.id] = FlowCandidate(
                        mediaMetadata = metadata,
                        candidateSource = "Favorite Track",
                        isLocal = true
                    )
                }
            }
        } catch (_: Exception) {}

        // 3. High Play-Count History Songs
        try {
            val topSongs = database.topSongs(40).first()
            for (song in topSongs) {
                val metadata = song.toMediaMetadata()
                if (!candidateMap.containsKey(metadata.id)) {
                    candidateMap[metadata.id] = FlowCandidate(
                        mediaMetadata = metadata,
                        candidateSource = "Listening History",
                        isLocal = true
                    )
                }
            }
        } catch (_: Exception) {}

        // 4. Top Artist Affinity Songs
        try {
            val topArtists = database.allArtistsByPlayTime().first().take(8)
            for (artistEntity in topArtists) {
                val songs = database.artistSongs(artistEntity.id, ArtistSongSortType.CREATE_DATE, true).first()
                for (song in songs.take(4)) {
                    val metadata = song.toMediaMetadata()
                    if (!candidateMap.containsKey(metadata.id)) {
                        candidateMap[metadata.id] = FlowCandidate(
                            mediaMetadata = metadata,
                            candidateSource = "Favorite Artist",
                            isLocal = true
                        )
                    }
                }
            }
        } catch (_: Exception) {}

        // 5. Onboarding Taste Profile Signals (for initial discovery & new users)
        try {
            val tasteProfile = tasteProfileRepository.getTasteProfileOnce()
            if (tasteProfile.favoriteArtists.isNotEmpty()) {
                val allArtists = database.allArtistsByPlayTime().first()
                for (selectedArtist in tasteProfile.favoriteArtists) {
                    val matchingArtist = allArtists.find { it.artist.name.equals(selectedArtist.name, ignoreCase = true) }
                    if (matchingArtist != null) {
                        val songs = database.artistSongs(matchingArtist.id, ArtistSongSortType.CREATE_DATE, true).first()
                        for (song in songs.take(5)) {
                            val metadata = song.toMediaMetadata()
                            if (!candidateMap.containsKey(metadata.id)) {
                                candidateMap[metadata.id] = FlowCandidate(
                                    mediaMetadata = metadata,
                                    candidateSource = "Onboarding Favorite Artist (${selectedArtist.name})",
                                    isLocal = true
                                )
                            }
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        candidateMap.values.toList().take(limit)
    }
}

