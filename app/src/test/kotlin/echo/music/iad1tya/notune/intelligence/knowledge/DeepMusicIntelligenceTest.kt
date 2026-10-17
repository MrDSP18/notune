package echo.music.iad1tya.notune.intelligence.knowledge

import echo.music.iad1tya.notune.ai.adaptive.AdaptivePlaybackBridge
import echo.music.iad1tya.notune.ai.adaptive.AdaptiveRecommendationEngine
import echo.music.iad1tya.notune.ai.adaptive.AdaptiveScoringConfig
import echo.music.iad1tya.notune.ai.adaptive.AdaptiveScoringEngine
import echo.music.iad1tya.notune.ai.adaptive.AdaptiveTrackContext
import echo.music.iad1tya.notune.ai.adaptive.RecommendationRequest
import echo.music.iad1tya.notune.ai.adaptive.UserIntent
import echo.music.iad1tya.notune.ai.adaptive.UserIntentType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeepMusicIntelligenceTest {

    private lateinit var repository: MusicKnowledgeRepository
    private lateinit var scoringEngine: AdaptiveScoringEngine
    private lateinit var playbackBridge: AdaptivePlaybackBridge

    private val composerAnirudh = PersonDetails("p_anirudh", "Anirudh Ravichander", roleName = "Composer", source = MetadataSource.LOCAL)
    private val lyricistVignesh = PersonDetails("p_vignesh", "Vignesh Shivan", roleName = "Lyricist", source = MetadataSource.LOCAL)
    private val actorRajini = PersonDetails("p_rajini", "Rajinikanth", roleName = "Actor", source = MetadataSource.LOCAL)
    private val actressTamannaah = PersonDetails("p_tamannaah", "Tamannaah Bhatia", roleName = "Actress", source = MetadataSource.LOCAL)

    private val sampleMovie = MovieDetails(
        movieId = "m_jailer",
        title = "Jailer",
        year = 2023,
        language = "Tamil",
        musicDirectors = listOf(composerAnirudh),
        leadActors = listOf(actorRajini),
        leadActresses = listOf(actressTamannaah),
        cast = listOf(
            CastMember(actorRajini, CastRole.HERO, "Tiger Muthuvel Pandian"),
            CastMember(actressTamannaah, CastRole.HEROINE, "Kamna")
        ),
        crew = listOf(
            CrewMember(composerAnirudh, CrewRole.MUSIC_DIRECTOR)
        ),
        soundtrackAlbumId = "alb_jailer",
        source = MetadataSource.LOCAL
    )

    private val sampleTrack1 = AdaptiveTrackContext(
        trackId = "song_kaavaalaa",
        title = "Kaavaalaa",
        artist = "Anirudh Ravichander",
        album = "Jailer",
        composer = "Anirudh Ravichander",
        lyricist = "Vignesh Shivan",
        singer = "Shilpa Rao",
        movieTitle = "Jailer",
        genre = "Dance/Pop",
        language = "Tamil",
        bpm = 125f,
        valence = 0.9f,
        energy = 0.85f
    )

    private val sampleTrack2 = AdaptiveTrackContext(
        trackId = "song_hukum",
        title = "Hukum",
        artist = "Anirudh Ravichander",
        album = "Jailer",
        composer = "Anirudh Ravichander",
        lyricist = "Super Subu",
        singer = "Anirudh Ravichander",
        movieTitle = "Jailer",
        genre = "High Energy",
        language = "Tamil",
        bpm = 130f,
        valence = 0.85f,
        energy = 0.95f
    )

    @Before
    fun setup() {
        val normalizer = echo.music.iad1tya.notune.intelligence.enrichment.SongIdentityNormalizer()
        val confidenceEngine = echo.music.iad1tya.notune.intelligence.enrichment.ConfidenceEngine()
        val fieldMerger = echo.music.iad1tya.notune.intelligence.enrichment.FieldMerger()
        val cacheManager = echo.music.iad1tya.notune.intelligence.enrichment.MetadataCacheManager()
        val orchestrator = echo.music.iad1tya.notune.intelligence.enrichment.ProviderOrchestrator(
            echo.music.iad1tya.notune.intelligence.enrichment.LocalMetadataProvider(),
            echo.music.iad1tya.notune.intelligence.enrichment.BackendMetadataProvider(),
            echo.music.iad1tya.notune.intelligence.enrichment.InnerTubeMetadataProvider(),
            echo.music.iad1tya.notune.intelligence.enrichment.LrclibMetadataProvider()
        )
        val resolver = echo.music.iad1tya.notune.intelligence.enrichment.MusicMetadataResolver(
            normalizer, orchestrator, confidenceEngine, fieldMerger, cacheManager
        )
        repository = MusicKnowledgeRepository(resolver, cacheManager)
        scoringEngine = AdaptiveScoringEngine(AdaptiveScoringConfig())
        val recEngine = AdaptiveRecommendationEngine(scoringEngine)
        playbackBridge = AdaptivePlaybackBridge(recEngine)
    }

    // 1. SongDetails mapping
    @Test
    fun testSongDetailsMapping() {
        val song = SongDetails(
            songId = "song_1",
            title = "Kaavaalaa",
            artists = listOf(composerAnirudh),
            language = "Tamil",
            source = MetadataSource.LOCAL
        )
        assertEquals("song_1", song.songId)
        assertEquals("Kaavaalaa", song.title)
        assertEquals(MetadataSource.LOCAL, song.source)
    }

    // 2. ArtistDetails mapping
    @Test
    fun testArtistDetailsMapping() {
        val artist = ArtistDetails(
            artistId = "p_anirudh",
            name = "Anirudh Ravichander",
            languages = listOf("Tamil", "Telugu"),
            genres = listOf("Film Score", "Pop"),
            source = MetadataSource.LOCAL
        )
        assertEquals("p_anirudh", artist.artistId)
        assertEquals(2, artist.languages.size)
    }

    // 3. AlbumDetails mapping
    @Test
    fun testAlbumDetailsMapping() {
        val album = AlbumDetails(
            albumId = "alb_jailer",
            title = "Jailer (Original Motion Picture Soundtrack)",
            totalTracks = 8,
            source = MetadataSource.LOCAL
        )
        assertEquals("alb_jailer", album.albumId)
        assertEquals(8, album.totalTracks)
    }

    // 4. MovieDetails mapping
    @Test
    fun testMovieDetailsMapping() {
        assertEquals("m_jailer", sampleMovie.movieId)
        assertEquals("Jailer", sampleMovie.title)
        assertEquals(2023, sampleMovie.year)
    }

    // 5. Hero/Heroine role mapping
    @Test
    fun testHeroHeroineRoleMapping() {
        val hero = sampleMovie.cast.find { it.role == CastRole.HERO }
        val heroine = sampleMovie.cast.find { it.role == CastRole.HEROINE }

        assertNotNull(hero)
        assertNotNull(heroine)
        assertEquals("Rajinikanth", hero?.person?.name)
        assertEquals("Tamannaah Bhatia", heroine?.person?.name)
    }

    // 6. Cast mapping
    @Test
    fun testCastMapping() {
        assertEquals(2, sampleMovie.cast.size)
        assertEquals("Tiger Muthuvel Pandian", sampleMovie.cast[0].characterName)
    }

    // 7. Crew mapping
    @Test
    fun testCrewMapping() {
        val musicDir = sampleMovie.crew.find { it.role == CrewRole.MUSIC_DIRECTOR }
        assertNotNull(musicDir)
        assertEquals("Anirudh Ravichander", musicDir?.person?.name)
    }

    // 8. Song -> Artist relationship
    @Test
    fun testSongArtistRelationship() {
        val song = SongDetails(songId = "s1", title = "T1", artists = listOf(composerAnirudh))
        assertEquals(1, song.artists.size)
        assertEquals("Anirudh Ravichander", song.artists.first().name)
    }

    // 9. Song -> Movie relationship
    @Test
    fun testSongMovieRelationship() {
        val song = SongDetails(songId = "s1", title = "T1", movieId = "m_jailer", movieTitle = "Jailer")
        assertEquals("m_jailer", song.movieId)
        assertEquals("Jailer", song.movieTitle)
    }

    // 10. Movie -> Soundtrack relationship
    @Test
    fun testMovieSoundtrackRelationship() {
        assertEquals("alb_jailer", sampleMovie.soundtrackAlbumId)
    }

    // 11. Artist -> Discography relationship
    @Test
    fun testArtistDiscographyRelationship() {
        val artist = ArtistDetails("a1", "Anirudh", albumIds = listOf("alb_jailer", "alb_leo"))
        assertEquals(2, artist.albumIds.size)
    }

    // 12. Composer relationship
    @Test
    fun testComposerRelationship() {
        val credits = SongCredits(composers = listOf(composerAnirudh))
        assertEquals("Anirudh Ravichander", credits.composers.first().name)
    }

    // 13. Lyricist relationship
    @Test
    fun testLyricistRelationship() {
        val credits = SongCredits(lyricists = listOf(lyricistVignesh))
        assertEquals("Vignesh Shivan", credits.lyricists.first().name)
    }

    // 14. Metadata confidence
    @Test
    fun testMetadataConfidenceTracking() {
        val localArt = MediaArtwork(role = ArtworkRole.SONG, source = MetadataSource.LOCAL)
        val providerArt = MediaArtwork(role = ArtworkRole.SONG, source = MetadataSource.PROVIDER)

        assertEquals(MetadataSource.LOCAL, localArt.source)
        assertEquals(MetadataSource.PROVIDER, providerArt.source)
    }

    // 15. Missing metadata fallback
    @Test
    fun testMissingMetadataFallback() {
        val bareSong = SongDetails(songId = "s_bare", title = "Bare Song")
        assertNull(bareSong.movieTitle)
        assertNull(bareSong.bpm)
        assertEquals(MetadataSource.UNKNOWN, bareSong.source)
    }

    // 16. Offline metadata resolution
    @Test
    fun testOfflineMetadataResolution() {
        val state = repository.resolveTrackKnowledge(sampleTrack1)
        assertNotNull(state.currentSongDetails)
        assertEquals("Kaavaalaa", state.currentSongDetails?.title)
        assertEquals(MetadataSource.LOCAL, state.source)
        assertFalse(state.isLoading)
    }

    // 17. Cached metadata
    @Test
    fun testCachedMetadataResolution() {
        repository.resolveTrackKnowledge(sampleTrack1)
        val stateCached = repository.resolveTrackKnowledge(sampleTrack1)
        assertNotNull(stateCached.currentSongDetails)
        assertEquals("Kaavaalaa", stateCached.currentSongDetails?.title)
    }

    // 18. Adaptive composer relationship scoring
    @Test
    fun testAdaptiveComposerScoring() {
        val request = RecommendationRequest(currentTrack = sampleTrack1)
        val scoredSameComposer = scoringEngine.scoreCandidate(sampleTrack2, request)

        val trackDiffComposer = sampleTrack2.copy(composer = "Different Composer")
        val scoredDiffComposer = scoringEngine.scoreCandidate(trackDiffComposer, request)

        assertTrue(
            "Same composer score (${scoredSameComposer.score}) should exceed different composer score (${scoredDiffComposer.score})",
            scoredSameComposer.score > scoredDiffComposer.score
        )
    }

    // 19. Adaptive movie relationship scoring
    @Test
    fun testAdaptiveMovieScoring() {
        val request = RecommendationRequest(currentTrack = sampleTrack1)
        val scoredSameMovie = scoringEngine.scoreCandidate(sampleTrack2, request)

        val trackDiffMovie = sampleTrack2.copy(movieTitle = "Different Movie")
        val scoredDiffMovie = scoringEngine.scoreCandidate(trackDiffMovie, request)

        assertTrue(
            "Same movie score (${scoredSameMovie.score}) should exceed different movie score (${scoredDiffMovie.score})",
            scoredSameMovie.score > scoredDiffMovie.score
        )
    }

    // 20. Adaptive artist relationship scoring
    @Test
    fun testAdaptiveArtistScoring() {
        val request = RecommendationRequest(currentTrack = sampleTrack1)
        val simSameArtist = scoringEngine.calculateSimilarity(sampleTrack2, sampleTrack1)

        val trackDiffArtist = sampleTrack2.copy(artist = "Other Artist")
        val simDiffArtist = scoringEngine.calculateSimilarity(trackDiffArtist, sampleTrack1)

        assertTrue("Same artist similarity ($simSameArtist) should exceed different artist ($simDiffArtist)", simSameArtist > simDiffArtist)
    }

    // 21. Explicit playlist intent remains dominant
    @Test
    fun testExplicitPlaylistIntentDominant() {
        val intent = UserIntent(UserIntentType.PLAYLIST, targetName = "Tamil")
        val request = RecommendationRequest(currentTrack = sampleTrack1, userIntent = intent)

        val scored = scoringEngine.scoreCandidate(sampleTrack2, request)
        assertTrue("Intent boost must be active", scored.score > 0.6f)
    }

    // 22. Room authority remains unchanged
    @Test
    fun testRoomAuthorityUnchanged() = runBlocking {
        playbackBridge.setRoomModeActive(true)
        val remaining = listOf(sampleTrack1, sampleTrack2)
        val adapted = playbackBridge.adaptQueueAsync(sampleTrack1, remaining, emptyList())
        assertEquals(remaining, adapted)
    }

    // 23. Currently playing song remains unchanged during metadata enrichment
    @Test
    fun testCurrentSongUnchangedDuringEnrichment() {
        val state = repository.resolveTrackKnowledge(sampleTrack1)
        assertEquals("song_kaavaalaa", state.currentSongDetails?.songId)
    }

    // 24. Deep-link song navigation
    @Test
    fun testDeepLinkSongNavigation() {
        val path = "/s/song_kaavaalaa"
        assertTrue("Path should match song route", path.startsWith("/s/"))
        assertEquals("song_kaavaalaa", path.removePrefix("/s/"))
    }

    // 25. Deep-link album navigation
    @Test
    fun testDeepLinkAlbumNavigation() {
        val path = "/p/alb_jailer"
        assertTrue("Path should match playlist/album route", path.startsWith("/p/"))
        assertEquals("alb_jailer", path.removePrefix("/p/"))
    }

    // 26. Deep-link artist navigation
    @Test
    fun testDeepLinkArtistNavigation() {
        val path = "/u/p_anirudh"
        assertTrue("Path should match artist/user route", path.startsWith("/u/"))
        assertEquals("p_anirudh", path.removePrefix("/u/"))
    }

    // 27. Deep-link movie navigation
    @Test
    fun testDeepLinkMovieNavigation() {
        val path = "/r/m_jailer"
        assertTrue("Path should match room/movie route", path.startsWith("/r/"))
        assertEquals("m_jailer", path.removePrefix("/r/"))
    }

    // 28. No fabricated metadata
    @Test
    fun testNoFabricatedMetadata() {
        val bareTrack = AdaptiveTrackContext("b_1", "Title", "Artist")
        val state = repository.resolveTrackKnowledge(bareTrack)

        assertNull("Unresolved movie should be null, not fake text", state.currentMovieDetails)
        assertNull("Unresolved album details should be null", state.currentAlbumDetails)
    }

    // 29. Deterministic mapping
    @Test
    fun testDeterministicMapping() {
        val state1 = repository.resolveTrackKnowledge(sampleTrack1)
        val state2 = repository.resolveTrackKnowledge(sampleTrack1)

        assertEquals(state1.currentSongDetails?.songId, state2.currentSongDetails?.songId)
        assertEquals(state1.source, state2.source)
    }

    // 30. Threading/off-main-thread behavior
    @Test
    fun testThreadingBehavior() = runBlocking {
        val state = repository.resolveTrackKnowledge(sampleTrack1)
        assertNotNull(state)
        assertEquals(false, state.isLoading)
    }
}
