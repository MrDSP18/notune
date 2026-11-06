package com.music.echo.notune.intelligence.musicbrain

import echo.music.iad1tya.notune.provider.UnifiedTrack
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicIdentityEngine @Inject constructor() {

    fun createCanonicalIdentity(track: UnifiedTrack): MusicIdentity {
        val normTitle = MusicIdentity.normalizeString(track.title)
        val artists = track.artist.split(",", "&", "feat.", "ft.").map { it.trim() }.filter { it.isNotBlank() }
        
        val version = when {
            normTitle.contains("remix") -> "REMIX"
            normTitle.contains("live") -> "LIVE"
            normTitle.contains("cover") -> "COVER"
            normTitle.contains("acoustic") -> "ACOUSTIC"
            normTitle.contains("instrumental") -> "INSTRUMENTAL"
            else -> "ORIGINAL"
        }

        val providerMap = mapOf(track.providerName to track.providerTrackId)

        return MusicIdentity(
            trackId = track.id,
            canonicalTitle = track.title,
            normalizedTitle = normTitle,
            artistIds = artists.map { MusicIdentity.normalizeString(it) },
            canonicalArtists = artists,
            albumId = track.album?.let { MusicIdentity.normalizeString(it) },
            albumTitle = track.album,
            durationSeconds = track.durationSeconds,
            primaryLanguage = inferLanguageFromText("${track.title} ${track.artist}"),
            primaryGenre = inferGenreFromText("${track.title} ${track.artist}"),
            providerIds = providerMap,
            isPlayable = track.rights.isStreamable,
            sourceConfidence = 0.95f,
            metadataConfidence = if (track.durationSeconds > 0) 0.98f else 0.85f
        )
    }

    fun deduplicateTracks(tracks: List<UnifiedTrack>): List<UnifiedTrack> {
        val identities = mutableListOf<MusicIdentity>()
        val result = mutableListOf<UnifiedTrack>()

        for (track in tracks) {
            val identity = createCanonicalIdentity(track)
            val existing = identities.find { it.matches(identity) }
            if (existing == null) {
                identities.add(identity)
                result.add(track)
            }
        }
        return result
    }

    private fun inferLanguageFromText(text: String): String {
        val lower = text.lowercase()
        return when {
            lower.contains("tamil") -> "Tamil"
            lower.contains("hindi") || lower.contains("bollywood") -> "Hindi"
            lower.contains("telugu") -> "Telugu"
            lower.contains("malayalam") -> "Malayalam"
            lower.contains("korean") || lower.contains("kpop") -> "Korean"
            else -> "English"
        }
    }

    private fun inferGenreFromText(text: String): String {
        val lower = text.lowercase()
        return when {
            lower.contains("hip hop") || lower.contains("rap") -> "Hip-Hop"
            lower.contains("rock") -> "Rock"
            lower.contains("classical") || lower.contains("carnatic") -> "Classical"
            lower.contains("jazz") -> "Jazz"
            lower.contains("electronic") || lower.contains("edm") || lower.contains("techno") -> "Electronic"
            lower.contains("melody") || lower.contains("romantic") -> "Melody"
            else -> "Pop"
        }
    }
}
