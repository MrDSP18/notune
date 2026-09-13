package com.music.echo.notune.identity

import androidx.compose.runtime.Immutable
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Immutable
enum class AuthProviderType {
    OFFLINE_GUEST,
    GOOGLE,
    GITHUB,
    PHONE_OTP,
    NOTUNE_ACCOUNT
}

@Immutable
data class NotuneDeviceSession(
    val deviceId: String,
    val deviceName: String,
    val isCurrentDevice: Boolean = true,
    val lastActiveAt: Long = System.currentTimeMillis()
)

@Immutable
data class NotuneUserAccount(
    val userId: String,
    val displayName: String,
    val email: String? = null,
    val avatarUrl: String? = null,
    val provider: AuthProviderType = AuthProviderType.OFFLINE_GUEST,
    val isAuthenticated: Boolean = false,
    val activeSessions: List<NotuneDeviceSession> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

@Singleton
class NotuneAccountRepository @Inject constructor() {

    private val _account = MutableStateFlow(
        NotuneUserAccount(
            userId = "local_guest_user",
            displayName = "NØTUNE Listener",
            provider = AuthProviderType.OFFLINE_GUEST,
            isAuthenticated = true,
            activeSessions = listOf(
                NotuneDeviceSession(
                    deviceId = "device_local_primary",
                    deviceName = "NØTUNE Device",
                    isCurrentDevice = true
                )
            )
        )
    )
    val account: StateFlow<NotuneUserAccount> = _account.asStateFlow()

    fun signInWithProvider(
        userId: String,
        displayName: String,
        email: String?,
        avatarUrl: String?,
        provider: AuthProviderType
    ) {
        _account.update { current ->
            current.copy(
                userId = userId,
                displayName = displayName,
                email = email,
                avatarUrl = avatarUrl,
                provider = provider,
                isAuthenticated = true
            )
        }
    }

    fun signOut() {
        _account.update {
            NotuneUserAccount(
                userId = "local_guest_user",
                displayName = "NØTUNE Listener",
                provider = AuthProviderType.OFFLINE_GUEST,
                isAuthenticated = true
            )
        }
    }

    fun revokeSession(deviceId: String) {
        _account.update { current ->
            current.copy(
                activeSessions = current.activeSessions.filter { it.deviceId != deviceId }
            )
        }
    }
}
