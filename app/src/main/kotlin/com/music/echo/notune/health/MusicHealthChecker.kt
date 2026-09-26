package echo.music.iad1tya.notune.health

import echo.music.iad1tya.notune.provider.MusicProvider
import echo.music.iad1tya.notune.provider.PlaybackType
import echo.music.iad1tya.notune.provider.ProviderRegistry
import echo.music.iad1tya.notune.provider.UnifiedTrack
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

enum class TrackHealthStatus {
    PLAYABLE,
    TEMPORARILY_UNAVAILABLE,
    PROVIDER_UNAVAILABLE,
    REGION_RESTRICTED,
    AUTH_REQUIRED,
    USER_LIBRARY,
    BROKEN_SOURCE,
    UNKNOWN
}

data class TrackHealthResult(
    val trackId: String,
    val status: TrackHealthStatus,
    val playbackUri: String? = null,
    val resolvedProviderName: String,
    val errorMessage: String? = null
)

@Singleton
class MusicHealthChecker @Inject constructor(
    private val providerRegistry: ProviderRegistry
) {
    suspend fun verifyTrackHealth(track: UnifiedTrack): TrackHealthResult {
        if (track.playbackType == PlaybackType.USER_LIBRARY) {
            return TrackHealthResult(
                trackId = track.id,
                status = TrackHealthStatus.USER_LIBRARY,
                resolvedProviderName = track.providerName
            )
        }

        val provider = providerRegistry.getProvider(track.providerName)
        if (provider == null) {
            return TrackHealthResult(
                trackId = track.id,
                status = TrackHealthStatus.PROVIDER_UNAVAILABLE,
                resolvedProviderName = track.providerName,
                errorMessage = "Provider ${track.providerName} not found"
            )
        }

        if (!provider.isAvailable()) {
            return TrackHealthResult(
                trackId = track.id,
                status = TrackHealthStatus.PROVIDER_UNAVAILABLE,
                resolvedProviderName = track.providerName,
                errorMessage = "Provider is currently offline"
            )
        }

        val uriResult = provider.resolvePlaybackUri(track.id)
        return uriResult.fold(
            onSuccess = { uri ->
                if (uri.isNotBlank()) {
                    TrackHealthResult(
                        trackId = track.id,
                        status = TrackHealthStatus.PLAYABLE,
                        playbackUri = uri,
                        resolvedProviderName = track.providerName
                    )
                } else {
                    TrackHealthResult(
                        trackId = track.id,
                        status = TrackHealthStatus.BROKEN_SOURCE,
                        resolvedProviderName = track.providerName,
                        errorMessage = "Stream URI is empty"
                    )
                }
            },
            onFailure = { ex ->
                Timber.w(ex, "Track health check failed for track ${track.id}")
                val status = when {
                    ex.message?.contains("403", ignoreCase = true) == true -> TrackHealthStatus.AUTH_REQUIRED
                    ex.message?.contains("geo", ignoreCase = true) == true -> TrackHealthStatus.REGION_RESTRICTED
                    else -> TrackHealthStatus.TEMPORARILY_UNAVAILABLE
                }
                TrackHealthResult(
                    trackId = track.id,
                    status = status,
                    resolvedProviderName = track.providerName,
                    errorMessage = ex.message
                )
            }
        )
    }
}
