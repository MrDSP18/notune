package echo.music.iad1tya.notune.rooms.couple

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class CallType {
    AUDIO,
    VIDEO
}

enum class CallState {
    IDLE,
    OUTGOING_RINGING,
    INCOMING_RINGING,
    CONNECTED,
    ENDED
}

data class CoupleCallSession(
    val callId: String,
    val partnerName: String,
    val callType: CallType,
    val callState: CallState = CallState.IDLE,
    val isMicMuted: Boolean = false,
    val isCameraOn: Boolean = true,
    val isSpeakerOn: Boolean = true,
    val durationSeconds: Long = 0
)

@Singleton
class CoupleCallController @Inject constructor() {

    private val _currentCall = MutableStateFlow<CoupleCallSession?>(null)
    val currentCall: StateFlow<CoupleCallSession?> = _currentCall.asStateFlow()

    fun startCall(partnerName: String, callType: CallType) {
        _currentCall.value = CoupleCallSession(
            callId = "call_${System.currentTimeMillis()}",
            partnerName = partnerName,
            callType = callType,
            callState = CallState.OUTGOING_RINGING
        )
    }

    fun acceptCall() {
        _currentCall.value = _currentCall.value?.copy(callState = CallState.CONNECTED)
    }

    fun toggleMic() {
        _currentCall.value = _currentCall.value?.let { it.copy(isMicMuted = !it.isMicMuted) }
    }

    fun toggleCamera() {
        _currentCall.value = _currentCall.value?.let { it.copy(isCameraOn = !it.isCameraOn) }
    }

    fun endCall() {
        _currentCall.value = _currentCall.value?.copy(callState = CallState.ENDED)
        _currentCall.value = null
    }
}
