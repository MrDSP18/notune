package com.music.echo.notune.social.connect

enum class QrPayloadType {
    USER_PROFILE,
    LIVE_ROOM,
    UNKNOWN
}

data class QrPayloadResult(
    val type: QrPayloadType,
    val identifier: String,
    val rawPayload: String
)

object NoQrEngine {

    fun generateUserPayload(username: String): String = "NOTUNE:USER:${username.trim().removePrefix("@")}"

    fun generateRoomPayload(roomId: String): String = "NOTUNE:ROOM:$roomId"

    fun parsePayload(payload: String): QrPayloadResult {
        val clean = payload.trim()
        return when {
            clean.startsWith("NOTUNE:USER:") -> {
                val user = clean.substringAfter("NOTUNE:USER:")
                QrPayloadResult(QrPayloadType.USER_PROFILE, user, clean)
            }
            clean.startsWith("NOTUNE:ROOM:") -> {
                val room = clean.substringAfter("NOTUNE:ROOM:")
                QrPayloadResult(QrPayloadType.LIVE_ROOM, room, clean)
            }
            else -> QrPayloadResult(QrPayloadType.UNKNOWN, clean, clean)
        }
    }
}
