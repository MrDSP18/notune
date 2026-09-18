package echo.music.iad1tya.extensions

import android.net.Uri
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.db.entities.SongEntity
import echo.music.iad1tya.db.entities.ArtistEntity
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import androidx.core.net.toUri

class MediaItemExtTest {

    @Before
    fun setUp() {
        mockkStatic(Uri::class)
        mockkStatic("androidx.core.net.UriKt")
        
        every { Uri.parse(any()) } answers {
            val path = it.invocation.args[0] as String
            mockk<Uri> {
                every { toString() } returns path
                every { scheme } returns path.substringBefore(":", "")
            }
        }
        
        every { any<String>().toUri() } answers {
            val path = it.invocation.args[0] as String
            mockk<Uri> {
                every { toString() } returns path
                every { scheme } returns path.substringBefore(":", "")
            }
        }
        
        every { Uri.encode(any()) } answers { it.invocation.args[0] as String }
    }

    @Test
    fun `test local song to MediaItem mapping`() {
        val localId = "content://media/external/audio/media/1"
        val songEntity = SongEntity(
            id = localId,
            title = "Test Song",
            duration = 180,
            isLocal = true
        )
        val artists = listOf(ArtistEntity(id = "a1", name = "Artist 1"))
        val song = Song(song = songEntity, artists = artists, album = null)
        
        val mediaItem = song.toMediaItem()
        
        assertEquals(localId, mediaItem.mediaId)
        assertEquals(localId, mediaItem.localConfiguration?.uri.toString())
        assertEquals("Test Song", mediaItem.mediaMetadata.title)
    }
}
