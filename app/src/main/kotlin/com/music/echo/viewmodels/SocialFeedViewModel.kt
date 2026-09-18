package com.music.echo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import echo.music.iad1tya.models.SocialPost
import echo.music.iad1tya.repository.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialFeedViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    val feed: StateFlow<List<SocialPost>> = socialRepository.getFeed()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun likePost(postId: String) {
        viewModelScope.launch {
            socialRepository.likePost(postId)
        }
    }
}
