package com.music.echo.notune.localmedia

import echo.music.iad1tya.localmedia.SupportedLocalAudio
import echo.music.iad1tya.models.LocalDuplicateGroup
import echo.music.iad1tya.models.LocalFolderNode
import echo.music.iad1tya.models.LocalSmartPlaylistType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalLibraryAndManagementTest {

    @Test
    fun testSupportedAudioFormatExtensions() {
        assertTrue(SupportedLocalAudio.isSupported("song.flac", "audio/flac"))
        assertTrue(SupportedLocalAudio.isSupported("track.mp3", "audio/mpeg"))
        assertTrue(SupportedLocalAudio.isSupported("music.wav", "audio/wav"))
        assertTrue(SupportedLocalAudio.isSupported("audio.m4a", "audio/x-m4a"))
        assertTrue(SupportedLocalAudio.isSupported("sound.opus", "audio/opus"))
        assertTrue(SupportedLocalAudio.isSupported("voice.ogg", "audio/ogg"))
        assertFalse(SupportedLocalAudio.isSupported("corrupted.ape", "audio/x-ape"))
    }

    @Test
    fun testLocalFolderNodeStructure() {
        val folder = LocalFolderNode(
            path = "/sdcard/Music/Tamil",
            name = "Tamil",
            songCount = 25,
            directSongs = emptyList()
        )

        assertEquals("Tamil", folder.name)
        assertEquals(25, folder.songCount)
    }

    @Test
    fun testLocalDuplicateGroupStructure() {
        val group = LocalDuplicateGroup(
            titleKey = "Master Anirudh",
            primaryArtist = "Anirudh Ravichander",
            duplicates = emptyList()
        )

        assertEquals("Master Anirudh", group.titleKey)
        assertEquals("Anirudh Ravichander", group.primaryArtist)
    }

    @Test
    fun testSmartPlaylistTypeEnum() {
        assertEquals(4, LocalSmartPlaylistType.entries.size)
        assertTrue(LocalSmartPlaylistType.RECENTLY_ADDED in LocalSmartPlaylistType.entries)
        assertTrue(LocalSmartPlaylistType.MOST_PLAYED in LocalSmartPlaylistType.entries)
        assertTrue(LocalSmartPlaylistType.FAVORITES in LocalSmartPlaylistType.entries)
        assertTrue(LocalSmartPlaylistType.NEVER_PLAYED in LocalSmartPlaylistType.entries)
    }
}
