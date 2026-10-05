package com.music.echo.notune.share

enum class ShareContentType {
    SONG, ALBUM, PLAYLIST, ARTIST, ROOM
}

object UniversalShareHandler {
    private const val SCHEME = "notune"
    private const val WEB_FALLBACK_BASE = "https://notune.app/share"

    fun buildDeepLink(type: ShareContentType, id: String): String {
        return "$SCHEME://${type.name.lowercase()}/$id"
    }

    fun buildWebFallbackUrl(type: ShareContentType, id: String): String {
        return "$WEB_FALLBACK_BASE?type=${type.name.lowercase()}&id=$id"
    }

    fun parseDeepLink(url: String): Pair<ShareContentType, String>? {
        if (!url.startsWith("$SCHEME://")) return null
        val path = url.removePrefix("$SCHEME://")
        val parts = path.split("/")
        if (parts.size < 2) return null

        val type = when (parts[0].lowercase()) {
            "song" -> ShareContentType.SONG
            "album" -> ShareContentType.ALBUM
            "playlist" -> ShareContentType.PLAYLIST
            "artist" -> ShareContentType.ARTIST
            "room" -> ShareContentType.ROOM
            else -> return null
        }

        return Pair(type, parts[1])
    }
}
