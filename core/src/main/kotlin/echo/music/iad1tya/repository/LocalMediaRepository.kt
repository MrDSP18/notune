package echo.music.iad1tya.repository

import echo.music.iad1tya.models.LocalAlbum
import echo.music.iad1tya.models.LocalArtist
import echo.music.iad1tya.models.LocalSong
import kotlinx.coroutines.flow.Flow

interface LocalMediaRepository {
    fun getSongs(): Flow<List<LocalSong>>
    fun getAlbums(): Flow<List<LocalAlbum>>
    fun getArtists(): Flow<List<LocalArtist>>
    suspend fun startScan(): Result<Unit>
}
