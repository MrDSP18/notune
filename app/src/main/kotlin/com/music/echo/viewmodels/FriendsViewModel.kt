package com.music.echo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import echo.music.iad1tya.models.SocialUser
import echo.music.iad1tya.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    val friends: StateFlow<List<SocialUser>> = socialRepository.getFriends()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val friendRequests: StateFlow<List<SocialUser>> = socialRepository.getFriendRequests()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _mutationError = MutableStateFlow<String?>(null)
    val mutationError: StateFlow<String?> = _mutationError.asStateFlow()

    fun acceptRequest(userId: String) {
        viewModelScope.launch {
            socialRepository.acceptFriendRequest(userId).onFailure { error ->
                _mutationError.value = error.message ?: "Unable to accept the friend request."
            }
        }
    }

    fun rejectRequest(userId: String) {
        viewModelScope.launch {
            socialRepository.rejectFriendRequest(userId).onFailure { error ->
                _mutationError.value = error.message ?: "Unable to reject the friend request."
            }
        }
    }

    fun clearMutationError() {
        _mutationError.value = null
    }
}
