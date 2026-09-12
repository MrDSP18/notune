package com.music.echo.notune.privacy

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.constants.PrivateSessionEnabledKey
import echo.music.iad1tya.utils.dataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrivateSessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val _isPrivateSession = MutableStateFlow(false)
    val isPrivateSession: StateFlow<Boolean> = _isPrivateSession.asStateFlow()

    init {
        scope.launch {
            context.dataStore.data.collect { prefs ->
                val enabled = prefs[PrivateSessionEnabledKey] ?: false
                _isPrivateSession.value = enabled
                Timber.d("PRIVATE_SESSION: Status updated = $enabled")
            }
        }
    }

    suspend fun setPrivateSession(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PrivateSessionEnabledKey] = enabled
        }
        _isPrivateSession.value = enabled
    }

    /**
     * Determines whether a history record or personalization update should be executed.
     * Returns true if learning/recording is ALLOWED (i.e. Private Session is OFF).
     */
    fun shouldRecordHistoryAndPersonalization(): Boolean {
        return !_isPrivateSession.value
    }
}
