package echo.music.iad1tya.playback

import android.net.Uri
import echo.music.iad1tya.db.entities.ArtistEntity
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.db.entities.SongEntity
import echo.music.iad1tya.extensions.toMediaItem
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LocalMediaPlaybackIntegrationTest {

    @Before
    fun setUp() {
        mockkStatic(Uri::class)
        // Since toUri() is inline, it calls Uri.parse() directly in bytecode.
        // So we only need to mock Uri.parse().
        every { Uri.parse(any()) } answers {
            val path = it.invocation.args[0] as String
            val u = mockk<Uri>(relaxed = true)
            every { u.toString() } returns path
            every { u.scheme } returns path.substringBefore(":", "")
            u
        }
        
        every { Uri.encode(any()) } answers { it.invocation.args[0] as String }
    }

    @After
    fun tearDown() {
        unmockkStatic(Uri::class)
    }

    @Test
    fun `test local song to MediaItem mapping preserved for playback`() {
        val localUri = "content://media/external/audio/media/123"
        val songEntity = SongEntity(
            id = localUri,
            title = "Local Song",
            duration = 200,
            isLocal = true
        )
        val artist = ArtistEntity(id = "a1", name = "Local Artist")
        val song = Song(song = songEntity, artists = listOf(artist), album = null)
        
        val mediaItem = song.toMediaItem()
        
        assertEquals(localUri, mediaItem.mediaId)
        assertEquals(localUri, mediaItem.localConfiguration?.uri.toString())
        assertEquals("Local Song", mediaItem.mediaMetadata.title)
    }
}
