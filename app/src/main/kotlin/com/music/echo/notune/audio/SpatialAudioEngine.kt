package echo.music.iad1tya.notune.audio

import android.content.Context
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.constants.SpatialAudioEnabledKey
import echo.music.iad1tya.constants.SpatialRoomPresetKey
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.get
import javax.inject.Inject
import javax.inject.Singleton

enum class SpatialRoomPreset(val displayName: String, val reverbDelayMs: Int, val widthRatio: Float) {
    STUDIO("Clean Studio", 10, 1.0f),
    CONCERT_HALL("Concert Hall", 85, 1.8f),
    CATHEDRAL("Cathedral Sanctuary", 140, 2.2f),
    CYBER_CLUB("Cyberpunk Club", 45, 1.5f),
    SUNSET_LOUNGE("Sunset Lounge", 30, 1.2f)
}

@Singleton
class SpatialAudioEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun isSpatialAudioEnabled(): Boolean = context.dataStore.get(SpatialAudioEnabledKey, true)

    suspend fun setSpatialAudioEnabled(enabled: Boolean) {
        context.dataStore.edit { it[SpatialAudioEnabledKey] = enabled }
    }

    suspend fun getActivePreset(): SpatialRoomPreset {
        val presetName = context.dataStore.get(SpatialRoomPresetKey, SpatialRoomPreset.CONCERT_HALL.name)
        return runCatching { SpatialRoomPreset.valueOf(presetName) }.getOrDefault(SpatialRoomPreset.CONCERT_HALL)
    }

    suspend fun setPreset(preset: SpatialRoomPreset) {
        context.dataStore.edit { it[SpatialRoomPresetKey] = preset.name }
    }
}
