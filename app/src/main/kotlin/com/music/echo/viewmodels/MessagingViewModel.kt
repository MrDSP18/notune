package com.music.echo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import echo.music.iad1tya.models.Message
import echo.music.iad1tya.models.MessageType
import echo.music.iad1tya.models.SocialUser
import echo.music.iad1tya.repository.MessagingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagingViewModel @Inject constructor(
    private val messagingRepository: MessagingRepository
) : ViewModel() {

    val conversations: StateFlow<List<SocialUser>> = messagingRepository.getConversations()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _selectedUserId = MutableStateFlow<String?>(null)
    
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val messages: StateFlow<List<Message>> = _selectedUserId
        .flatMapLatest { userId ->
            if (userId != null) messagingRepository.getMessages(userId)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun selectConversation(userId: String) {
        _selectedUserId.value = userId
    }

    fun sendMessage(userId: String, content: String) {
        viewModelScope.launch {
            messagingRepository.sendMessage(userId, MessageType.TEXT, content)
        }
    }
}
