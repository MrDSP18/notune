package com.music.echo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import echo.music.iad1tya.models.SocialUser
import echo.music.iad1tya.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    val friends: StateFlow<List<SocialUser>> = socialRepository.getFriends()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val friendRequests: StateFlow<List<SocialUser>> = socialRepository.getFriendRequests()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun acceptRequest(userId: String) {
        viewModelScope.launch {
            socialRepository.acceptFriendRequest(userId)
        }
    }

    fun rejectRequest(userId: String) {
        viewModelScope.launch {
            socialRepository.rejectFriendRequest(userId)
        }
    }
}
