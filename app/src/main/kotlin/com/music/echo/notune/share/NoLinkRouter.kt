package echo.music.iad1tya.notune.share

import android.net.Uri
import androidx.navigation.NavHostController
import com.music.innertube.YouTube
import echo.music.iad1tya.listentogether.ListenTogetherClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

sealed class DeepLinkDestination {
    data class Song(val songId: String) : DeepLinkDestination()
    data class Playlist(val playlistId: String) : DeepLinkDestination()
    data class Album(val albumId: String) : DeepLinkDestination()
    data class Artist(val artistId: String) : DeepLinkDestination()
    data class Room(val roomCode: String) : DeepLinkDestination()
    data class UserProfile(val userId: String) : DeepLinkDestination()
    data class AiMix(val mixId: String) : DeepLinkDestination()
    data class SearchQuery(val query: String) : DeepLinkDestination()
    object Invalid : DeepLinkDestination()
}

@Singleton
class NoLinkRouter @Inject constructor(
    private val listenTogetherManager: ListenTogetherClient
) {
    fun parseUri(uri: Uri): DeepLinkDestination {
        val pathSegments = uri.pathSegments
        if (pathSegments.isEmpty()) {
            val queryParam = uri.getQueryParameter("q") ?: uri.getQueryParameter("search")
            if (!queryParam.isNullOrBlank()) {
                return DeepLinkDestination.SearchQuery(queryParam)
            }
            return DeepLinkDestination.Invalid
        }

        val firstSegment = pathSegments.firstOrNull()?.lowercase() ?: ""
        val secondSegment = pathSegments.getOrNull(1) ?: ""

        return when (firstSegment) {
            "song", "track", "watch" -> {
                val id = secondSegment.ifBlank { uri.getQueryParameter("v") ?: "" }
                if (id.isNotBlank()) DeepLinkDestination.Song(id) else DeepLinkDestination.Invalid
            }
            "playlist" -> {
                val id = secondSegment.ifBlank { uri.getQueryParameter("list") ?: "" }
                if (id.isNotBlank()) DeepLinkDestination.Playlist(id) else DeepLinkDestination.Invalid
            }
            "album", "browse" -> {
                val id = secondSegment.ifBlank { uri.lastPathSegment ?: "" }
                if (id.isNotBlank()) DeepLinkDestination.Album(id) else DeepLinkDestination.Invalid
            }
            "artist", "channel", "c" -> {
                val id = secondSegment.ifBlank { uri.lastPathSegment ?: "" }
                if (id.isNotBlank()) DeepLinkDestination.Artist(id) else DeepLinkDestination.Invalid
            }
            "room", "listen" -> {
                val code = secondSegment.ifBlank { uri.getQueryParameter("code") ?: uri.getQueryParameter("room") ?: "" }
                if (code.isNotBlank()) DeepLinkDestination.Room(code) else DeepLinkDestination.Invalid
            }
            "user", "profile" -> {
                if (secondSegment.isNotBlank()) DeepLinkDestination.UserProfile(secondSegment) else DeepLinkDestination.Invalid
            }
            "mix" -> {
                if (secondSegment.isNotBlank()) DeepLinkDestination.AiMix(secondSegment) else DeepLinkDestination.Invalid
            }
            else -> {
                // Fallback video/playlist ID extraction
                val vId = uri.getQueryParameter("v")
                val pId = uri.getQueryParameter("list")
                val roomCode = uri.getQueryParameter("code") ?: uri.getQueryParameter("room")
                when {
                    !roomCode.isNullOrBlank() -> DeepLinkDestination.Room(roomCode)
                    !vId.isNullOrBlank() -> DeepLinkDestination.Song(vId)
                    !pId.isNullOrBlank() -> DeepLinkDestination.Playlist(pId)
                    else -> DeepLinkDestination.Invalid
                }
            }
        }
    }

    fun handleDeepLink(
        uri: Uri,
        navController: NavHostController,
        scope: CoroutineScope
    ) {
        val destination = parseUri(uri)
        Timber.d("NoLinkRouter routing destination: $destination for URI: $uri")

        when (destination) {
            is DeepLinkDestination.Song -> {
                navController.navigate("online_playlist/${destination.songId}")
            }
            is DeepLinkDestination.Playlist -> {
                navController.navigate("online_playlist/${destination.playlistId}")
            }
            is DeepLinkDestination.Album -> {
                navController.navigate("album/${destination.albumId}")
            }
            is DeepLinkDestination.Artist -> {
                navController.navigate("artist/${destination.artistId}")
            }
            is DeepLinkDestination.Room -> {
                listenTogetherManager.joinRoom(destination.roomCode, "Guest")
            }
            is DeepLinkDestination.SearchQuery -> {
                val encoded = URLEncoder.encode(destination.query, "UTF-8")
                navController.navigate("search/$encoded")
            }
            is DeepLinkDestination.UserProfile -> {
                Timber.d("User profile deep link: ${destination.userId}")
            }
            is DeepLinkDestination.AiMix -> {
                navController.navigate("online_playlist/${destination.mixId}")
            }
            DeepLinkDestination.Invalid -> {
                Timber.w("Invalid or unsupported deep link URI: $uri")
            }
        }
    }
}
