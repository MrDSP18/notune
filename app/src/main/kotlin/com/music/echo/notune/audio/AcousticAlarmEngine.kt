package echo.music.iad1tya.notune.audio

import android.content.Context
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.constants.AcousticAlarmEnabledKey
import echo.music.iad1tya.constants.AcousticAlarmTimeKey
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.get
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AcousticAlarmEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun isAlarmEnabled(): Boolean = context.dataStore.get(AcousticAlarmEnabledKey, false)

    suspend fun getAlarmTime(): String = context.dataStore.get(AcousticAlarmTimeKey, "07:00")

    suspend fun setAlarm(enabled: Boolean, timeHHMM: String = "07:00") {
        context.dataStore.edit {
            it[AcousticAlarmEnabledKey] = enabled
            it[AcousticAlarmTimeKey] = timeHHMM
        }
    }
}
