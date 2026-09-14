package echo.music.iad1tya.notune.audio

import android.content.Context
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.constants.KaraokeVocalRemoverKey
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.get
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KaraokeStudioEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun isVocalRemoverEnabled(): Boolean = context.dataStore.get(KaraokeVocalRemoverKey, false)

    suspend fun setVocalRemoverEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KaraokeVocalRemoverKey] = enabled }
    }
}
