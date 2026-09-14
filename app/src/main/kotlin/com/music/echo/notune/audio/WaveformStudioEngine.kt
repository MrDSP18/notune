package echo.music.iad1tya.notune.audio

import android.content.Context
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.constants.SpectrumVisualizerPresetKey
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.get
import javax.inject.Inject
import javax.inject.Singleton

enum class VisualizerPreset(val title: String) {
    CYBER_RING("Cybernetic Ring"),
    OSCILLOSCOPE("Analog Oscilloscope"),
    NEON_BARS("Neon Spectrum Bars"),
    PARTICLE_FIELD("Particle Nebula"),
    RIBBON("Acoustic Wave Ribbon"),
    STARBURST("Quantum Starburst")
}

@Singleton
class WaveformStudioEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun getActivePreset(): VisualizerPreset {
        val name = context.dataStore.get(SpectrumVisualizerPresetKey, VisualizerPreset.CYBER_RING.name)
        return runCatching { VisualizerPreset.valueOf(name) }.getOrDefault(VisualizerPreset.CYBER_RING)
    }

    suspend fun setPreset(preset: VisualizerPreset) {
        context.dataStore.edit { it[SpectrumVisualizerPresetKey] = preset.name }
    }
}
