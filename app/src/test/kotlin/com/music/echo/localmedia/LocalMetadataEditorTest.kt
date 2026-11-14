package com.music.echo.localmedia

import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.ArtistEntity
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.db.entities.SongArtistMap
import echo.music.iad1tya.db.entities.SongEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LocalMetadataEditorTest {

    private lateinit var database: MusicDatabase

    @Before
    fun setUp() {
        database = mockk(relaxed = true)
        coEvery { database.withTransaction(any()) } coAnswers {
            val block = firstArg<suspend MusicDatabase.() -> Unit>()
            database.block()
        }
    }

    @Test
    fun testUpdateSongMetadataSuccess() = runTest {
        val existingSong = Song(
            song = SongEntity(
                id = "song-1",
                title = "Old Title",
                albumName = "Old Album",
                duration = 200,
                isLocal = true
            ),
            artists = listOf(ArtistEntity(id = "artist-old", name = "Old Artist"))
        )

        coEvery { database.getSongsByIds(listOf("song-1")) } returns listOf(existingSong)

        val success = LocalMetadataEditor.updateSongMetadata(
            songId = "song-1",
            newTitle = "New Title",
            newArtistName = "New Artist",
            newAlbumName = "New Album",
            database = database
        )

        assertTrue("Update should succeed", success)
        val updatedSongSlot = slot<SongEntity>()
        coVerify { database.update(capture(updatedSongSlot)) }
        assertEquals("New Title", updatedSongSlot.captured.title)
        assertEquals("New Album", updatedSongSlot.captured.albumName)
        coVerify { database.deleteSongArtistMaps("song-1") }
    }

    @Test
    fun testUpdateSongMetadataDetailedReportsAccurateContract() = runTest {
        val existingSong = Song(
            song = SongEntity(
                id = "song-2",
                title = "Original Title",
                albumName = "Original Album",
                duration = 180,
                isLocal = true
            ),
            artists = emptyList()
        )

        coEvery { database.getSongsByIds(listOf("song-2")) } returns listOf(existingSong)

        val result = LocalMetadataEditor.updateSongMetadataDetailed(
            songId = "song-2",
            newTitle = "Refined Title",
            newArtistName = "Refined Artist",
            newAlbumName = null,
            database = database
        )

        assertTrue(result.success)
        assertEquals(1, result.updatedCount)
        assertTrue("Database sync must be confirmed", result.isDatabaseSynced)
        assertFalse("Must never falsely claim physical file tags were written when only Room was updated", result.isPhysicalFileUpdated)
    }

    @Test
    fun testBulkUpdateSongMetadataProcessesBatch() = runTest {
        val song1 = Song(
            song = SongEntity(id = "s1", title = "T1", albumName = "A1", duration = 100),
            artists = emptyList()
        )
        val song2 = Song(
            song = SongEntity(id = "s2", title = "T2", albumName = "A2", duration = 120),
            artists = emptyList()
        )

        coEvery { database.getSongsByIds(listOf("s1")) } returns listOf(song1)
        coEvery { database.getSongsByIds(listOf("s2")) } returns listOf(song2)

        val updates = listOf(
            SongMetadataUpdate("s1", "T1 New", "Artist 1", "A1 New"),
            SongMetadataUpdate("s2", "T2 New", "Artist 2", "A2 New")
        )

        val result = LocalMetadataEditor.bulkUpdateSongMetadata(updates, database)

        assertTrue(result.success)
        assertEquals(2, result.updatedCount)
        assertTrue(result.isDatabaseSynced)
        assertFalse(result.isPhysicalFileUpdated)
    }

    @Test
    fun testBulkUpdateSongMetadataHandlesTransactionFailure() = runTest {
        coEvery { database.withTransaction(any()) } throws RuntimeException("DB Disk Full")

        val updates = listOf(
            SongMetadataUpdate("s1", "T1 New", "Artist 1", "A1 New")
        )

        val result = LocalMetadataEditor.bulkUpdateSongMetadata(updates, database)

        assertFalse(result.success)
        assertEquals(0, result.updatedCount)
        assertFalse(result.isDatabaseSynced)
        assertTrue(result.message?.contains("Transaction aborted") == true)
    }
}
