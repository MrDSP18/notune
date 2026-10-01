package com.music.echo.notune

import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HosannaPlaybackTest {

    @Test
    fun testSearchAndResolveHosannaSong() = runBlocking {
        println("=== Testing YouTube Search for 'Hosanna' ===")
        val searchResult = YouTube.searchSummary("Hosanna")
        if (searchResult.isFailure) {
            println("Search failed with exception: ${searchResult.exceptionOrNull()}")
            searchResult.exceptionOrNull()?.printStackTrace()
        }
        assertTrue("Search for 'Hosanna' should succeed", searchResult.isSuccess)

        val result = searchResult.getOrThrow()
        val songItems = result.summaries.flatMap { it.items }.filterIsInstance<SongItem>()
        assertTrue("Should return at least one song result for 'Hosanna'", songItems.isNotEmpty())

        val hosannaSong = songItems.first()
        println("Found Song: ${hosannaSong.title} by ${hosannaSong.artists.joinToString { it.name }} (ID: ${hosannaSong.id})")
        assertNotNull("Song ID should not be null", hosannaSong.id)
        assertTrue("Song title should contain 'Hosanna'", hosannaSong.title.lowercase().contains("hosanna"))

        println("=== Resolving YouTube Queue Metadata for ${hosannaSong.id} ===")
        val queueResult = YouTube.queue(listOf(hosannaSong.id))
        assertTrue("Queue fetch for 'Hosanna' should succeed", queueResult.isSuccess)
        val queueSongs = queueResult.getOrThrow()
        assertTrue("Queue should contain at least one track", queueSongs.isNotEmpty())
        val queueSong = queueSongs.first()
        println("Successfully resolved queue item: title=${queueSong.title}, artist=${queueSong.artists.joinToString { it.name }}")
        assertNotNull(queueSong.title)
    }

    @Test
    fun testSearchMultipleDiverseSongs() = runBlocking {
        val testQueries = listOf("Hosanna", "Enjoy Enjaami", "Kesariya", "Shape of You")

        for (query in testQueries) {
            println("--- Testing query: '$query' ---")
            val searchResult = YouTube.searchSummary(query)
            if (searchResult.isFailure) {
                println("Search failed for '$query': ${searchResult.exceptionOrNull()}")
            }
            assertTrue("Search for '$query' should succeed", searchResult.isSuccess)

            val songs = searchResult.getOrThrow().summaries.flatMap { it.items }.filterIsInstance<SongItem>()
            assertTrue("Should find song items for '$query'", songs.isNotEmpty())

            val topSong = songs.first()
            println("Top result for '$query': ${topSong.title} (ID: ${topSong.id})")
            assertNotNull(topSong.id)
            assertNotNull(topSong.title)
        }
    }
}
