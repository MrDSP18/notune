package com.music.echo.notune.intelligence

import com.music.echo.notune.intelligence.intent.IntentEngine
import com.music.echo.notune.intelligence.intent.IntentType
import com.music.echo.notune.intelligence.musicbrain.MusicClassifierEngine
import com.music.echo.notune.intelligence.musicbrain.MusicIdentityEngine
import com.music.echo.notune.intelligence.personalization.PreferenceDecay
import com.music.echo.notune.intelligence.personalization.TasteProfileStore
import com.music.echo.notune.intelligence.recommendation.CentralizedRankingEngine
import com.music.echo.notune.intelligence.recommendation.DiversityController
import com.music.echo.notune.intelligence.recommendation.RankedTrackResult
import echo.music.iad1tya.notune.provider.UnifiedTrack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NotuneIntelligenceSystemTest {

    @Test
    fun `test IntentEngine natural language parsing`() {
        val intentEngine = IntentEngine()

        val intent1 = intentEngine.parseIntent("Play something energetic")
        assertEquals(IntentType.MODIFY_QUEUE, intent1.intentType)
        assertEquals("Energetic", intent1.targetMood)
        assertNotNull(intent1.constraints.minEnergy)

        val intent2 = intentEngine.parseIntent("Play more Tamil songs")
        assertEquals(IntentType.FILTER_QUEUE_LANGUAGE, intent2.intentType)
        assertEquals("Tamil", intent2.targetLanguage)

        val intent3 = intentEngine.parseIntent("Don't play Taylor Swift anymore today")
        assertEquals(IntentType.SESSION_EXCLUSION, intent3.intentType)
        assertTrue(intent3.constraints.excludeArtists.contains("Taylor Swift"))

        val intent4 = intentEngine.parseIntent("Give me songs I haven't heard")
        assertEquals(IntentType.DISCOVER_UNHEARD, intent4.intentType)
        assertTrue(intent4.constraints.discoveryRequested)
    }

    @Test
    fun `test MusicIdentityEngine deduplication`() {
        val identityEngine = MusicIdentityEngine()

        val track1 = UnifiedTrack(
            id = "t1", title = "Munbe Vaa", artist = "A.R. Rahman",
            providerName = "Local", providerTrackId = "1"
        )
        val track2 = UnifiedTrack(
            id = "t2", title = "Munbe Vaa", artist = "A.R. Rahman",
            providerName = "YouTube", providerTrackId = "yt_123"
        )

        val deduplicated = identityEngine.deduplicateTracks(listOf(track1, track2))
        assertEquals(1, deduplicated.size)
        assertEquals("Munbe Vaa", deduplicated.first().title)
    }

    @Test
    fun `test CentralizedRankingEngine score and explanations`() {
        val classifier = MusicClassifierEngine()
        val tasteProfileStore = TasteProfileStore(PreferenceDecay())
        val rankingEngine = CentralizedRankingEngine(classifier, tasteProfileStore)
        val intentEngine = IntentEngine()

        val intent = intentEngine.parseIntent("Play Tamil songs by A.R. Rahman")
        val track1 = UnifiedTrack(id = "1", title = "Munbe Vaa (Tamil)", artist = "A.R. Rahman", providerName = "Test", providerTrackId = "1")
        val track2 = UnifiedTrack(id = "2", title = "Random Song", artist = "Band X", providerName = "Test", providerTrackId = "2")

        val snapshot = com.music.echo.notune.intelligence.context.NotuneContextSnapshot(
            playback = com.music.echo.notune.intelligence.context.PlaybackStateSnapshot(),
            session = com.music.echo.notune.intelligence.context.SessionStateSnapshot(),
            user = com.music.echo.notune.intelligence.context.UserStateSnapshot(),
            environment = com.music.echo.notune.intelligence.context.EnvironmentStateSnapshot(),
            room = com.music.echo.notune.intelligence.context.RoomStateSnapshot()
        )

        val results = rankingEngine.rankCandidates(listOf(track1, track2), intent, snapshot)
        assertEquals(2, results.size)
        assertEquals("Munbe Vaa (Tamil)", results.first().track.title)
        assertTrue(results.first().explanation.contains("Tamil"))
    }

    @Test
    fun `test DiversityController saturation prevention`() {
        val diversityController = DiversityController()
        val track1 = UnifiedTrack(id = "1", title = "Song 1", artist = "Same Artist", providerName = "T", providerTrackId = "1")
        val track2 = UnifiedTrack(id = "2", title = "Song 2", artist = "Same Artist", providerName = "T", providerTrackId = "2")
        val track3 = UnifiedTrack(id = "3", title = "Song 3", artist = "Same Artist", providerName = "T", providerTrackId = "3")
        val track4 = UnifiedTrack(id = "4", title = "Other Song", artist = "Different Artist", providerName = "T", providerTrackId = "4")

        val candidates = listOf(
            RankedTrackResult(track1, 0.9f, "exp"),
            RankedTrackResult(track2, 0.85f, "exp"),
            RankedTrackResult(track3, 0.8f, "exp"),
            RankedTrackResult(track4, 0.75f, "exp")
        )

        val filtered = diversityController.applyDiversityFilter(candidates, maxPerArtist = 2)
        assertEquals(3, filtered.size)
        assertTrue(filtered.any { it.track.artist == "Different Artist" })
    }
}
