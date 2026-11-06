package echo.music.iad1tya

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DeepLinkParserTest {
    @Test
    fun roomLinksResolveToRoomCodes() {
        val target = DeepLinkParser.parse("https://notune.app/room/abc123")

        assertEquals(DeepLinkTarget.Kind.ROOM, target.kind)
        assertEquals("abc123", target.roomCode)
    }

    @Test
    fun songAndPlaylistLinksResolveToIds() {
        val song = "https://notune.app/song/abc123"
        val playlist = "https://notune.app/playlist?list=PL123"

        assertEquals(DeepLinkTarget.Kind.SONG, DeepLinkParser.parse(song).kind)
        assertEquals("abc123", DeepLinkParser.parse(song).id)
        assertEquals(DeepLinkTarget.Kind.PLAYLIST, DeepLinkParser.parse(playlist).kind)
        assertEquals("PL123", DeepLinkParser.parse(playlist).id)
    }

    @Test
    fun githubRepositoryUrlsDoNotBecomeRoomInvites() {
        val target = DeepLinkParser.parse("https://github.com/MrDSP18/notune")

        assertEquals(DeepLinkTarget.Kind.UNKNOWN, target.kind)
        assertNull(target.roomCode)
    }

    @Test
    fun youtubeLinksResolveToVideoIds() {
        val target = DeepLinkParser.parse("https://youtube.com/watch?v=abcXYZ")

        assertEquals(DeepLinkTarget.Kind.YOUTUBE_VIDEO, target.kind)
        assertEquals("abcXYZ", target.id)
    }
}
