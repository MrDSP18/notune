

package echo.music.iad1tya.playback.queues

import androidx.media3.common.MediaItem
import com.music.innertube.YouTube
import com.music.innertube.models.WatchEndpoint
import echo.music.iad1tya.extensions.toMediaItem
import echo.music.iad1tya.models.MediaMetadata
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class YouTubeQueue(
    private var endpoint: WatchEndpoint,
    override val preloadItem: MediaMetadata? = null,
) : Queue {
    private var continuation: String? = null
    private var retryCount = 0
    private val maxRetries = 3

    init {
        val cleanVideoId = endpoint.videoId?.removePrefix("yt_")?.removePrefix("local_")
        var cleanPlaylistId = endpoint.playlistId?.removePrefix("yt_")?.removePrefix("local_")?.replace("RDAMVMyt_", "RDAMVM")?.replace("RDAMVMlocal_", "RDAMVM")
        if (cleanPlaylistId.isNullOrEmpty() && !cleanVideoId.isNullOrEmpty()) {
            cleanPlaylistId = "RDAMVM$cleanVideoId"
        }
        endpoint = endpoint.copy(videoId = cleanVideoId, playlistId = cleanPlaylistId)
    }

    override suspend fun getInitialStatus(): Queue.Status {
        return withContext(IO) {
            var lastException: Throwable? = null

            for (attempt in 0..maxRetries) {
                try {
                    val nextResult = YouTube.next(endpoint, continuation).getOrThrow()
                    if (nextResult.items.isNotEmpty()) {
                        endpoint = nextResult.endpoint
                        continuation = nextResult.continuation
                        retryCount = 0
                        return@withContext Queue.Status(
                            title = nextResult.title ?: preloadItem?.title,
                            items = nextResult.items.map { it.toMediaItem() },
                            mediaItemIndex = nextResult.currentIndex ?: 0,
                        )
                    } else if (attempt == 0 && endpoint.videoId != null) {
                        // If radio playlist returned empty, fallback to videoId alone or retry
                        endpoint = WatchEndpoint(
                            videoId = endpoint.videoId,
                            playlistId = if (endpoint.playlistId != null) null else "RDAMVM${endpoint.videoId}"
                        )
                    }
                } catch (e: Exception) {
                    lastException = e

                    if (attempt == 0 && endpoint.videoId != null) {
                        endpoint = WatchEndpoint(
                            videoId = endpoint.videoId,
                            playlistId = if (endpoint.playlistId != null) null else "RDAMVM${endpoint.videoId}"
                        )
                    }
                }
            }
            if (preloadItem != null) {
                return@withContext Queue.Status(
                    title = preloadItem.title,
                    items = listOf(preloadItem.toMediaItem()),
                    mediaItemIndex = 0
                )
            }
            throw lastException ?: Exception("Failed to get initial status")
        }
    }

    override fun hasNextPage(): Boolean = continuation != null

    override suspend fun nextPage(): List<MediaItem> {
        return withContext(IO) {
            var lastException: Throwable? = null
            
            for (attempt in 0..maxRetries) {
                try {
                    val nextResult = YouTube.next(endpoint, continuation).getOrThrow()
                    endpoint = nextResult.endpoint
                    continuation = nextResult.continuation
                    retryCount = 0
                    return@withContext nextResult.items.map { it.toMediaItem() }
                } catch (e: Exception) {
                    lastException = e
                    retryCount++
                    if (retryCount >= maxRetries) {
                        continuation = null 
                    }
                }
            }
            throw lastException ?: Exception("Failed to get next page")
        }
    }

    companion object {
        
        fun radio(song: MediaMetadata): YouTubeQueue {
            return YouTubeQueue(
                WatchEndpoint(
                    videoId = song.id,
                    playlistId = "RDAMVM${song.id}"
                ),
                song
            )
        }
    }
}
