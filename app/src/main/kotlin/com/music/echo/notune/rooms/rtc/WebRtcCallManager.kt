package echo.music.iad1tya.notune.rooms.rtc

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
enum class RtcMessageType {
    SDP_OFFER,
    SDP_ANSWER,
    ICE_CANDIDATE,
    CALL_LEAVE
}

@Serializable
data class RtcSignalMessage(
    val type: RtcMessageType,
    val callId: String,
    val senderUserId: String,
    val targetUserId: String,
    val sdpContent: String? = null,
    val sdpMid: String? = null,
    val sdpMLineIndex: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class IceServerConfig(
    val uri: String,
    val username: String? = null,
    val credential: String? = null,
    val ttlSeconds: Long = 86400
)

enum class WebRtcConnectionState {
    NEW,
    CONNECTING,
    CONNECTED,
    DISCONNECTED,
    FAILED,
    CLOSED
}

data class WebRtcSessionState(
    val callId: String,
    val peerUserId: String,
    val connectionState: WebRtcConnectionState = WebRtcConnectionState.NEW,
    val isLocalAudioEnabled: Boolean = true,
    val isLocalVideoEnabled: Boolean = true,
    val localSdpOffer: String? = null,
    val remoteSdpAnswer: String? = null,
    val iceCandidatesCount: Int = 0,
    val iceServers: List<IceServerConfig> = emptyList()
)

@Singleton
class WebRtcCallManager @Inject constructor() {

    private val _sessionState = MutableStateFlow<WebRtcSessionState?>(null)
    val sessionState: StateFlow<WebRtcSessionState?> = _sessionState.asStateFlow()

    /**
     * Default STUN/TURN configuration. In production, short-lived TURN credentials are
     * dynamically requested from the backend API (P0-7).
     */
    fun getDefaultIceServers(): List<IceServerConfig> {
        return listOf(
            IceServerConfig(uri = "stun:stun.l.google.com:19302"),
            IceServerConfig(uri = "stun:stun1.l.google.com:19302"),
            IceServerConfig(
                uri = "turn:turn.notune.app:3478?transport=udp",
                username = "notune_ephemeral_user",
                credential = "ephemeral_auth_token_xyz"
            )
        )
    }

    fun initiateCall(callId: String, peerUserId: String): RtcSignalMessage {
        val simulatedSdpOffer = "v=0\r\no=- ${System.currentTimeMillis()} 2 IN IP4 127.0.0.1\r\ns=NØTUNE_RTC\r\nt=0 0\r\nm=audio 9 UDP/TLS/RTP/SAVPF 111\r\nm=video 9 UDP/TLS/RTP/SAVPF 96\r\n"

        _sessionState.value = WebRtcSessionState(
            callId = callId,
            peerUserId = peerUserId,
            connectionState = WebRtcConnectionState.CONNECTING,
            localSdpOffer = simulatedSdpOffer,
            iceServers = getDefaultIceServers()
        )

        return RtcSignalMessage(
            type = RtcMessageType.SDP_OFFER,
            callId = callId,
            senderUserId = "local_user",
            targetUserId = peerUserId,
            sdpContent = simulatedSdpOffer
        )
    }

    fun handleIncomingOffer(offer: RtcSignalMessage): RtcSignalMessage {
        val simulatedSdpAnswer = "v=0\r\no=- ${System.currentTimeMillis()} 2 IN IP4 127.0.0.1\r\ns=NØTUNE_RTC_ANS\r\nt=0 0\r\nm=audio 9 UDP/TLS/RTP/SAVPF 111\r\nm=video 9 UDP/TLS/RTP/SAVPF 96\r\n"

        _sessionState.value = WebRtcSessionState(
            callId = offer.callId,
            peerUserId = offer.senderUserId,
            connectionState = WebRtcConnectionState.CONNECTED,
            remoteSdpAnswer = simulatedSdpAnswer,
            iceServers = getDefaultIceServers()
        )

        return RtcSignalMessage(
            type = RtcMessageType.SDP_ANSWER,
            callId = offer.callId,
            senderUserId = "local_user",
            targetUserId = offer.senderUserId,
            sdpContent = simulatedSdpAnswer
        )
    }

    fun handleIncomingAnswer(answer: RtcSignalMessage) {
        _sessionState.value = _sessionState.value?.copy(
            connectionState = WebRtcConnectionState.CONNECTED,
            remoteSdpAnswer = answer.sdpContent
        )
    }

    fun addIceCandidate(candidate: RtcSignalMessage) {
        _sessionState.value = _sessionState.value?.let { state ->
            state.copy(iceCandidatesCount = state.iceCandidatesCount + 1)
        }
    }

    fun toggleAudio(): Boolean {
        val currentState = _sessionState.value ?: return false
        val newAudioState = !currentState.isLocalAudioEnabled
        _sessionState.value = currentState.copy(isLocalAudioEnabled = newAudioState)
        return newAudioState
    }

    fun toggleVideo(): Boolean {
        val currentState = _sessionState.value ?: return false
        val newVideoState = !currentState.isLocalVideoEnabled
        _sessionState.value = currentState.copy(isLocalVideoEnabled = newVideoState)
        return newVideoState
    }

    fun terminateCall() {
        _sessionState.value = _sessionState.value?.copy(connectionState = WebRtcConnectionState.CLOSED)
        _sessionState.value = null
    }
}

