package com.music.echo.notune.onboarding

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import echo.music.iad1tya.utils.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

enum class ContextualTipKey(val tipText: String) {
    LYRICS_SING_ALONG("Tip: Switch to Sing-Along mode to view pronunciation in your preferred language."),
    FLOW_QUEUE("Tip: Tell NØTUNE how you feel and FLOW will dynamically build your queue."),
    ROOMS_SYNC("Tip: Invite friends to a Room to build a shared queue and listen synchronously."),
    MUSIC_DNA_INSPECT("Tip: Tap any genre or language in Music DNA to see why NØTUNE recommended it.")
}

@Singleton
class ContextualTipManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun shouldShowTip(key: ContextualTipKey): Boolean {
        val prefKey = booleanPreferencesKey("tip_dismissed_${key.name}")
        val dismissed = context.dataStore.data.first()[prefKey] ?: false
        return !dismissed
    }

    fun dismissTip(key: ContextualTipKey) {
        val prefKey = booleanPreferencesKey("tip_dismissed_${key.name}")
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[prefKey] = true
            }
        }
    }
}
