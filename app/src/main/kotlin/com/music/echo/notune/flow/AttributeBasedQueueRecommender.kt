package echo.music.iad1tya.notune.flow

import androidx.media3.common.MediaItem
import echo.music.iad1tya.models.MediaMetadata
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class SongAttributes(
    val musicDirectors: Set<String> = emptySet(),
    val actors: Set<String> = emptySet(),
    val authors: Set<String> = emptySet(),
    val genres: Set<String> = emptySet(),
    val primaryArtist: String? = null
)

/**
 * Recommends and ranks upcoming queue tracks based on musical attributes:
 * - Music Director / Composer
 * - Actor / Film Casting
 * - Author / Lyricist
 * - Genre & Musical Lineage
 *
 * Works in tandem with [AutoQueueCooldownManager] to ensure 100% deduplication
 * and enforce the strict 24-hour auto-queue cooldown window.
 */
@Singleton
class AttributeBasedQueueRecommender @Inject constructor(
    private val cooldownManager: AutoQueueCooldownManager
) {
    companion object {
        const val TAG = "AttributeQueueRecommender"

        // Music Directors / Composers (Indian + Global)
        val MUSIC_DIRECTORS = listOf(
            "a.r. rahman", "ar rahman", "rahman", "anirudh", "anirudh ravichander",
            "yuvan shankar raja", "yuvan", "harris jayaraj", "santhosh narayanan",
            "ilaiyaraaja", "ilayaraja", "vidyasagar", "d. imman", "imman", "g.v. prakash",
            "gv prakash", "deva", "pritam", "vishal-shekhar", "shankar-ehsaan-loy",
            "devi sri prasad", "dsp", "s.s. thaman", "thaman", "m.m. keeravani", "keeravani",
            "sachin-jigar", "amit trivedi", "ajay-atul", "sushin shyam", "jakes bejoy",
            "hesham abdul wahab", "sean roldan", "hans zimmer", "ludwig goransson",
            "john williams", "max martin", "metro boomin", "finneas", "jack antonoff"
        )

        // Actors / Cast (Indian & Cinema)
        val ACTORS = listOf(
            "vijay", "thalapathy", "rajinikanth", "thalaivar", "ajith", "ajith kumar", "ak",
            "kamal haasan", "kamal", "suriya", "dhanush", "sivakarthikeyan", "sk", "vikram",
            "chiyaan", "silambarasan", "simbu", "str", "karthi", "vijay sethupathi", "vjs",
            "fahadh faasil", "allu arjun", "prabhas", "ram charan", "mahesh babu", "jr ntr",
            "ntr", "nani", "shah rukh khan", "srk", "salman khan", "aamir khan", "ranbir kapoor",
            "ranveer singh", "hrithik roshan", "yash", "rishab shetty", "dulquer salmaan"
        )

        // Authors / Lyricists
        val AUTHORS = listOf(
            "vairamuthu", "vaali", "thamarai", "na. muthukumar", "na muthukumar", "kabilan",
            "vivek", "vignesh shivan", "pa. vijay", "yugabharathi", "madhan karky", "rokey",
            "gulzar", "javed akhtar", "amitabh bhattacharya", "swanand kirkire", "prasoon joshi",
            "irshad kamil", "kausar munir", "kunaal vermaa", "anand bakshi", "sameer"
        )

        // Genres & Styles
        val GENRES = listOf(
            "kollywood", "tamil", "thamizh", "tamizh", "tollywood", "telugu", "bollywood", "hindi",
            "mollywood", "malayalam", "sandalwood", "kannada", "melody", "kuthu", "folk",
            "hip-hop", "hip hop", "rap", "pop", "rock", "metal", "edm", "electronic",
            "dance", "r&b", "soul", "lofi", "lo-fi", "chill", "classical", "carnatic",
            "hindustani", "devotional", "acoustic", "indie", "ambient"
        )
    }

    /**
     * Extracts attributes (Music Director, Actor, Author, Genre) from song metadata.
     */
    fun extractAttributes(metadata: MediaMetadata?): SongAttributes {
        if (metadata == null) return SongAttributes()

        val textToSearch = buildString {
            append(metadata.title.lowercase(Locale.ROOT))
            append(' ')
            metadata.artists.forEach { append(it.name.lowercase(Locale.ROOT)).append(' ') }
            append(metadata.album?.title?.lowercase(Locale.ROOT).orEmpty())
        }

        val foundDirectors = MUSIC_DIRECTORS.filter { textToSearch.contains(it) }.toSet()
        val foundActors = ACTORS.filter { textToSearch.contains(it) }.toSet()
        val foundAuthors = AUTHORS.filter { textToSearch.contains(it) }.toSet()
        val foundGenres = GENRES.filter { textToSearch.contains(it) }.toSet()
        val primaryArtist = metadata.artists.firstOrNull()?.name?.lowercase(Locale.ROOT)

        return SongAttributes(
            musicDirectors = foundDirectors,
            actors = foundActors,
            authors = foundAuthors,
            genres = foundGenres,
            primaryArtist = primaryArtist
        )
    }

    /**
     * Scores how well a candidate track matches the attributes of the current track.
     */
    fun scoreCandidate(currentAttributes: SongAttributes, candidate: MediaItem): Float {
        val title = candidate.mediaMetadata.title?.toString()?.lowercase(Locale.ROOT).orEmpty()
        val artist = candidate.mediaMetadata.artist?.toString()?.lowercase(Locale.ROOT).orEmpty()
        val album = candidate.mediaMetadata.albumTitle?.toString()?.lowercase(Locale.ROOT).orEmpty()
        val candidateText = "$title $artist $album"

        var score = 10f // baseline score

        // 1. Music Director Affinity (Highest Weight)
        for (md in currentAttributes.musicDirectors) {
            if (candidateText.contains(md)) {
                score += 35f
                break
            }
        }

        // 2. Actor / Star Affinity
        for (actor in currentAttributes.actors) {
            if (candidateText.contains(actor)) {
                score += 30f
                break
            }
        }

        // 3. Author / Lyricist Affinity
        for (author in currentAttributes.authors) {
            if (candidateText.contains(author)) {
                score += 25f
                break
            }
        }

        // 4. Genre & Language Continuity
        for (genre in currentAttributes.genres) {
            if (candidateText.contains(genre)) {
                score += 20f
                break
            }
        }

        // 5. Artist Continuity
        if (currentAttributes.primaryArtist != null && candidateText.contains(currentAttributes.primaryArtist)) {
            score += 20f
        }

        return score
    }

    /**
     * Filters and ranks candidates for the upcoming queue:
     * 1. Eliminates duplicates (tracks already in active queue).
     * 2. Eliminates tracks currently under 24-hour auto-add cooldown.
     * 3. Sorts surviving candidates by attribute affinity to ensure smooth musical continuity.
     * 4. Enforces artist diversity so the same artist isn't played 3 times back-to-back.
     */
    fun filterAndRankQueue(
        currentTrack: MediaMetadata?,
        candidates: List<MediaItem>,
        activeQueueIds: Set<String>,
        currentTimeMs: Long = System.currentTimeMillis()
    ): List<MediaItem> {
        val currentAttributes = extractAttributes(currentTrack)

        // 1. Deduplication & 24h Cooldown Filtering
        val filtered = candidates.filter { candidate ->
            val id = candidate.mediaId
            if (id.isBlank()) return@filter false
            if (activeQueueIds.contains(id)) {
                Timber.tag(TAG).d("Rejected candidate $id: Already in active queue")
                return@filter false
            }
            if (!cooldownManager.isEligibleForAutoAdd(id, currentTimeMs)) {
                val remainingMins = (cooldownManager.getRemainingCooldownMs(id, currentTimeMs) / 60000L).coerceAtLeast(1)
                Timber.tag(TAG).d("Rejected candidate $id: Cooling down for another $remainingMins minutes (24h rule)")
                return@filter false
            }
            true
        }

        // 2. Score candidates by attribute match
        val scoredCandidates = filtered.map { candidate ->
            val score = scoreCandidate(currentAttributes, candidate)
            candidate to score
        }.sortedByDescending { it.second }

        // 3. Enforce Artist Diversification (prevent more than 2 consecutive tracks by the same artist)
        val diversified = mutableListOf<MediaItem>()
        val recentArtists = mutableListOf<String>()

        for ((candidate, _) in scoredCandidates) {
            val artistName = candidate.mediaMetadata.artist?.toString()?.lowercase(Locale.ROOT).orEmpty()
            if (artistName.isNotBlank()) {
                val consecutiveCount = recentArtists.takeLast(2).count { it == artistName }
                if (consecutiveCount >= 2) {
                    continue // Skip 3rd consecutive track by same artist
                }
            }
            diversified.add(candidate)
            if (artistName.isNotBlank()) {
                recentArtists.add(artistName)
            }
        }

        Timber.tag(TAG).d("Ranked and filtered ${candidates.size} -> ${diversified.size} candidates based on attributes & 24h cooldown")
        return diversified
    }
}
