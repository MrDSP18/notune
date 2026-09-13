package com.music.echo.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import echo.music.iad1tya.constants.GoogleAccountLinkedKey
import echo.music.iad1tya.constants.GoogleUserEmailKey
import echo.music.iad1tya.constants.GoogleUserIdKey
import echo.music.iad1tya.constants.GoogleUserNameKey
import echo.music.iad1tya.constants.GoogleUserPhotoUrlKey
import echo.music.iad1tya.constants.IsGoogleLoggedInKey
import echo.music.iad1tya.utils.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class GoogleUserAccount(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val isLoggedIn: Boolean = false
)

class GoogleAuthManager(private val context: Context) {

    val googleAccountFlow: Flow<GoogleUserAccount> = context.dataStore.data.map { prefs ->
        GoogleUserAccount(
            id = prefs[GoogleUserIdKey] ?: "",
            name = prefs[GoogleUserNameKey] ?: "",
            email = prefs[GoogleUserEmailKey] ?: "",
            photoUrl = prefs[GoogleUserPhotoUrlKey] ?: "",
            isLoggedIn = prefs[IsGoogleLoggedInKey] ?: false
        )
    }

    suspend fun saveGoogleLogin(
        id: String,
        name: String,
        email: String,
        photoUrl: String = ""
    ) {
        context.dataStore.edit { prefs ->
            prefs[GoogleUserIdKey] = id
            prefs[GoogleUserNameKey] = name
            prefs[GoogleUserEmailKey] = email
            prefs[GoogleUserPhotoUrlKey] = photoUrl
            prefs[IsGoogleLoggedInKey] = true
            prefs[GoogleAccountLinkedKey] = true
        }
    }

    suspend fun signOut() {
        context.dataStore.edit { prefs ->
            prefs[IsGoogleLoggedInKey] = false
            prefs[GoogleAccountLinkedKey] = false
            prefs.remove(GoogleUserIdKey)
            prefs.remove(GoogleUserNameKey)
            prefs.remove(GoogleUserEmailKey)
            prefs.remove(GoogleUserPhotoUrlKey)
        }
    }
}
