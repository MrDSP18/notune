package com.music.echo.notune.auth

import com.music.echo.notune.config.PlatformConfig
import com.music.echo.notune.identity.AuthProviderType
import com.music.echo.notune.identity.NotuneAccountRepository
import com.music.echo.notune.privacy.security.SecureStorageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    data class Success(val userId: String, val username: String, val email: String?) : AuthResult()
    data class Error(val code: String, val message: String) : AuthResult()
}

@Singleton
class NoTuneAuthManager @Inject constructor(
    private val secureStorageManager: SecureStorageManager,
    private val accountRepository: NotuneAccountRepository
) {
    companion object {
        const val KEY_ACCESS_TOKEN = "user_session_token"
        const val KEY_REFRESH_TOKEN = "user_refresh_token"
    }

    suspend fun register(
        username: String,
        email: String,
        password: String,
        displayName: String? = null
    ): AuthResult = withContext(Dispatchers.IO) {
        try {
            val url = URL(PlatformConfig.authRegisterEndpoint())
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 8000
                readTimeout = 8000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }

            val body = JSONObject().apply {
                put("username", username.trim())
                put("email", email.trim())
                put("password", password)
                if (!displayName.isNullOrBlank()) {
                    put("displayName", displayName.trim())
                }
            }.toString()

            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }

            val responseCode = conn.responseCode
            val responseText = (if (responseCode in 200..299) conn.inputStream else conn.errorStream)
                ?.bufferedReader()?.use { it.readText() } ?: ""

            val json = JSONObject(responseText)
            if (responseCode == 201 && json.optBoolean("success")) {
                val user = json.getJSONObject("user")
                val tokens = json.getJSONObject("tokens")
                val accessToken = tokens.getString("accessToken")
                val refreshToken = tokens.getString("refreshToken")

                secureStorageManager.storeSecret(KEY_ACCESS_TOKEN, accessToken)
                secureStorageManager.storeSecret(KEY_REFRESH_TOKEN, refreshToken)

                val emailVal = user.optStringOrNull("email")
                val avatarVal = user.optStringOrNull("avatarUrl")
                val displayNameVal = user.optStringOrNull("displayName") ?: user.optString("username", "Listener")

                accountRepository.signInWithProvider(
                    userId = user.getString("id"),
                    displayName = displayNameVal,
                    email = emailVal,
                    avatarUrl = avatarVal,
                    provider = AuthProviderType.NOTUNE_ACCOUNT
                )

                AuthResult.Success(
                    userId = user.getString("id"),
                    username = user.getString("username"),
                    email = emailVal
                )
            } else {
                AuthResult.Error(
                    code = json.optString("error", "REGISTRATION_FAILED"),
                    message = json.optString("message", "Registration failed.")
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "NoTune registration network error")
            AuthResult.Error(code = "NETWORK_ERROR", message = e.message ?: "Network error occurred.")
        }
    }

    suspend fun login(
        identifier: String,
        password: String
    ): AuthResult = withContext(Dispatchers.IO) {
        try {
            val url = URL(PlatformConfig.authLoginEndpoint())
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 8000
                readTimeout = 8000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }

            val body = JSONObject().apply {
                put("identifier", identifier.trim())
                put("password", password)
            }.toString()

            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }

            val responseCode = conn.responseCode
            val responseText = (if (responseCode in 200..299) conn.inputStream else conn.errorStream)
                ?.bufferedReader()?.use { it.readText() } ?: ""

            val json = JSONObject(responseText)
            if (responseCode == 200 && json.optBoolean("success")) {
                val user = json.getJSONObject("user")
                val tokens = json.getJSONObject("tokens")
                val accessToken = tokens.getString("accessToken")
                val refreshToken = tokens.getString("refreshToken")

                secureStorageManager.storeSecret(KEY_ACCESS_TOKEN, accessToken)
                secureStorageManager.storeSecret(KEY_REFRESH_TOKEN, refreshToken)

                val emailVal = user.optStringOrNull("email")
                val avatarVal = user.optStringOrNull("avatarUrl")
                val displayNameVal = user.optStringOrNull("displayName") ?: user.optString("username", "Listener")

                accountRepository.signInWithProvider(
                    userId = user.getString("id"),
                    displayName = displayNameVal,
                    email = emailVal,
                    avatarUrl = avatarVal,
                    provider = AuthProviderType.NOTUNE_ACCOUNT
                )

                AuthResult.Success(
                    userId = user.getString("id"),
                    username = user.getString("username"),
                    email = emailVal
                )
            } else {
                AuthResult.Error(
                    code = json.optString("error", "LOGIN_FAILED"),
                    message = json.optString("message", "Authentication failed.")
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "NoTune login network error")
            AuthResult.Error(code = "NETWORK_ERROR", message = e.message ?: "Network error occurred.")
        }
    }

    suspend fun logout(): Boolean = withContext(Dispatchers.IO) {
        val refreshToken = secureStorageManager.getSecret(KEY_REFRESH_TOKEN)
        if (!refreshToken.isNullOrEmpty()) {
            try {
                val url = URL(PlatformConfig.authLogoutEndpoint())
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 4000
                    readTimeout = 4000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                }
                val body = JSONObject().apply { put("refreshToken", refreshToken) }.toString()
                conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
                conn.responseCode
            } catch (e: Exception) {
                Timber.w(e, "Logout network call failed, clearing local tokens anyway")
            }
        }
        secureStorageManager.removeSecret(KEY_ACCESS_TOKEN)
        secureStorageManager.removeSecret(KEY_REFRESH_TOKEN)
        accountRepository.signOut()
        true
    }

    fun getActiveAccessToken(): String? {
        return secureStorageManager.getSecret(KEY_ACCESS_TOKEN)
    }

    fun isAuthenticated(): Boolean {
        return !secureStorageManager.getSecret(KEY_ACCESS_TOKEN).isNullOrEmpty() &&
               accountRepository.account.value.provider == AuthProviderType.NOTUNE_ACCOUNT
    }
}

private fun JSONObject.optStringOrNull(key: String): String? {
    return if (has(key) && !isNull(key)) {
        val v = getString(key).trim()
        if (v.isEmpty()) null else v
    } else {
        null
    }
}
