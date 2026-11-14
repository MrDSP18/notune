package com.music.echo.localmedia

import echo.music.iad1tya.db.entities.ArtistEntity
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.db.entities.SongEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalMediaAlgorithmsTest {
    @Test
    fun duplicateNormalizationPreservesTamilLetters() {
        assertEquals("வணக்கம்", DuplicateSongDetector.normalize("வணக்கம்"))
    }

    @Test
    fun duplicateNormalizationIgnoresPunctuationAndSpacing() {
        assertEquals(
            DuplicateSongDetector.normalize("A.R. Rahman - Song!"),
            DuplicateSongDetector.normalize("a r rahman song")
        )
    }

    @Test
    fun recommendationsRespectNonPositiveLimit() {
        assertTrue(LocalRecommendationEngine.rankSongs(listOf(track("1", "One", "Artist", 10L)), 0).isEmpty())
    }

    @Test
    fun recommendationsHandleZeroPlayHistoryWithoutNaN() {
        val ranked = LocalRecommendationEngine.rankSongs(
            listOf(track("1", "One", "Artist A", 0L), track("2", "Two", "Artist B", 0L)),
            2
        )
        assertEquals(2, ranked.size)
    }

    @Test
    fun recommendationsDiversifyArtistsAfterStrongPersonalMatch() {
        val songs = listOf(
            track("1", "Favorite", "Artist A", 120L, liked = true, album = "Album A"),
            track("2", "Another A", "Artist A", 90L, album = "Album A"),
            track("3", "Artist B", "Artist B", 60L, album = "Album B")
        )
        val ranked = LocalRecommendationEngine.rankSongs(songs, 3)
        assertEquals(3, ranked.size)
        assertEquals("1", ranked.first().song.id)
        assertEquals("Artist B", ranked[1].artists.first().name)
    }

    @Test
    fun smartPlaylistsRespectRequestedLimit() {
        val songs = listOf(
            track("1", "One", "Artist A", 10L, liked = true),
            track("2", "Two", "Artist B", 20L, liked = true),
            track("3", "Three", "Artist C", 30L, liked = true)
        )
        assertEquals(
            2,
            SmartPlaylistEngine.selectSongs(songs, SmartPlaylistType.FAVORITES, 2).size
        )
        assertTrue(SmartPlaylistEngine.selectSongs(songs, SmartPlaylistType.NEVER_PLAYED, 0).isEmpty())
    }

    private fun track(
        id: String,
        title: String,
        artist: String,
        playTime: Long,
        liked: Boolean = false,
        album: String = "Unknown"
    ) = Song(
        song = SongEntity(
            id = id,
            title = title,
            albumName = album,
            totalPlayTime = playTime,
            liked = liked
        ),
        artists = listOf(ArtistEntity(id = "artist-$artist", name = artist))
    )
}
