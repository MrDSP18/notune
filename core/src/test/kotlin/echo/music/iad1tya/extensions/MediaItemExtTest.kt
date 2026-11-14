package echo.music.iad1tya.extensions

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata as FrameworkMediaMetadata
import echo.music.iad1tya.db.entities.ArtistEntity
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.db.entities.SongEntity
import echo.music.iad1tya.models.MediaMetadata
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [32])
class MediaItemExtTest {

    @Test
    fun testLocalSongEntityStructure() {
        val localId = "content://media/external/audio/media/1"
        val songEntity = SongEntity(
            id = localId,
            title = "Test Song",
            duration = 180,
            isLocal = true
        )
        val artists = listOf(ArtistEntity(id = "a1", name = "Artist 1"))
        val song = Song(song = songEntity, artists = artists, album = null)

        assertEquals(localId, song.song.id)
        assertEquals("Test Song", song.song.title)
        assertEquals(true, song.song.isLocal)
    }

    @Test
    fun testSafeMetadata_withAttachedTag_returnsAttachedMetadata() {
        val attached = MediaMetadata(
            id = "custom_id",
            title = "Attached Title",
            artists = listOf(MediaMetadata.Artist(id = "1", name = "Singer")),
            duration = 240
        )
        val mediaItem = MediaItem.Builder()
            .setMediaId("custom_id")
            .setUri("https://example.com/audio.mp3")
            .setTag(attached)
            .build()

        val safe = mediaItem.safeMetadata
        assertEquals("custom_id", safe.id)
        assertEquals("Attached Title", safe.title)
        assertEquals(240, safe.duration)
        assertEquals("Singer", safe.artists.first().name)
    }

    @Test
    fun testSafeMetadata_withNoTag_fallsBackGracefully() {
        val mediaItem = MediaItem.Builder()
            .setMediaId("song_xyz")
            .setMediaMetadata(
                FrameworkMediaMetadata.Builder()
                    .setTitle("Framework Title")
                    .setArtist("Artist Alpha, Artist Beta")
                    .build()
            )
            .build()

        val safe = mediaItem.safeMetadata
        assertNotNull(safe)
        assertEquals("song_xyz", safe.id)
        assertEquals("Framework Title", safe.title)
        assertEquals(2, safe.artists.size)
        assertEquals("Artist Alpha", safe.artists[0].name)
        assertEquals("Artist Beta", safe.artists[1].name)
        assertEquals(0, safe.duration)
    }

    @Test
    fun testSafeMetadata_withEmptyFrameworkMetadata_usesMediaIdAsTitle() {
        val mediaItem = MediaItem.Builder()
            .setMediaId("song_fallback_only")
            .build()

        val safe = mediaItem.safeMetadata
        assertNotNull(safe)
        assertEquals("song_fallback_only", safe.id)
        assertEquals("song_fallback_only", safe.title)
        assertEquals(0, safe.artists.size)
        assertEquals(0, safe.duration)
        assertNull(safe.thumbnailUrl)
    }
}
