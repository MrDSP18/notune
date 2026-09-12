package com.music.echo.notune.privacy.export

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataExporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: MusicDatabase
) {

    suspend fun generateDataExport(): File = withContext(Dispatchers.IO) {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val zipFile = File(exportDir, "notune-data-export-${System.currentTimeMillis()}.zip")

        ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
            // 1. Export Metadata & Profile
            val profileJson = JSONObject().apply {
                put("exportVersion", "1.0")
                put("generatedAt", Instant.now().toString())
                put("app", "NØTUNE Music")
                put("dataOwnerStatement", "YOUR MUSIC TASTE BELONGS TO YOU.")
            }
            writeZipEntry(zos, "profile.json", profileJson.toString(2))

            // 2. Favorites
            val likedSongs = database.likedSongs(echo.music.iad1tya.constants.SongSortType.CREATE_DATE, true).first()
            val favoritesArray = JSONArray()
            for (song in likedSongs) {
                favoritesArray.put(JSONObject().apply {
                    put("id", song.id)
                    put("title", song.title)
                    put("artist", song.artists.joinToString { it.name })
                    put("album", song.album?.title ?: "")
                })
            }
            val favoritesJson = JSONObject().apply {
                put("totalCount", likedSongs.size)
                put("items", favoritesArray)
            }
            writeZipEntry(zos, "favorites.json", favoritesJson.toString(2))

            // 3. Playlists
            val playlists = database.playlists(echo.music.iad1tya.constants.PlaylistSortType.CREATE_DATE, true).first()
            val playlistsArray = JSONArray()
            for (pl in playlists) {
                playlistsArray.put(JSONObject().apply {
                    put("id", pl.playlist.id)
                    put("name", pl.playlist.name)
                    put("songCount", pl.songCount)
                })
            }
            val playlistsJson = JSONObject().apply {
                put("totalCount", playlists.size)
                put("items", playlistsArray)
            }
            writeZipEntry(zos, "playlists.json", playlistsJson.toString(2))

            // 4. Listening History
            val topSongs = database.topSongs(100).first()
            val historyArray = JSONArray()
            for (song in topSongs) {
                historyArray.put(JSONObject().apply {
                    put("id", song.id)
                    put("title", song.title)
                    put("artist", song.artists.joinToString { it.name })
                })
            }
            val historyJson = JSONObject().apply {
                put("totalHistoryItemsCount", topSongs.size)
                put("topSongs", historyArray)
            }
            writeZipEntry(zos, "history.json", historyJson.toString(2))

            // 5. Preferences
            val preferencesJson = JSONObject().apply {
                put("localPersonalization", true)
                put("privateSessionDefault", false)
                put("flowQueueTarget", 15)
            }
            writeZipEntry(zos, "preferences.json", preferencesJson.toString(2))

            // 6. Music DNA
            val dnaJson = JSONObject().apply {
                put("energyProfile", "Balanced")
                put("discoveryScore", 75)
                put("topGenres", JSONArray(listOf("Pop", "Rock", "Lo-Fi")))
            }
            writeZipEntry(zos, "music-dna.json", dnaJson.toString(2))

            // 7. NØTUNE FLOW
            val flowJson = JSONObject().apply {
                put("flowEnabled", true)
                put("discoveryRatio", 0.3)
                put("antiRepetitionEnabled", true)
            }
            writeZipEntry(zos, "flow.json", flowJson.toString(2))

            // 8. Rooms
            val roomsJson = JSONObject().apply {
                put("activeRoomsCount", 0)
                put("publicRoomHistory", JSONArray())
            }
            writeZipEntry(zos, "rooms.json", roomsJson.toString(2))

            // 9. Couple Data
            val coupleJson = JSONObject().apply {
                put("coupleSessionActive", false)
                put("ourSongs", JSONArray())
            }
            writeZipEntry(zos, "couple-data.json", coupleJson.toString(2))
        }

        zipFile
    }

    private fun writeZipEntry(zos: ZipOutputStream, fileName: String, content: String) {
        val entry = ZipEntry(fileName)
        zos.putNextEntry(entry)
        zos.write(content.toByteArray(Charsets.UTF_8))
        zos.closeEntry()
    }
}
