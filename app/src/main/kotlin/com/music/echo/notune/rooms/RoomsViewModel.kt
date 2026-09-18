package echo.music.iad1tya.notune.rooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import echo.music.iad1tya.models.*
import echo.music.iad1tya.repository.RoomRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoomsUiState(
    val publicRooms: List<Room> = emptyList(),
    val currentRoom: Room? = null,
    val connectionState: RoomConnectionState = RoomConnectionState.IDLE,
    val activeMessages: List<Message> = emptyList(), // Reusing Message model
    val syncState: RoomSyncState = RoomSyncState()
)

@HiltViewModel
class RoomsViewModel @Inject constructor(
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState: StateFlow<RoomsUiState> = combine(
        roomRepository.currentRoom,
        roomRepository.connectionState,
        roomRepository.syncState
    ) { room, connState, sync ->
        RoomsUiState(
            currentRoom = room,
            connectionState = connState,
            syncState = sync
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RoomsUiState())

    fun createRoom(name: String, type: RoomType) {
        viewModelScope.launch {
            roomRepository.createRoom(name, type)
        }
    }

    fun joinRoom(roomCode: String) {
        viewModelScope.launch {
            roomRepository.joinRoom(roomCode)
        }
    }

    fun leaveRoom() {
        viewModelScope.launch {
            roomRepository.leaveRoom()
        }
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            roomRepository.sendChatMessage(content)
        }
    }

    fun sendReaction(reaction: String) {
        viewModelScope.launch {
            roomRepository.sendReaction(reaction)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
