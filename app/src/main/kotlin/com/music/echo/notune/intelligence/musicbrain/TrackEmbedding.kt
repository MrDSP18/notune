package com.music.echo.notune.intelligence.musicbrain

/**
 * 12-Dimensional Music Vector representation for a track.
 */
data class TrackEmbedding(
    val trackId: String,
    val title: String,
    val artistName: String,
    val energy: Float = 0.5f,
    val moodValence: Float = 0.5f,
    val danceability: Float = 0.5f,
    val acousticness: Float = 0.5f,
    val instrumentalness: Float = 0.2f,
    val tempoBpm: Float = 110f,
    val language: String = "English",
    val genre: String = "Pop",
    val era: String = "2020s",
    val popularity: Float = 0.5f,
    val novelty: Float = 0.3f,
    val vocalStyle: String = "Clean"
) {
    fun toNormalizedVector(): FloatArray {
        return floatArrayOf(
            energy,
            moodValence,
            danceability,
            acousticness,
            instrumentalness,
            (tempoBpm / 200f).coerceIn(0f, 1f),
            popularity,
            novelty
        )
    }
}

/**
 * 12-Dimensional User Taste Vector representation.
 */
data class UserEmbedding(
    val userId: String = "local_user",
    val targetEnergy: Float = 0.65f,
    val targetValence: Float = 0.60f,
    val targetDanceability: Float = 0.55f,
    val targetAcousticness: Float = 0.50f,
    val targetInstrumentalness: Float = 0.30f,
    val targetTempoBpm: Float = 115f,
    val preferredLanguage: String = "Tamil",
    val preferredGenre: String = "Tamil Pop",
    val preferredEra: String = "2000s",
    val noveltyPreference: Float = 0.40f
) {
    fun toNormalizedVector(): FloatArray {
        return floatArrayOf(
            targetEnergy,
            targetValence,
            targetDanceability,
            targetAcousticness,
            targetInstrumentalness,
            (targetTempoBpm / 200f).coerceIn(0f, 1f),
            0.6f,
            noveltyPreference
        )
    }
}
