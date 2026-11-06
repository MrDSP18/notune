package com.music.echo.notune.intelligence.tools

enum class ToolCategory {
    CATALOG_SEARCH, TASTE_INTELLIGENCE, PLAYBACK_QUEUE, PLAYLIST_SOCIAL
}

data class NotuneToolDefinition(
    val name: String,
    val description: String,
    val category: ToolCategory,
    val parameters: List<String> = emptyList()
)

object NotuneToolRegistry {

    val tools: List<NotuneToolDefinition> = listOf(
        // Catalog & Search
        NotuneToolDefinition("search_music", "Search catalog for songs, artists, albums, playlists", ToolCategory.CATALOG_SEARCH, listOf("query", "filter")),
        NotuneToolDefinition("search_lyrics", "Search for lyrics by excerpt or track name", ToolCategory.CATALOG_SEARCH, listOf("query")),
        NotuneToolDefinition("get_song", "Get song metadata by ID", ToolCategory.CATALOG_SEARCH, listOf("songId")),
        NotuneToolDefinition("get_artist", "Get artist profile and top tracks by ID", ToolCategory.CATALOG_SEARCH, listOf("artistId")),
        NotuneToolDefinition("get_album", "Get album details and track list by ID", ToolCategory.CATALOG_SEARCH, listOf("albumId")),
        NotuneToolDefinition("find_similar", "Find similar tracks based on current song", ToolCategory.CATALOG_SEARCH, listOf("songId", "count")),
        NotuneToolDefinition("start_artist_flow", "Start continuous radio based on artist", ToolCategory.CATALOG_SEARCH, listOf("artistId")),
        NotuneToolDefinition("start_song_radio", "Start continuous radio based on song", ToolCategory.CATALOG_SEARCH, listOf("songId")),

        // Taste & Intelligence
        NotuneToolDefinition("get_user_taste", "Retrieve top user languages, genres, and artists", ToolCategory.TASTE_INTELLIGENCE),
        NotuneToolDefinition("get_music_dna", "Get visual music DNA traits and traits distribution", ToolCategory.TASTE_INTELLIGENCE),
        NotuneToolDefinition("translate_lyrics", "Translate active lyrics to target language", ToolCategory.TASTE_INTELLIGENCE, listOf("targetLanguage")),
        NotuneToolDefinition("romanize_lyrics", "Get romanized pronunciation of active lyrics", ToolCategory.TASTE_INTELLIGENCE),

        // Playback & Queue
        NotuneToolDefinition("get_current_playback", "Get currently playing song, position, and status", ToolCategory.PLAYBACK_QUEUE),
        NotuneToolDefinition("get_queue", "Get active queue items and match percentages", ToolCategory.PLAYBACK_QUEUE),
        NotuneToolDefinition("add_to_queue", "Add track(s) to end of active queue", ToolCategory.PLAYBACK_QUEUE, listOf("songIds")),
        NotuneToolDefinition("remove_from_queue", "Remove specific index or track from queue", ToolCategory.PLAYBACK_QUEUE, listOf("index")),
        NotuneToolDefinition("reorder_queue", "Reorder queue items to match vibe", ToolCategory.PLAYBACK_QUEUE, listOf("vibe")),
        NotuneToolDefinition("play_song", "Play specific song immediately", ToolCategory.PLAYBACK_QUEUE, listOf("songId")),
        NotuneToolDefinition("pause", "Pause active playback", ToolCategory.PLAYBACK_QUEUE),
        NotuneToolDefinition("skip", "Skip to next track", ToolCategory.PLAYBACK_QUEUE),
        NotuneToolDefinition("favorite", "Toggle favorite status for current track", ToolCategory.PLAYBACK_QUEUE),

        // Playlist & Social
        NotuneToolDefinition("create_playlist", "Create new custom playlist", ToolCategory.PLAYLIST_SOCIAL, listOf("title", "songIds")),
        NotuneToolDefinition("add_to_playlist", "Add song to existing playlist", ToolCategory.PLAYLIST_SOCIAL, listOf("playlistId", "songId")),
        NotuneToolDefinition("create_room", "Create realtime listening room", ToolCategory.PLAYLIST_SOCIAL, listOf("roomTitle")),
        NotuneToolDefinition("join_room", "Join existing listening room by ID", ToolCategory.PLAYLIST_SOCIAL, listOf("roomId")),
        NotuneToolDefinition("share_song", "Generate universal share link for song", ToolCategory.PLAYLIST_SOCIAL, listOf("songId")),
        NotuneToolDefinition("share_playlist", "Generate universal share link for playlist", ToolCategory.PLAYLIST_SOCIAL, listOf("playlistId")),
        NotuneToolDefinition("share_room", "Generate universal share link for room", ToolCategory.PLAYLIST_SOCIAL, listOf("roomId")),
        NotuneToolDefinition("find_friend", "Search social network for user", ToolCategory.PLAYLIST_SOCIAL, listOf("username"))
    )

    fun getTool(name: String): NotuneToolDefinition? {
        return tools.find { it.name == name }
    }
}
