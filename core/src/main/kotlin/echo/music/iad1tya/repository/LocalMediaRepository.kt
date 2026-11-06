package echo.music.iad1tya.repository

import android.net.Uri
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.models.LocalAlbum
import echo.music.iad1tya.models.LocalArtist
import echo.music.iad1tya.models.LocalDuplicateGroup
import echo.music.iad1tya.models.LocalFolderNode
import echo.music.iad1tya.models.LocalSmartPlaylistType
import echo.music.iad1tya.models.LocalSong
import kotlinx.coroutines.flow.Flow

interface LocalMediaRepository {
    fun getSongs(): Flow<List<LocalSong>>
    fun getAlbums(): Flow<List<LocalAlbum>>
    fun getArtists(): Flow<List<LocalArtist>>
    suspend fun startScan(): Result<Unit>
    suspend fun scanSafFolder(treeUri: Uri): Result<Int>
    suspend fun getFolders(): List<LocalFolderNode>
    suspend fun getSongsInFolder(path: String): List<Song>
    suspend fun updateSongMetadata(songId: String, title: String, artist: String, album: String?): Boolean
    suspend fun getDuplicates(): List<LocalDuplicateGroup>
    suspend fun getSmartPlaylist(type: LocalSmartPlaylistType): List<Song>
    suspend fun getRecommendations(): List<Song>
}
