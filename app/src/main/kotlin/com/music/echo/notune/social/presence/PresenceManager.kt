package com.music.echo.notune.social.presence

import com.music.echo.notune.identity.NotuneAccountRepository
import echo.music.iad1tya.models.PlaybackState
import echo.music.iad1tya.models.PresenceState
import echo.music.iad1tya.repository.PlaybackRepository
import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class PresenceManager @Inject constructor(
    private val playbackRepository: PlaybackRepository,
    private val accountRepository: NotuneAccountRepository,
    private val httpClient: HttpClient,
    @echo.music.iad1tya.di.ApplicationScope private val scope: CoroutineScope
) {
    init {
        scope.launch {
            playbackRepository.playbackState.collectLatest { state ->
                updatePresence(state)
            }
        }
    }

    private suspend fun updatePresence(state: PlaybackState) {
        val presence = if (state.isPlaying) {
            PresenceState.LISTENING
        } else {
            PresenceState.ONLINE
        }
        
        val userId = accountRepository.account.value.userId
        Timber.tag("Presence").d("Updating presence for $userId: $presence (Song: ${state.currentSong?.title})")
        
        // Here we would emit to WebSocket
    }
}
