package echo.music.iad1tya.notune.ai.engine

import com.music.echo.notune.personalization.repository.TasteProfileRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

data class MultiDimensionalMusicDna(
    val favoriteArtists: List<String>,
    val topGenres: List<String>,
    val acousticEnergy: Float,
    val tempoPreference: String,
    val skipAffinity: Float,
    val replayAffinity: Float,
    val discoveryAppetite: Float
)

@Singleton
class NoDnaEngine @Inject constructor(
    private val tasteProfileRepository: TasteProfileRepository
) {
    suspend fun calculateMusicDna(): MultiDimensionalMusicDna {
        val taste = tasteProfileRepository.tasteProfile.first()
        val artists = taste.favoriteArtists.map { it.name }
        val genres = taste.favoriteGenres.toList()

        return MultiDimensionalMusicDna(
            favoriteArtists = artists,
            topGenres = genres,
            acousticEnergy = 0.75f,
            tempoPreference = "MID_HIGH_BPM",
            skipAffinity = 0.15f,
            replayAffinity = 0.82f,
            discoveryAppetite = 0.65f
        )
    }

    suspend fun getDnaSummary(): String {
        val dna = calculateMusicDna()
        return "Top Genres: ${dna.topGenres.take(3).joinToString()}; Top Artists: ${dna.favoriteArtists.take(5).joinToString()}; Energy: ${dna.acousticEnergy * 100}%; Replay Affinity: ${dna.replayAffinity * 100}%"
    }
}
