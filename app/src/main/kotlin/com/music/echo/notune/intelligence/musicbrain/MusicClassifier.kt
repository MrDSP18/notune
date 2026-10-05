package com.music.echo.notune.intelligence.musicbrain

data class ClassificationConfidence<T>(
    val value: T,
    val confidence: Float
)

data class TrackClassification(
    val language: ClassificationConfidence<String>,
    val genre: ClassificationConfidence<String>,
    val subgenre: ClassificationConfidence<String>,
    val era: ClassificationConfidence<String>,
    val energy: ClassificationConfidence<Float>,         // 0.0f to 1.0f
    val tempoBpm: ClassificationConfidence<Int>,
    val danceability: ClassificationConfidence<Float>,
    val acousticness: ClassificationConfidence<Float>,
    val instrumentalness: ClassificationConfidence<Float>,
    val popularity: ClassificationConfidence<Float>,
    val familiarity: ClassificationConfidence<Float>,
    val novelty: ClassificationConfidence<Float>,
    val isExplicit: Boolean = false,
    val isLive: Boolean = false,
    val isRemix: Boolean = false,
    val isCover: Boolean = false
)

object MusicClassifier {
    fun classify(identity: MusicIdentity): TrackClassification {
        val titleLower = identity.canonicalTitle.lowercase()
        val isLive = titleLower.contains("live") || titleLower.contains("concert")
        val isRemix = titleLower.contains("remix") || titleLower.contains("mix") || titleLower.contains("dj")
        val isCover = titleLower.contains("cover") || titleLower.contains("tribute")

        val eraStr = when (val year = identity.releaseYear) {
            null -> "2020s"
            in 1950..1969 -> "Classic 50s/60s"
            in 1970..1979 -> "70s Retro"
            in 1980..1989 -> "80s Hits"
            in 1990..1999 -> "90s Golden Era"
            in 2000..2009 -> "2000s Melodies"
            in 2010..2019 -> "2010s Hits"
            else -> "Modern 2020s"
        }

        val estimatedEnergy = when {
            titleLower.contains("beat") || titleLower.contains("dance") || titleLower.contains("rock") -> 0.85f
            titleLower.contains("melody") || titleLower.contains("unplugged") || titleLower.contains("acoustic") -> 0.35f
            else -> 0.60f
        }

        return TrackClassification(
            language = ClassificationConfidence(identity.primaryLanguage, identity.metadataConfidence),
            genre = ClassificationConfidence(identity.primaryGenre, identity.metadataConfidence),
            subgenre = ClassificationConfidence(identity.subGenres.firstOrNull() ?: identity.primaryGenre, 0.70f),
            era = ClassificationConfidence(eraStr, if (identity.releaseYear != null) 1.0f else 0.50f),
            energy = ClassificationConfidence(estimatedEnergy, 0.65f),
            tempoBpm = ClassificationConfidence(120, 0.50f),
            danceability = ClassificationConfidence(0.60f, 0.50f),
            acousticness = ClassificationConfidence(if (estimatedEnergy < 0.50f) 0.70f else 0.20f, 0.60f),
            instrumentalness = ClassificationConfidence(0.10f, 0.50f),
            popularity = ClassificationConfidence(0.75f, 0.60f),
            familiarity = ClassificationConfidence(0.80f, 0.60f),
            novelty = ClassificationConfidence(0.20f, 0.60f),
            isExplicit = false,
            isLive = isLive,
            isRemix = isRemix,
            isCover = isCover
        )
    }
}
