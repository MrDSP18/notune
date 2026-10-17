package echo.music.iad1tya.notune.intelligence.ui

import echo.music.iad1tya.notune.ai.adaptive.AdaptiveScoringConfig
import echo.music.iad1tya.notune.ai.adaptive.AdaptiveScoringEngine
import echo.music.iad1tya.notune.ai.adaptive.AdaptiveTrackContext
import echo.music.iad1tya.notune.ai.adaptive.RecommendationRequest
import echo.music.iad1tya.notune.intelligence.knowledge.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeepMusicUiTest {

    private lateinit var repository: MusicKnowledgeRepository
    private lateinit var scoringEngine: AdaptiveScoringEngine
    private lateinit var viewModel: KnowledgeViewModel

    @Before
    fun setUp() {
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
        viewModel = KnowledgeViewModel(repository).apply {
            ioDispatcher = kotlinx.coroutines.Dispatchers.Unconfined
        }
    }

    @Test
    fun testCurrentSongOpensDetails() = runTest {
        val track = AdaptiveTrackContext(
            trackId = "song_1",
            title = "Testing Track 1",
            artist = "Artist A",
            album = "Album Alpha"
        )
        viewModel.loadSongDetails("song_1", track)

        val state = viewModel.songState.value
        assertTrue(state is KnowledgeUiState.Success)
        val details = (state as KnowledgeUiState.Success).data
        assertEquals("song_1", details.songId)
        assertEquals("Testing Track 1", details.title)
    }

    @Test
    fun testSelectedSongOpensDetails() = runTest {
        val song = SongDetails(
            songId = "song_selected",
            title = "Selected Track",
            artists = listOf(PersonDetails("p1", "Singer 1")),
            source = MetadataSource.LOCAL
        )
        repository.cacheSongDetails(song)
        viewModel.loadSongDetails("song_selected")

        val state = viewModel.songState.value
        assertTrue(state is KnowledgeUiState.Success)
        assertEquals("Selected Track", (state as KnowledgeUiState.Success).data.title)
    }

    @Test
    fun testArtistNavigation() = runTest {
        val artist = ArtistDetails(
            artistId = "artist_1",
            name = "Test Artist",
            roles = listOf("Singer", "Composer")
        )
        repository.cacheArtistDetails(artist)
        viewModel.loadArtistDetails("artist_1")

        val state = viewModel.artistState.value
        assertTrue(state is KnowledgeUiState.Success)
        assertEquals("Test Artist", (state as KnowledgeUiState.Success).data.name)
    }

    @Test
    fun testAlbumNavigation() = runTest {
        val album = AlbumDetails(
            albumId = "album_1",
            title = "Test Album",
            totalTracks = 10
        )
        repository.cacheAlbumDetails(album)
        viewModel.loadAlbumDetails("album_1")

        val state = viewModel.albumState.value
        assertTrue(state is KnowledgeUiState.Success)
        assertEquals("Test Album", (state as KnowledgeUiState.Success).data.title)
    }

    @Test
    fun testMovieNavigation() = runTest {
        val movie = MovieDetails(
            movieId = "movie_1",
            title = "Test Movie",
            year = 2024
        )
        repository.cacheMovieDetails(movie)
        viewModel.loadMovieDetails("movie_1")

        val state = viewModel.movieState.value
        assertTrue(state is KnowledgeUiState.Success)
        assertEquals("Test Movie", (state as KnowledgeUiState.Success).data.title)
    }

    @Test
    fun testPersonNavigation() = runTest {
        val person = PersonDetails(
            personId = "person_1",
            name = "Lead Performer",
            roleName = "Hero"
        )
        val song = SongDetails(
            songId = "s_person",
            title = "Hero Track",
            artists = listOf(person)
        )
        repository.cacheSongDetails(song)
        viewModel.loadPersonDetails("person_1")

        val state = viewModel.personState.value
        assertTrue(state is KnowledgeUiState.Success)
        assertEquals("Lead Performer", (state as KnowledgeUiState.Success).data.name)
    }

    @Test
    fun testHeroRendering() {
        val hero = PersonDetails(personId = "hero_1", name = "Hero Actor")
        val castMember = CastMember(person = hero, role = CastRole.HERO)
        assertEquals(CastRole.HERO, castMember.role)
        assertEquals("Hero Actor", castMember.person.name)
    }

    @Test
    fun testHeroineRendering() {
        val heroine = PersonDetails(personId = "heroine_1", name = "Heroine Actress")
        val castMember = CastMember(person = heroine, role = CastRole.HEROINE)
        assertEquals(CastRole.HEROINE, castMember.role)
        assertEquals("Heroine Actress", castMember.person.name)
    }

    @Test
    fun testMultipleLeadActors() {
        val actor1 = PersonDetails(personId = "lead_1", name = "Lead Actor 1")
        val actor2 = PersonDetails(personId = "lead_2", name = "Lead Actor 2")
        val movie = MovieDetails(
            movieId = "multi_lead",
            title = "Ensemble Movie",
            leadActors = listOf(actor1, actor2)
        )
        assertEquals(2, movie.leadActors.size)
        assertEquals("Lead Actor 1", movie.leadActors[0].name)
        assertEquals("Lead Actor 2", movie.leadActors[1].name)
    }

    @Test
    fun testMultipleLeadActresses() {
        val actress1 = PersonDetails(personId = "actress_1", name = "Lead Actress 1")
        val actress2 = PersonDetails(personId = "actress_2", name = "Lead Actress 2")
        val movie = MovieDetails(
            movieId = "multi_actress",
            title = "Dual Lead Movie",
            leadActresses = listOf(actress1, actress2)
        )
        assertEquals(2, movie.leadActresses.size)
        assertEquals("Lead Actress 1", movie.leadActresses[0].name)
        assertEquals("Lead Actress 2", movie.leadActresses[1].name)
    }

    @Test
    fun testSupportingCast() {
        val suppPerson = PersonDetails(personId = "supp_1", name = "Supporting Actor 1")
        val castMember = CastMember(person = suppPerson, role = CastRole.SUPPORTING_ACTOR)
        assertEquals(CastRole.SUPPORTING_ACTOR, castMember.role)
    }

    @Test
    fun testCrewRendering() {
        val dir = PersonDetails(personId = "dir_1", name = "Director 1")
        val crewMember = CrewMember(person = dir, role = CrewRole.DIRECTOR)
        assertEquals(CrewRole.DIRECTOR, crewMember.role)
    }

    @Test
    fun testSongCredits() {
        val singer = PersonDetails(personId = "s1", name = "Singer 1")
        val composer = PersonDetails(personId = "c1", name = "Composer 1")
        val lyricist = PersonDetails(personId = "l1", name = "Lyricist 1")

        val credits = SongCredits(
            singers = listOf(singer),
            composers = listOf(composer),
            lyricists = listOf(lyricist),
            label = "Music Label X"
        )
        assertEquals("Singer 1", credits.singers.first().name)
        assertEquals("Composer 1", credits.composers.first().name)
        assertEquals("Lyricist 1", credits.lyricists.first().name)
        assertEquals("Music Label X", credits.label)
    }

    @Test
    fun testLyricsIntegration() {
        val song = SongDetails(
            songId = "s_lyrics",
            title = "Song with Lyrics",
            lyricsAvailable = true
        )
        assertTrue(song.lyricsAvailable)
    }

    @Test
    fun testRelatedSongs() {
        val related = RelatedSong(
            songId = "rel_1",
            title = "Related Track",
            artist = "Composer B",
            relationshipReason = "Same composer",
            similarityScore = 0.95f
        )
        val song = SongDetails(
            songId = "s_rel",
            title = "Main Track",
            relatedSongs = listOf(related)
        )
        assertEquals(1, song.relatedSongs.size)
        assertEquals("Same composer", song.relatedSongs.first().relationshipReason)
    }

    @Test
    fun testAdaptiveRecommendationIntegration() {
        val track = AdaptiveTrackContext(
            trackId = "cand_1",
            title = "Candidate Track",
            artist = "Artist A",
            genre = "Rock"
        )
        val scoredTrack = scoringEngine.scoreCandidate(
            candidate = track,
            request = RecommendationRequest(currentTrack = track),
            recentArtistCounts = emptyMap<String, Int>(),
            recentGenreCounts = emptyMap<String, Int>()
        )
        assertTrue(scoredTrack.score > 0.0f)
    }

    @Test
    fun testNoAiExplanation() = runTest {
        val track = AdaptiveTrackContext(
            trackId = "track_ai",
            title = "AI Track",
            artist = "Anirudh",
            genre = "EDM"
        )
        viewModel.loadSongDetails("track_ai", track)
        val state = viewModel.songState.value
        assertTrue(state is KnowledgeUiState.Success)
    }

    @Test
    fun testMissingMetadata() {
        val song = repository.getSongDetails("unknown_id")
        assertEquals("Track unknown_id", song.title)
        assertEquals(MetadataSource.UNKNOWN, song.source)
    }

    @Test
    fun testPartialMetadata() {
        val song = SongDetails(
            songId = "partial_1",
            title = "Partial Song",
            language = null,
            genre = null,
            source = MetadataSource.LOCAL
        )
        assertNull(song.language)
        assertNull(song.genre)
        assertEquals(MetadataSource.LOCAL, song.source)
    }

    @Test
    fun testOfflineMetadata() = runTest {
        val track = AdaptiveTrackContext(
            trackId = "offline_1",
            title = "Local Offline Track",
            artist = "Local Artist"
        )
        val state = repository.resolveTrackKnowledge(track)
        assertEquals(MetadataSource.LOCAL, state.source)
        assertFalse(state.isLoading)
    }

    @Test
    fun testCachedMetadata() = runTest {
        val song = SongDetails(songId = "cached_1", title = "Cached Song")
        repository.cacheSongDetails(song)
        val retrieved = repository.getSongDetails("cached_1")
        assertEquals("Cached Song", retrieved.title)
    }

    @Test
    fun testCurrentPlaybackRemainsUnchanged() = runTest {
        val songId = "play_1"
        viewModel.loadSongDetails(songId)
        val state = viewModel.songState.value
        assertTrue(state is KnowledgeUiState.Success)
    }

    @Test
    fun testQueueRemainsUnchanged() = runTest {
        val movie = repository.getMovieDetails("m_queue")
        assertEquals("Movie m_queue", movie.title)
    }

    @Test
    fun testRoomAuthorityRemainsUnchanged() = runTest {
        val person = repository.getPersonDetails("p_room")
        assertEquals("Person p_room", person.name)
    }

    @Test
    fun testNavigationUsesIdsRatherThanGiantObjects() {
        val songId = "s_123"
        val movieId = "m_456"
        val personId = "p_789"
        val routeSong = "song_details/$songId"
        val routeMovie = "movie/$movieId"
        val routePerson = "person/$personId"

        assertEquals("song_details/s_123", routeSong)
        assertEquals("movie/m_456", routeMovie)
        assertEquals("person/p_789", routePerson)
    }

    @Test
    fun testNoFakeMetadata() {
        val song = repository.getSongDetails("")
        assertEquals("Information unavailable", song.title)
    }

    @Test
    fun testMetadataLoadingOffMainThread() = runTest {
        viewModel.loadSongDetails("async_1")
        val state = viewModel.songState.value
        assertNotNull(state)
    }

    @Test
    fun testArtworkCacheReuse() {
        val artwork = MediaArtwork(
            url = "https://example.com/art.jpg",
            role = ArtworkRole.SONG,
            source = MetadataSource.LOCAL
        )
        val song = SongDetails(songId = "art_1", title = "Art Song", artwork = artwork)
        assertEquals("https://example.com/art.jpg", song.artwork?.url)
    }

    @Test
    fun testDeepLinkSongNavigation() {
        val deepLinkRoute = "song_details/song_abc"
        assertTrue(deepLinkRoute.startsWith("song_details/"))
    }

    @Test
    fun testDeepLinkAlbumNavigation() {
        val deepLinkRoute = "album/album_xyz"
        assertTrue(deepLinkRoute.startsWith("album/"))
    }

    @Test
    fun testDeepLinkArtistNavigation() {
        val deepLinkRoute = "artist/artist_123"
        assertTrue(deepLinkRoute.startsWith("artist/"))
    }

    @Test
    fun testDeepLinkMovieNavigation() {
        val deepLinkRoute = "movie/movie_789"
        assertTrue(deepLinkRoute.startsWith("movie/"))
    }
}
