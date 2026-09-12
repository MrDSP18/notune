package echo.music.iad1tya.notune

import com.music.echo.notune.ai.AiPlaylistDoctor
import com.music.echo.notune.ai.ConversationalPlaybackEngine
import com.music.echo.notune.audio.HeadphoneIntelligenceManager
import com.music.echo.notune.flow.MoodJourneyEngine
import com.music.echo.notune.flow.MoodJourneyStage
import echo.music.iad1tya.db.entities.ArtistEntity
import echo.music.iad1tya.db.entities.Song
import echo.music.iad1tya.db.entities.SongEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NextGenFeaturesTest {

    private val playlistDoctor = AiPlaylistDoctor()
    private val conversationalEngine = ConversationalPlaybackEngine()
    private val headphoneManager = HeadphoneIntelligenceManager()
    private val moodJourneyEngine = MoodJourneyEngine()

    @Test
    fun testAiPlaylistDoctor_detectsArtistDominance() {
        val dominantArtist = ArtistEntity(id = "artist_1", name = "Dominant Artist")
        val sampleSongs = (1..10).map { id ->
            Song(
                song = SongEntity(
                    id = "song_$id",
                    title = "Title $id"
                ),
                artists = listOf(dominantArtist)
            )
        }

        val diagnosis = playlistDoctor.diagnosePlaylist("My Playlist", sampleSongs)

        assertEquals("Dominant Artist", diagnosis.dominantArtist)
        assertTrue(diagnosis.issues.any { it.contains("dominates") })
        assertTrue(diagnosis.healthScore < 100)
    }

    @Test
    fun testConversationalPlaybackEngine_parsesCalmQuery() {
        val query = "Play something calm and relaxing for studying"
        val intent = conversationalEngine.parseConversationalRequest(query)

        assertEquals("calm", intent.mood)
        assertTrue(intent.targetEnergy <= 0.4f)
    }

    @Test
    fun testHeadphoneIntelligence_detectsAirPodsProfile() {
        val profile = headphoneManager.detectProfileFromDeviceName("Dharan's AirPods Pro")
        assertEquals("AirPods / Wireless", profile.deviceName)
        assertTrue(profile.spatialAudio)
    }

    @Test
    fun testMoodJourneyEngine_advancesThroughStages() {
        moodJourneyEngine.startJourney()
        assertEquals(MoodJourneyStage.CALM, moodJourneyEngine.getCurrentStage())

        val nextStage = moodJourneyEngine.advanceStage()
        assertEquals(MoodJourneyStage.CHILL, nextStage)
    }
}
