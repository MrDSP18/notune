package echo.music.iad1tya.notune.intelligence.enrichment

import echo.music.iad1tya.notune.intelligence.knowledge.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cache entry wrapping resolved entity and timestamp.
 */
data class CachedItem<T>(
    val value: T,
    val cachedAt: Long = System.currentTimeMillis(),
    val ttlMs: Long = 86_400_000L, // 24 hours default TTL
    val gracePeriodMs: Long = 30 * 86_400_000L // 30 days offline grace period
) {
    fun isExpired(): Boolean = System.currentTimeMillis() - cachedAt > ttlMs
    fun isStale(): Boolean = isExpired()
    fun isWithinGracePeriod(): Boolean = System.currentTimeMillis() - cachedAt <= (ttlMs + gracePeriodMs)
}

/**
 * Thread-safe memory and offline cache manager for enriched music knowledge graph objects.
 */
@Singleton
class MetadataCacheManager @Inject constructor() {

    private val songCache = ConcurrentHashMap<String, CachedItem<SongDetails>>()
    private val artistCache = ConcurrentHashMap<String, CachedItem<ArtistDetails>>()
    private val albumCache = ConcurrentHashMap<String, CachedItem<AlbumDetails>>()
    private val movieCache = ConcurrentHashMap<String, CachedItem<MovieDetails>>()
    private val personCache = ConcurrentHashMap<String, CachedItem<PersonDetails>>()

    fun putSong(song: SongDetails, ttlMs: Long = 86_400_000L) {
        songCache[song.songId] = CachedItem(song, ttlMs = ttlMs)
    }

    fun getSong(songId: String, allowStale: Boolean = true): SongDetails? {
        val entry = songCache[songId] ?: return null
        if (!allowStale && entry.isExpired()) return null
        if (allowStale && !entry.isWithinGracePeriod()) return null
        return entry.value
    }

    fun putArtist(artist: ArtistDetails, ttlMs: Long = 86_400_000L) {
        artistCache[artist.artistId] = CachedItem(artist, ttlMs = ttlMs)
    }

    fun getArtist(artistId: String, allowStale: Boolean = true): ArtistDetails? {
        val entry = artistCache[artistId] ?: return null
        if (!allowStale && entry.isExpired()) return null
        if (allowStale && !entry.isWithinGracePeriod()) return null
        return entry.value
    }

    fun putAlbum(album: AlbumDetails, ttlMs: Long = 86_400_000L) {
        albumCache[album.albumId] = CachedItem(album, ttlMs = ttlMs)
    }

    fun getAlbum(albumId: String, allowStale: Boolean = true): AlbumDetails? {
        val entry = albumCache[albumId] ?: return null
        if (!allowStale && entry.isExpired()) return null
        if (allowStale && !entry.isWithinGracePeriod()) return null
        return entry.value
    }

    fun putMovie(movie: MovieDetails, ttlMs: Long = 86_400_000L) {
        movieCache[movie.movieId] = CachedItem(movie, ttlMs = ttlMs)
    }

    fun getMovie(movieId: String, allowStale: Boolean = true): MovieDetails? {
        val entry = movieCache[movieId] ?: return null
        if (!allowStale && entry.isExpired()) return null
        if (allowStale && !entry.isWithinGracePeriod()) return null
        return entry.value
    }

    fun putPerson(person: PersonDetails, ttlMs: Long = 86_400_000L) {
        personCache[person.personId] = CachedItem(person, ttlMs = ttlMs)
    }

    fun getPerson(personId: String, allowStale: Boolean = true): PersonDetails? {
        val entry = personCache[personId] ?: return null
        if (!allowStale && entry.isExpired()) return null
        if (allowStale && !entry.isWithinGracePeriod()) return null
        return entry.value
    }

    fun getSongCacheSize(): Int = songCache.size
    fun getArtistCacheSize(): Int = artistCache.size
    fun getAlbumCacheSize(): Int = albumCache.size
    fun getMovieCacheSize(): Int = movieCache.size
    fun getPersonCacheSize(): Int = personCache.size
    fun getTotalCacheEntries(): Int = songCache.size + artistCache.size + albumCache.size + movieCache.size + personCache.size

    fun clearSongs() = songCache.clear()
    fun clearArtists() = artistCache.clear()
    fun clearAlbums() = albumCache.clear()
    fun clearMovies() = movieCache.clear()
    fun clearPeople() = personCache.clear()

    fun clearAll() {
        songCache.clear()
        artistCache.clear()
        albumCache.clear()
        movieCache.clear()
        personCache.clear()
    }
}
