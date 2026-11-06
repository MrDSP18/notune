package com.music.echo.notune.link

enum class NoLinkType(val pathPrefix: String) {
    SONG("s"),
    PLAYLIST("p"),
    ROOM("r"),
    PROFILE("u")
}

data class NoLinkItem(
    val id: String,
    val shortCode: String,
    val type: NoLinkType,
    val title: String,
    val subtitle: String,
    val artworkUrl: String? = null,
    val canonicalUrl: String
)

object NoLinkEngine {

    private const val BASE_DOMAIN = "https://notune.app"

    fun generateSongLink(songId: String, title: String, artist: String): NoLinkItem {
        val shortCode = generateShortCode(songId)
        val canonical = "$BASE_DOMAIN/s/$shortCode"
        return NoLinkItem(
            id = songId,
            shortCode = shortCode,
            type = NoLinkType.SONG,
            title = title,
            subtitle = artist,
            canonicalUrl = canonical
        )
    }

    fun generatePlaylistLink(playlistId: String, title: String, trackCount: Int): NoLinkItem {
        val shortCode = generateShortCode(playlistId)
        val canonical = "$BASE_DOMAIN/p/$shortCode"
        return NoLinkItem(
            id = playlistId,
            shortCode = shortCode,
            type = NoLinkType.PLAYLIST,
            title = title,
            subtitle = "$trackCount tracks",
            canonicalUrl = canonical
        )
    }

    fun generateRoomLink(roomId: String, roomTitle: String, listenerCount: Int): NoLinkItem {
        val shortCode = generateShortCode(roomId)
        val canonical = "$BASE_DOMAIN/r/$shortCode"
        return NoLinkItem(
            id = roomId,
            shortCode = shortCode,
            type = NoLinkType.ROOM,
            title = roomTitle,
            subtitle = "🔴 $listenerCount listeners in Room",
            canonicalUrl = canonical
        )
    }

    fun generateProfileLink(username: String): NoLinkItem {
        val cleanUser = username.trim().removePrefix("@")
        val canonical = "$BASE_DOMAIN/u/$cleanUser"
        return NoLinkItem(
            id = cleanUser,
            shortCode = cleanUser,
            type = NoLinkType.PROFILE,
            title = "@$cleanUser",
            subtitle = "NØTUNE Member Profile",
            canonicalUrl = canonical
        )
    }

    fun parseNoLink(urlStr: String): NoLinkItem? {
        if (!urlStr.contains("notune.app/")) return null
        val path = urlStr.substringAfter("notune.app/").trim()
        val parts = path.split("/")
        if (parts.size < 2) return null

        val prefix = parts[0]
        val code = parts[1]

        return when (prefix) {
            "s" -> NoLinkItem(code, code, NoLinkType.SONG, "Shared Track", "NØTUNE Track", canonicalUrl = urlStr)
            "p" -> NoLinkItem(code, code, NoLinkType.PLAYLIST, "Shared Playlist", "NØTUNE Playlist", canonicalUrl = urlStr)
            "r" -> NoLinkItem(code, code, NoLinkType.ROOM, "Shared Room", "Live NØ ROOM", canonicalUrl = urlStr)
            "u" -> NoLinkItem(code, code, NoLinkType.PROFILE, "@$code", "NØTUNE Member", canonicalUrl = urlStr)
            else -> null
        }
    }

    private fun generateShortCode(input: String): String {
        val hash = input.hashCode().toLong() and 0xFFFFFFFFL
        return java.lang.Long.toString(hash, 36)
    }
}
