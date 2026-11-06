package com.music.echo.notune.intelligence.intent

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntentEngine @Inject constructor() {

    fun parseIntent(query: String): StructuredUserIntent {
        val q = query.trim()
        val lower = q.lowercase()

        var intentType = IntentType.PLAY_MUSIC
        var targetArtist: String? = null
        var targetGenre: String? = null
        var targetLanguage: String? = null
        var targetMood: String? = null
        var targetTrack: String? = null
        var durationMinutes: Int? = null
        var minEnergy: Float? = null
        var maxEnergy: Float? = null
        var isDiscovery = false
        var isUnderrated = false
        val excludeArtists = mutableListOf<String>()
        val excludeGenres = mutableListOf<String>()
        var targetEra: String? = null
        var persistentPreferenceChange = false
        var sessionScope = true

        // 1. Action & Intent type detection
        when {
            lower.contains("why did you play") || lower.contains("why this song") || lower.contains("explain recommendation") -> {
                intentType = IntentType.EXPLAIN_RECOMMENDATION
            }
            lower.contains("translate") && lower.contains("lyrics") -> {
                intentType = IntentType.TRANSLATE_LYRICS
            }
            lower.contains("create room") || lower.contains("listen together") -> {
                intentType = IntentType.CREATE_ROOM
            }
            lower.contains("share") && (lower.contains("playlist") || lower.contains("song")) -> {
                intentType = IntentType.SHARE_PLAYLIST
            }
            lower.contains("songs like this") || lower.contains("similar to this") || lower.contains("find similar") -> {
                intentType = IntentType.FIND_SIMILAR
            }
            lower.contains("don't play") || lower.contains("don't want") || lower.contains("exclude") || lower.contains("no more") -> {
                intentType = IntentType.SESSION_EXCLUSION
                if (lower.contains("permanently") || lower.contains("never")) {
                    persistentPreferenceChange = true
                    sessionScope = false
                }
            }
            lower.contains("haven't heard") || lower.contains("never heard") || lower.contains("unheard") || lower.contains("discover") -> {
                intentType = IntentType.DISCOVER_UNHEARD
                isDiscovery = true
            }
            lower.contains("minute") || lower.contains("hour") -> {
                intentType = IntentType.CREATE_TIME_LIMITED_QUEUE
                val minuteRegex = "(\\d+)\\s*minute".toRegex()
                val match = minuteRegex.find(lower)
                if (match != null) {
                    durationMinutes = match.groupValues[1].toIntOrNull()
                } else if (lower.contains("30 minute") || lower.contains("half hour")) {
                    durationMinutes = 30
                } else if (lower.contains("45 minute")) {
                    durationMinutes = 45
                } else if (lower.contains("hour")) {
                    durationMinutes = 60
                }
            }
            lower.contains("calmer") || lower.contains("energetic") || lower.contains("add more") || lower.contains("remove next") || lower.contains("surprise me") -> {
                intentType = IntentType.MODIFY_QUEUE
            }
            (lower.contains("only play") || lower.contains("play more")) && (lower.contains("tamil") || lower.contains("hindi") || lower.contains("english") || lower.contains("telugu")) -> {
                intentType = IntentType.FILTER_QUEUE_LANGUAGE
            }
            lower.contains("from") && (lower.contains("2000") || lower.contains("2010") || lower.contains("1990") || lower.contains("90s")) -> {
                intentType = IntentType.SEARCH_WITH_CONSTRAINTS
            }
        }

        // 2. Language Extraction
        when {
            lower.contains("tamil") -> targetLanguage = "Tamil"
            lower.contains("hindi") || lower.contains("bollywood") -> targetLanguage = "Hindi"
            lower.contains("malayalam") -> targetLanguage = "Malayalam"
            lower.contains("telugu") -> targetLanguage = "Telugu"
            lower.contains("korean") || lower.contains("k-pop") || lower.contains("kpop") -> targetLanguage = "Korean"
            lower.contains("english") -> targetLanguage = "English"
        }

        // 3. Era Extraction
        when {
            lower.contains("2000s") || lower.contains("2000's") -> targetEra = "2000s"
            lower.contains("90s") || lower.contains("1990s") -> targetEra = "90s"
            lower.contains("80s") || lower.contains("1980s") -> targetEra = "80s"
            lower.contains("2010s") || lower.contains("2010 to 2015") -> targetEra = "2010s"
        }

        // 4. Mood & Energy
        when {
            lower.contains("peaceful") || lower.contains("relax") || lower.contains("chill") || lower.contains("calmer") -> {
                targetMood = "Calm"
                maxEnergy = 0.45f
            }
            lower.contains("energetic") || lower.contains("workout") || lower.contains("upbeat") -> {
                targetMood = "Energetic"
                minEnergy = 0.75f
            }
            lower.contains("sad") || lower.contains("melancholic") -> {
                targetMood = "Sad"
                maxEnergy = 0.40f
            }
            lower.contains("happy") || lower.contains("happier") -> {
                targetMood = "Happy"
                minEnergy = 0.60f
            }
        }

        // 5. Artist Extraction & Exclusions
        when {
            lower.contains("ar rahman") || lower.contains("a.r. rahman") || lower.contains("rahman") -> {
                if (intentType == IntentType.SESSION_EXCLUSION) excludeArtists.add("A.R. Rahman") else targetArtist = "A.R. Rahman"
            }
            lower.contains("anirudh") -> {
                if (intentType == IntentType.SESSION_EXCLUSION) excludeArtists.add("Anirudh Ravichander") else targetArtist = "Anirudh Ravichander"
            }
            lower.contains("ilaiyaraaja") || lower.contains("ilayaraja") -> {
                if (intentType == IntentType.SESSION_EXCLUSION) excludeArtists.add("Ilaiyaraaja") else targetArtist = "Ilaiyaraaja"
            }
            lower.contains("taylor swift") -> {
                if (intentType == IntentType.SESSION_EXCLUSION) excludeArtists.add("Taylor Swift") else targetArtist = "Taylor Swift"
            }
        }

        if (lower.contains("underrated") || lower.contains("hidden gem")) isUnderrated = true

        val actionDesc = when (intentType) {
            IntentType.PLAY_MUSIC -> "Initiate playback matching '$q'"
            IntentType.FIND_SIMILAR -> "Find tracks acoustically similar to current selection"
            IntentType.MODIFY_QUEUE -> "Modify current queue flow parameters"
            IntentType.FILTER_QUEUE_LANGUAGE -> "Filter active queue to $targetLanguage"
            IntentType.SESSION_EXCLUSION -> "Exclude ${excludeArtists.joinToString().ifEmpty { "artist" }} from active session"
            IntentType.DISCOVER_UNHEARD -> "Inject unheard candidate discoveries"
            IntentType.CREATE_TIME_LIMITED_QUEUE -> "Assemble ${durationMinutes ?: 30} minute continuous queue"
            IntentType.SEARCH_WITH_CONSTRAINTS -> "Execute constrained catalog search"
            IntentType.EXPLAIN_RECOMMENDATION -> "Generate ranking signal explanation card"
            IntentType.TRANSLATE_LYRICS -> "Translate synchronized lyrics"
            IntentType.CREATE_ROOM -> "Spin up synchronized Listen Together room"
            IntentType.SHARE_PLAYLIST -> "Generate deep link payload"
            IntentType.OTHER -> "Process neural command query"
        }

        return StructuredUserIntent(
            intentType = intentType,
            rawQuery = q,
            targetArtist = targetArtist,
            targetGenre = targetGenre,
            targetLanguage = targetLanguage,
            targetMood = targetMood,
            targetTrack = targetTrack,
            durationMinutes = durationMinutes,
            constraints = IntentConstraints(
                minEnergy = minEnergy,
                maxEnergy = maxEnergy,
                discoveryRequested = isDiscovery,
                underratedRequested = isUnderrated,
                excludeArtists = excludeArtists,
                excludeGenres = excludeGenres,
                targetEra = targetEra
            ),
            requestedAction = actionDesc,
            confidence = 0.92f,
            sessionScope = sessionScope,
            persistentPreferenceChange = persistentPreferenceChange
        )
    }
}
