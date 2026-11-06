package echo.music.iad1tya.notune.intelligence.enrichment

import java.text.Normalizer
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Normalizes title, artist, album, and movie names to extract clean song identities.
 * Supports noisy YouTube video titles, remaster suffixes, feat. separators, and Indian regional titles.
 */
@Singleton
class SongIdentityNormalizer @Inject constructor() {

    private val noisySuffixesRegex = Regex(
        "(?i)\\s*[\\[(](official\\s*(audio|video|music\\s*video|lyric\\s*video|full\\s*video|hd|4k)?|lyric\\s*video|remastered(\\s*\\d{4})?|video\\s*song|full\\s*song|audio|hd|4k|original\\s*motion\\s*picture\\s*soundtrack|soundtrack|ost|tamil|telugu|hindi|malayalam|kannada|bengali|marathi|punjabi|gujarati|odia|assamese)[\\])]\\s*"
    )

    private val moviePatternRegex = Regex(
        "(?i)[\\s|\\[(]+from\\s+[\"']?([^\"'\\])]+)[\"']?[\\])]?"
    )

    private val featPatternRegex = Regex(
        "(?i)\\s*[\\[(]?\\b(feat\\.?|ft\\.?|featuring)\\b\\s+([^\\)\\]]+)[\\])]?"
    )

    /**
     * Normalize raw title, artist, and album fields into a clean SongIdentity.
     */
    fun normalize(
        rawTitle: String,
        rawArtist: String,
        rawAlbum: String? = null,
        durationMs: Long = 0L,
        isrc: String? = null,
        filename: String? = null
    ): SongIdentity {
        var title = cleanString(rawTitle)
        var artist = cleanString(rawArtist)
        var album = rawAlbum?.let { cleanString(it) }
        var extractedMovie: String? = null
        val featuredArtists = mutableListOf<String>()

        // Replace non-breaking spaces and whitespace variants with standard space
        title = title.replace('\u00A0', ' ').replace('\t', ' ')
        artist = artist.replace('\u00A0', ' ').replace('\t', ' ')

        // Use filename as extraction source when title/artist are empty
        val extractionSource = filename?.takeIf { it.isNotBlank() } ?: rawTitle

        // Fallback for empty/unknown title/artist from filename or raw input
        if (title.isBlank() || title.equalsIgnoreCase("unknown") || title.equalsIgnoreCase("track")) {
            title = extractTitleFromRawInput(extractionSource)
        }
        if (artist.isBlank() || artist.equalsIgnoreCase("unknown")) {
            artist = extractArtistFromRawInput(extractionSource)
        }

        // Extract movie if embedded in title (e.g. "Hukum [From Jailer]")
        val movieMatch = moviePatternRegex.find(title)
        if (movieMatch != null) {
            extractedMovie = movieMatch.groupValues[1].trim()
            title = title.replace(movieMatch.value, "").trim()
        }

        // Extract featuring artists if present in title
        val featMatch = featPatternRegex.find(title)
        if (featMatch != null) {
            val featNames = featMatch.groupValues[2]
            featNames.split(",", "&", "and").forEach { name ->
                val trimmed = name.trim()
                if (trimmed.isNotBlank()) featuredArtists.add(trimmed)
            }
            title = title.replace(featMatch.value, "").trim()
        }

        // Strip remaining noisy suffixes like (Official Audio), - Remastered 2024
        title = noisySuffixesRegex.replace(title, "").trim()
        title = title.removeSuffix("-").removeSuffix("|").trim()

        // Normalize Unicode
        title = Normalizer.normalize(title, Normalizer.Form.NFC).trim()
        artist = Normalizer.normalize(artist, Normalizer.Form.NFC).trim()
        album = album?.let { Normalizer.normalize(it, Normalizer.Form.NFC).trim() }
        extractedMovie = extractedMovie?.let { Normalizer.normalize(it, Normalizer.Form.NFC).trim() }

        return SongIdentity(
            cleanTitle = title,
            cleanArtist = artist,
            cleanAlbum = album,
            cleanMovie = extractedMovie,
            featuredArtists = featuredArtists,
            durationMs = durationMs,
            isrc = isrc,
            rawInput = "$rawTitle - $rawArtist"
        )
    }

    private fun cleanString(input: String): String {
        return input.trim()
    }

    private fun extractTitleFromRawInput(raw: String): String {
        val clean = raw.replace('\u00A0', ' ')
            .substringAfterLast("/").substringAfterLast("\\").substringBeforeLast(".")
        return if (clean.contains("_-_")) {
            clean.substringAfter("_-_").replace("_", " ").trim()
        } else if (clean.contains(" - ")) {
            clean.substringAfter(" - ").trim()
        } else if (clean.contains("-")) {
            clean.substringAfter("-").replace("_", " ").trim()
        } else {
            clean.replace("_", " ").trim()
        }
    }

    private fun extractArtistFromRawInput(raw: String): String {
        val clean = raw.replace('\u00A0', ' ')
            .substringAfterLast("/").substringAfterLast("\\").substringBeforeLast(".")
        return if (clean.contains("_-_")) {
            clean.substringBefore("_-_").replace("_", " ").trim()
        } else if (clean.contains(" - ")) {
            clean.substringBefore(" - ").trim()
        } else if (clean.contains("-")) {
            clean.substringBefore("-").replace("_", " ").trim()
        } else {
            "Unknown Artist"
        }
    }

    private fun String.equalsIgnoreCase(other: String): Boolean {
        return this.equals(other, ignoreCase = true)
    }
}
