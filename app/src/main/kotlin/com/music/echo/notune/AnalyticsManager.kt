package com.music.echo.notune

import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.Event
import echo.music.iad1tya.di.ApplicationScope
import echo.music.iad1tya.models.AppEvent
import echo.music.iad1tya.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class AnalyticsManager @Inject constructor(
    private val eventRepository: EventRepository,
    private val database: MusicDatabase,
    @echo.music.iad1tya.di.ApplicationScope private val scope: CoroutineScope
) {
    private var job: kotlinx.coroutines.Job? = null

    init {
        job = scope.launch {
            eventRepository.events.collect { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: AppEvent) {
        when (event) {
            is AppEvent.PlaybackCompleted -> {
                Timber.tag("Analytics").d("Playback Completed: ${event.song.title} (${event.playTimeMs}ms)")
                database.query {
                    incrementTotalPlayTime(event.song.id, event.playTimeMs)
                    insert(
                        Event(
                            songId = event.song.id,
                            timestamp = LocalDateTime.now(),
                            playTime = event.playTimeMs
                        )
                    )
                }
            }
            is AppEvent.SongLiked -> {
                Timber.tag("Analytics").d("Song Liked: ${event.song.title}")
            }
            else -> {}
        }
    }

    fun release() {
        job?.cancel()
    }
}
