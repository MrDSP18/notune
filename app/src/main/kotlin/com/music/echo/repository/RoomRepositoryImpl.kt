package com.music.echo.repository

import echo.music.iad1tya.listentogether.ListenTogetherClient
import echo.music.iad1tya.listentogether.*
import echo.music.iad1tya.models.*
import echo.music.iad1tya.repository.RoomRepository
import echo.music.iad1tya.repository.PlaybackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import echo.music.iad1tya.listentogether.ConnectionState as ClientConnectionState
import echo.music.iad1tya.listentogether.RoomRole as ClientRoomRole

@Singleton
class RoomRepositoryImpl @Inject constructor(
    private val client: ListenTogetherClient,
    private val playbackRepository: PlaybackRepository,
    @echo.music.iad1tya.di.ApplicationScope private val scope: CoroutineScope
) : RoomRepository {

    override val connectionState: StateFlow<RoomConnectionState> = client.connectionState.map {
        when (it) {
            ClientConnectionState.DISCONNECTED -> RoomConnectionState.DISCONNECTED
            ClientConnectionState.CONNECTING -> RoomConnectionState.CONNECTING
            ClientConnectionState.CONNECTED -> RoomConnectionState.CONNECTED
            ClientConnectionState.RECONNECTING -> RoomConnectionState.RECONNECTING
            ClientConnectionState.ERROR -> RoomConnectionState.ERROR
        }
    }.stateIn(scope, SharingStarted.Eagerly, RoomConnectionState.IDLE)

    override val currentRoom: StateFlow<Room?> = client.roomState.map { state ->
        state?.let {
            Room(
                roomId = it.roomCode,
                name = "Room ${it.roomCode}",
                type = RoomType.PUBLIC, 
                hostId = it.hostId,
                members = it.users.map { user ->
                    RoomMember(
                        userId = user.userId,
                        username = user.username,
                        role = if (user.isHost) RoomRole.HOST else RoomRole.MEMBER,
                        isConnected = user.isConnected
                    )
                },
                playbackState = RoomPlaybackState(
                    currentTrackId = it.currentTrack?.id,
                    trackMetadata = it.currentTrack?.let { track ->
                        MediaMetadata(
                            id = track.id,
                            title = track.title,
                            artists = listOf(MediaMetadata.Artist(id = null, name = track.artist)),
                            duration = track.duration.toInt(),
                            thumbnailUrl = track.thumbnail
                        )
                    },
                    isPlaying = it.isPlaying,
                    positionMs = it.position,
                    lastUpdateServerTime = it.lastUpdate
                ),
                queue = it.queue.map { item ->
                    RoomQueueItem(
                        id = item.id,
                        metadata = MediaMetadata(
                            id = item.id,
                            title = item.title,
                            artists = listOf(MediaMetadata.Artist(id = null, name = item.artist)),
                            duration = item.duration.toInt(),
                            thumbnailUrl = item.thumbnail
                        ),
                        addedBy = item.suggestedBy ?: ""
                    )
                }
            )
        }
    }.stateIn(scope, SharingStarted.Eagerly, null)

    override val syncState = MutableStateFlow(RoomSyncState())

    override suspend fun createRoom(name: String, type: RoomType): Result<Room> {
        client.createRoom(name)
        return currentRoom.filterNotNull().first().let { Result.success(it) }
    }

    override suspend fun joinRoom(roomCode: String): Result<Room> {
        client.joinRoom(roomCode, "User")
        return currentRoom.filterNotNull().first().let { Result.success(it) }
    }

    override suspend fun leaveRoom(): Result<Unit> = runCatching {
        client.leaveRoom()
    }

    override suspend fun play() {
        client.sendPlaybackAction(PlaybackActions.PLAY, position = playbackRepository.playbackState.value.position)
    }

    override suspend fun pause() {
        client.sendPlaybackAction(PlaybackActions.PAUSE, position = playbackRepository.playbackState.value.position)
    }

    override suspend fun seekTo(positionMs: Long) {
        client.sendPlaybackAction(PlaybackActions.SEEK, position = positionMs)
    }

    override suspend fun next() {
        client.sendPlaybackAction(PlaybackActions.SKIP_NEXT)
    }

    override suspend fun previous() {
        client.sendPlaybackAction(PlaybackActions.SKIP_PREV)
    }

    override suspend fun addToQueue(metadata: MediaMetadata) {
        client.sendPlaybackAction(
            PlaybackActions.QUEUE_ADD,
            trackInfo = TrackInfo(
                id = metadata.id,
                title = metadata.title,
                artist = metadata.artists.joinToString { it.name },
                duration = metadata.duration.toLong(),
                thumbnail = metadata.thumbnailUrl
            )
        )
    }

    override suspend fun removeFromQueue(itemId: String) {
        client.sendPlaybackAction(PlaybackActions.QUEUE_REMOVE, trackId = itemId)
    }

    override suspend fun moveQueueItem(itemId: String, newIndex: Int) {
        // Not supported in protocol yet
    }

    override suspend fun sendChatMessage(message: String): Result<Unit> = runCatching {
        client.sendChatMessage(message)
    }

    override suspend fun sendReaction(reaction: String): Result<Unit> = runCatching {
        client.sendChatMessage("REACTION:$reaction")
    }
}
