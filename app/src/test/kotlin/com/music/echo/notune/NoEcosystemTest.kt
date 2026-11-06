package com.music.echo.notune

import com.music.echo.notune.link.NoLinkEngine
import com.music.echo.notune.link.NoLinkType
import com.music.echo.notune.release.NoReleaseEngine
import com.music.echo.notune.release.ReleaseInfo
import com.music.echo.notune.social.connect.NoQrEngine
import com.music.echo.notune.social.connect.QrPayloadType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NoEcosystemTest {

    @Test
    fun testNoLinkGenerationAndParsing() {
        val songLink = NoLinkEngine.generateSongLink("s123", "Munbe Vaa", "A.R. Rahman")
        assertTrue(songLink.canonicalUrl.startsWith("https://notune.app/s/"))

        val parsed = NoLinkEngine.parseNoLink(songLink.canonicalUrl)
        assertNotNull(parsed)
        assertEquals(NoLinkType.SONG, parsed?.type)

        val profileLink = NoLinkEngine.generateProfileLink("@dharan")
        assertEquals("https://notune.app/u/dharan", profileLink.canonicalUrl)

        val parsedProfile = NoLinkEngine.parseNoLink(profileLink.canonicalUrl)
        assertNotNull(parsedProfile)
        assertEquals(NoLinkType.PROFILE, parsedProfile?.type)
    }

    @Test
    fun testNoQrPayloadEncodingAndDecoding() {
        val userPayload = NoQrEngine.generateUserPayload("priya")
        assertEquals("NOTUNE:USER:priya", userPayload)

        val decodedUser = NoQrEngine.parsePayload(userPayload)
        assertEquals(QrPayloadType.USER_PROFILE, decodedUser.type)
        assertEquals("priya", decodedUser.identifier)

        val roomPayload = NoQrEngine.generateRoomPayload("room_99")
        val decodedRoom = NoQrEngine.parsePayload(roomPayload)
        assertEquals(QrPayloadType.LIVE_ROOM, decodedRoom.type)
        assertEquals("room_99", decodedRoom.identifier)
    }

    @Test
    fun testNoReleaseEngineUpdateCheck() {
        val latestApproved = ReleaseInfo(
            versionName = "3.2.0",
            versionCode = 30200,
            channel = "stable",
            approved = true,
            downloadUrl = "https://github.com/MrDSP18/notune/releases/download/v3.2.0/notune.apk",
            sha256 = "abc123hash",
            releaseNotes = "New V3 release features"
        )

        val check = NoReleaseEngine.checkForUpdate(latestApproved)
        assertTrue(check.isUpdateAvailable)
        assertEquals("3.2.0", check.latestRelease?.versionName)

        val sameRelease = latestApproved.copy(versionCode = 30100)
        val sameCheck = NoReleaseEngine.checkForUpdate(sameRelease)
        assertFalse(sameCheck.isUpdateAvailable)

        assertTrue(NoReleaseEngine.verifySha256Checksum("ABC123HASH", "abc123hash"))
    }
}
