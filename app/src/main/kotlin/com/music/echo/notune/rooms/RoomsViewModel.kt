package echo.music.iad1tya.notune.rooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.RoomEntity
import echo.music.iad1tya.db.entities.RoomMessageEntity
import echo.music.iad1tya.notune.rooms.models.*
import echo.music.iad1tya.notune.rooms.security.RoomEncryptionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class RoomsUiState(
    val publicRooms: List<RoomModel> = emptyList(),
    val currentRoom: RoomModel? = null,
    val activeMessages: List<RoomMessage> = emptyList(),
    val activeMembers: List<RoomMember> = emptyList(),
    val queueVotes: List<QueueVoteItem> = emptyList(),
    val searchQuery: String = "",
    val isE2EEncrypted: Boolean = false,
    val securityBadgeText: String = "🔐 END-TO-END ENCRYPTED"
)

@HiltViewModel
class RoomsViewModel @Inject constructor(
    private val database: MusicDatabase,
    private val encryptionManager: RoomEncryptionManager
) : ViewModel() {

    private val roomDao = database.roomDao()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentRoom = MutableStateFlow<RoomModel?>(null)
    val currentRoom: StateFlow<RoomModel?> = _currentRoom.asStateFlow()

    private val _activeMessages = MutableStateFlow<List<RoomMessage>>(emptyList())
    val activeMessages: StateFlow<List<RoomMessage>> = _activeMessages.asStateFlow()

    private val _queueVotes = MutableStateFlow<List<QueueVoteItem>>(emptyList())
    val queueVotes: StateFlow<List<QueueVoteItem>> = _queueVotes.asStateFlow()

    val publicRooms: StateFlow<List<RoomModel>> = roomDao.getPublicRooms()
        .map { entities ->
            entities.map { entity ->
                RoomModel(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    type = RoomType.valueOf(entity.roomType),
                    isEncrypted = entity.isEncrypted,
                    memberLimit = entity.memberLimit,
                    hostId = entity.hostId,
                    hostName = entity.hostName,
                    genreTag = entity.genreTag,
                    createdAt = entity.createdAt
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<RoomsUiState> = combine(
        publicRooms,
        _currentRoom,
        _activeMessages,
        _queueVotes
    ) { rooms, room, messages, votes ->
        RoomsUiState(
            publicRooms = rooms,
            currentRoom = room,
            activeMessages = messages,
            queueVotes = votes,
            isE2EEncrypted = room?.isEncrypted ?: false,
            securityBadgeText = encryptionManager.getE2EBadgeText()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RoomsUiState())

    fun createRoom(
        name: String,
        description: String,
        type: RoomType,
        genreTag: String? = null,
        memberLimit: Int = 100
    ) {
        viewModelScope.launch {
            val isEncrypted = type == RoomType.PRIVATE || type == RoomType.COUPLE || type == RoomType.FRIENDS
            val roomId = UUID.randomUUID().toString()

            val entity = RoomEntity(
                id = roomId,
                name = name,
                description = description,
                roomType = type.name,
                isEncrypted = isEncrypted,
                memberLimit = if (type == RoomType.COUPLE) 2 else memberLimit,
                hostId = "local_user_id",
                hostName = "You",
                genreTag = genreTag,
                createdAt = System.currentTimeMillis()
            )

            roomDao.insertRoom(entity)

            val model = RoomModel(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                type = type,
                isEncrypted = isEncrypted,
                memberLimit = entity.memberLimit,
                hostId = entity.hostId,
                hostName = entity.hostName,
                genreTag = entity.genreTag,
                createdAt = entity.createdAt
            )

            _currentRoom.value = model
        }
    }

    fun joinRoom(room: RoomModel) {
        _currentRoom.value = room
    }

    fun sendMessage(content: String, attachedSongId: String? = null, attachedSongTitle: String? = null) {
        val room = _currentRoom.value ?: return
        viewModelScope.launch {
            val messageId = UUID.randomUUID().toString()
            val finalContent = if (room.isEncrypted) {
                encryptionManager.encryptTextMessage(content, room.id)
            } else {
                content
            }

            val entity = RoomMessageEntity(
                id = messageId,
                roomId = room.id,
                senderId = "local_user_id",
                senderName = "You",
                encryptedContent = finalContent,
                timestamp = System.currentTimeMillis(),
                isEncrypted = room.isEncrypted,
                attachedSongId = attachedSongId,
                attachedSongTitle = attachedSongTitle
            )

            roomDao.insertMessage(entity)

            val decryptedMessage = RoomMessage(
                id = messageId,
                roomId = room.id,
                senderId = "local_user_id",
                senderName = "You",
                content = content,
                timestamp = entity.timestamp,
                isEncrypted = room.isEncrypted,
                attachedSongId = attachedSongId,
                attachedSongTitle = attachedSongTitle
            )

            _activeMessages.update { it + decryptedMessage }
        }
    }

    fun voteQueueSong(songId: String) {
        _queueVotes.update { votes ->
            votes.map { item ->
                if (item.songId == songId) {
                    val newVoted = !item.userVoted
                    val newCount = if (newVoted) item.votes + 1 else (item.votes - 1).coerceAtLeast(0)
                    item.copy(votes = newCount, userVoted = newVoted)
                } else item
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
