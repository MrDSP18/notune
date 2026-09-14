package echo.music.iad1tya.notune.audio

import android.content.Context
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.constants.AnalogTapeEmulationKey
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.get
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalogTapeEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun isTapeEmulationEnabled(): Boolean = context.dataStore.get(AnalogTapeEmulationKey, false)

    suspend fun setTapeEmulationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[AnalogTapeEmulationKey] = enabled }
    }
}
