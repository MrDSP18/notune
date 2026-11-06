package echo.music.iad1tya

import android.net.Uri
import java.net.URI

internal data class DeepLinkTarget(
    val kind: Kind,
    val id: String? = null,
    val roomCode: String? = null,
) {
    internal enum class Kind {
        ROOM,
        SONG,
        PLAYLIST,
        ALBUM,
        ARTIST,
        SEARCH,
        MIX,
        YOUTUBE_VIDEO,
        UNKNOWN,
    }
}

internal object DeepLinkParser {
    fun parse(uri: Uri): DeepLinkTarget = parse(uri.toString())

    fun parse(rawUrl: String): DeepLinkTarget {
        val uri = URI(rawUrl)
        val segments = uri.path
            ?.trim('/')
            ?.split('/')
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
        val path = segments.firstOrNull()?.lowercase()
        val host = uri.host?.lowercase()
        val query = uri.query
            ?.split('&')
            ?.mapNotNull { pair ->
                val index = pair.indexOf('=')
                if (index < 0) null else pair.substring(0, index) to pair.substring(index + 1)
            }
            ?.toMap()
            ?: emptyMap()

        val roomCode = when {
            path == "room" || path == "listen" -> segments.getOrNull(1)
                ?: query["code"]
                ?: query["room"]
            host == "listen" -> query["code"]
                ?: query["room"]
                ?: segments.firstOrNull()
            else -> null
        }

        if (!roomCode.isNullOrBlank() && (path == "room" || path == "listen" || host == "listen")) {
            return DeepLinkTarget(DeepLinkTarget.Kind.ROOM, roomCode = roomCode)
        }

        when (path) {
            "song", "track" -> {
                val songId = segments.getOrNull(1) ?: query["id"] ?: query["v"]
                return DeepLinkTarget(DeepLinkTarget.Kind.SONG, id = songId)
            }
            "playlist" -> {
                val playlistId = segments.getOrNull(1) ?: query["list"] ?: query["id"]
                return DeepLinkTarget(DeepLinkTarget.Kind.PLAYLIST, id = playlistId)
            }
            "album", "browse" -> {
                val albumId = segments.lastOrNull() ?: query["id"]
                return DeepLinkTarget(DeepLinkTarget.Kind.ALBUM, id = albumId)
            }
            "artist", "channel", "c" -> {
                val artistId = segments.lastOrNull() ?: query["id"]
                return DeepLinkTarget(DeepLinkTarget.Kind.ARTIST, id = artistId)
            }
            "search" -> {
                val searchQuery = query["q"]
                return DeepLinkTarget(DeepLinkTarget.Kind.SEARCH, id = searchQuery)
            }
            "mix" -> {
                val mixId = segments.getOrNull(1) ?: query["id"]
                return DeepLinkTarget(DeepLinkTarget.Kind.MIX, id = mixId)
            }
        }

        val youtubeVideoId = when {
            path == "watch" -> query["v"]
            host == "youtu.be" || host == "share.notune.fun" -> segments.firstOrNull()
            else -> null
        }
        if (!youtubeVideoId.isNullOrBlank()) {
            return DeepLinkTarget(DeepLinkTarget.Kind.YOUTUBE_VIDEO, id = youtubeVideoId)
        }

        val playlistId = query["list"]
        if (!playlistId.isNullOrBlank()) {
            return DeepLinkTarget(DeepLinkTarget.Kind.PLAYLIST, id = playlistId)
        }

        return DeepLinkTarget(DeepLinkTarget.Kind.UNKNOWN)
    }
}
