package com.music.echo.repository

import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.localmedia.LocalSongScanner
import echo.music.iad1tya.models.LocalAlbum
import echo.music.iad1tya.models.LocalArtist
import echo.music.iad1tya.models.LocalSong
import echo.music.iad1tya.repository.LocalMediaRepository
import echo.music.iad1tya.constants.AlbumSortType
import echo.music.iad1tya.constants.ArtistSortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalMediaRepositoryImpl @Inject constructor(
    private val database: MusicDatabase,
    private val scanner: LocalSongScanner
) : LocalMediaRepository {

    override fun getSongs(): Flow<List<LocalSong>> = 
        database.localSongs().map { songs ->
            songs.map { song ->
                LocalSong(
                    id = song.song.id,
                    title = song.song.title,
                    artists = song.artists.map { it.name },
                    albumTitle = song.song.albumName,
                    durationSeconds = song.song.duration,
                    thumbnailUrl = song.thumbnailUrl,
                    mimeType = song.format?.mimeType ?: "audio/*",
                    sizeBytes = song.format?.contentLength ?: 0L,
                    dateModified = song.song.dateModified
                )
            }
        }

    override fun getAlbums(): Flow<List<LocalAlbum>> =
        database.albums(AlbumSortType.NAME, false).map { albums ->
            albums.filter { it.album.isLocal }.map { album ->
                LocalAlbum(
                    id = album.album.id,
                    title = album.album.title,
                    artists = album.artists.map { it.name },
                    songCount = album.album.songCount,
                    durationSeconds = album.album.duration,
                    thumbnailUrl = album.thumbnailUrl
                )
            }
        }

    override fun getArtists(): Flow<List<LocalArtist>> =
        database.artists(ArtistSortType.NAME, false).map { artists ->
            artists.filter { it.artist.isLocal }.map { artist ->
                LocalArtist(
                    id = artist.artist.id,
                    name = artist.artist.name,
                    songCount = artist.songCount,
                    thumbnailUrl = artist.artist.thumbnailUrl
                )
            }
        }

    override suspend fun startScan(): Result<Unit> = runCatching {
        scanner.scanDevice()
        Unit
    }
}
