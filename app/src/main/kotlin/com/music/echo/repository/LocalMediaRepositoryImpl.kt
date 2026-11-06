package com.music.echo.repository

import android.net.Uri
import com.music.echo.localmedia.DuplicateSongDetector
import com.music.echo.localmedia.LocalFolderManager
import com.music.echo.localmedia.LocalMetadataEditor
import com.music.echo.localmedia.LocalRecommendationEngine
import com.music.echo.localmedia.SafFolderScanner
import com.music.echo.localmedia.SmartPlaylistEngine
import com.music.echo.localmedia.SmartPlaylistType
import echo.music.iad1tya.constants.AlbumSortType
import echo.music.iad1tya.constants.ArtistSortType
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.localmedia.LocalSongScanner
import echo.music.iad1tya.models.LocalAlbum
import echo.music.iad1tya.models.LocalArtist
import echo.music.iad1tya.models.LocalDuplicateGroup
import echo.music.iad1tya.models.LocalFolderNode
import echo.music.iad1tya.models.LocalSmartPlaylistType
import echo.music.iad1tya.models.LocalSong
import echo.music.iad1tya.repository.LocalMediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalMediaRepositoryImpl @Inject constructor(
    private val database: MusicDatabase,
    private val scanner: LocalSongScanner,
    private val safScanner: SafFolderScanner,
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

    override suspend fun scanSafFolder(treeUri: Uri): Result<Int> = runCatching {
        safScanner.scanFolderUri(treeUri)
    }

    override suspend fun getFolders(): List<LocalFolderNode> {
        return LocalFolderManager.getFolderHierarchy(database).map { folder ->
            LocalFolderNode(
                path = folder.path,
                name = folder.name,
                songCount = folder.songCount,
                directSongs = folder.directSongs
            )
        }
    }

    override suspend fun getSongsInFolder(path: String): List<Song> {
        return LocalFolderManager.getSongsInFolder(path, database)
    }

    override suspend fun updateSongMetadata(
        songId: String,
        title: String,
        artist: String,
        album: String?
    ): Boolean {
        return LocalMetadataEditor.updateSongMetadata(songId, title, artist, album, database)
    }

    override suspend fun getDuplicates(): List<LocalDuplicateGroup> {
        return DuplicateSongDetector.findDuplicateSongs(database).map { dup ->
            LocalDuplicateGroup(
                titleKey = dup.titleKey,
                primaryArtist = dup.primaryArtist,
                duplicates = dup.duplicates
            )
        }
    }

    override suspend fun getSmartPlaylist(type: LocalSmartPlaylistType): List<Song> {
        val engineType = when (type) {
            LocalSmartPlaylistType.RECENTLY_ADDED -> SmartPlaylistType.RECENTLY_ADDED
            LocalSmartPlaylistType.MOST_PLAYED -> SmartPlaylistType.MOST_PLAYED
            LocalSmartPlaylistType.FAVORITES -> SmartPlaylistType.FAVORITES
            LocalSmartPlaylistType.NEVER_PLAYED -> SmartPlaylistType.NEVER_PLAYED
        }
        return SmartPlaylistEngine.getSmartPlaylistSongs(engineType, database)
    }

    override suspend fun getRecommendations(): List<Song> {
        return LocalRecommendationEngine.getLocalRecommendations(database)
    }
}
