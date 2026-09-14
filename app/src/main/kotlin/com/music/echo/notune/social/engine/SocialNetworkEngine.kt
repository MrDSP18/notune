package com.music.echo.notune.social.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class PresenceState {
    ONLINE,
    LISTENING,
    LISTEN_TOGETHER,
    AWAY,
    OFFLINE,
    INVISIBLE
}

enum class PresencePrivacy {
    EVERYONE,
    FRIENDS_ONLY,
    NOBODY,
    INVISIBLE
}

data class LocalUserPresence(
    val state: PresenceState = PresenceState.ONLINE,
    val privacy: PresencePrivacy = PresencePrivacy.EVERYONE,
    val isSharingCurrentTrack: Boolean = true,
    val currentTrackId: String? = null,
    val currentTrackTitle: String? = null,
    val currentTrackArtist: String? = null,
    val allowDropInListening: Boolean = true
)

@Singleton
class SocialNetworkEngine @Inject constructor() {

    private val _userPresence = MutableStateFlow(LocalUserPresence())
    val userPresence: StateFlow<LocalUserPresence> = _userPresence.asStateFlow()

    fun setPresenceState(state: PresenceState) {
        _userPresence.value = _userPresence.value.copy(state = state)
    }

    fun setPresencePrivacy(privacy: PresencePrivacy) {
        _userPresence.value = _userPresence.value.copy(privacy = privacy)
    }

    fun toggleSharingCurrentTrack(enabled: Boolean) {
        _userPresence.value = _userPresence.value.copy(isSharingCurrentTrack = enabled)
    }

    fun toggleDropInListening(enabled: Boolean) {
        _userPresence.value = _userPresence.value.copy(allowDropInListening = enabled)
    }

    fun updateNowPlaying(trackId: String?, title: String?, artist: String?) {
        val newState = if (trackId != null) PresenceState.LISTENING else PresenceState.ONLINE
        _userPresence.value = _userPresence.value.copy(
            state = newState,
            currentTrackId = trackId,
            currentTrackTitle = title,
            currentTrackArtist = artist
        )
    }
}
