package com.music.echo.localmedia

import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

data class FolderNode(
    val path: String,
    val name: String,
    val songCount: Int,
    val directSongs: List<Song> = emptyList(),
    val subfolders: List<FolderNode> = emptyList()
)

object LocalFolderManager {

    suspend fun getFolderHierarchy(database: MusicDatabase): List<FolderNode> = withContext(Dispatchers.IO) {
        val songs = database.getLocalSongsSync()
        val folderMap = mutableMapOf<String, MutableList<Song>>()

        songs.forEach { song ->
            val id = song.song.id
            val folderPath = extractFolderPath(id) ?: "Downloads & Other"
            folderMap.getOrPut(folderPath) { mutableListOf() }.add(song)
        }

        folderMap.map { (path, songList) ->
            FolderNode(
                path = path,
                name = path.substringAfterLast('/').ifBlank { path },
                songCount = songList.size,
                directSongs = songList
            )
        }.sortedBy { it.name.lowercase() }
    }

    suspend fun getSongsInFolder(path: String, database: MusicDatabase): List<Song> = withContext(Dispatchers.IO) {
        val songs = database.getLocalSongsSync()
        songs.filter { song ->
            val folderPath = extractFolderPath(song.song.id) ?: "Downloads & Other"
            folderPath == path || folderPath.startsWith("$path/")
        }
    }

    private fun extractFolderPath(uriOrPath: String): String? {
        if (uriOrPath.startsWith("content://media/external/audio/media/")) {
            return "MediaStore External Audio"
        }
        if (uriOrPath.startsWith("content://")) {
            val decoded = java.net.URLDecoder.decode(uriOrPath, "UTF-8")
            val relative = decoded.substringAfter("primary:", "").substringBeforeLast('/')
            return if (relative.isNotBlank()) relative else "Document Storage"
        }
        val file = File(uriOrPath)
        return file.parent?.replace('\\', '/')
    }
}
