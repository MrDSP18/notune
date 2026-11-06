package com.music.echo.notune.personalization.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.utils.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

enum class NotuneThemePreset {
    NOTUNE_RED,
    OLED_DARK,
    MONO_MINIMAL,
    CYBER_NEON,
    GLASS_FROST,
    SUNSET_AMBER,
    CUSTOM
}

enum class NotuneGeometryPreset {
    SHARP,
    SOFT,
    PILL,
    CIRCLE,
    TECHNICAL
}

data class NotuneThemeConfig(
    val preset: NotuneThemePreset = NotuneThemePreset.NOTUNE_RED,
    val primaryColorHex: String = "#FF0031",
    val secondaryColorHex: String = "#333333",
    val backgroundColorHex: String = "#0A0A0A",
    val surfaceColorHex: String = "#121212",
    val playerColorHex: String = "#1A1A1A",
    val lyricsColorHex: String = "#FF0031"
)

data class NotuneTypographyConfig(
    val fontScale: Float = 1.0f,
    val useMonospaceMode: Boolean = false,
    val letterSpacingScale: Float = 1.0f
)

data class NotuneGeometryConfig(
    val preset: NotuneGeometryPreset = NotuneGeometryPreset.SOFT,
    val cornerRadiusDp: Int = 12
)

data class NotuneEffectsConfig(
    val blurIntensity: Float = 0.5f,
    val glassmorphismEnabled: Boolean = true,
    val reducedMotion: Boolean = false,
    val glowEffectsEnabled: Boolean = true
)

data class NotunePlayerConfig(
    val showArtwork: Boolean = true,
    val showLyrics: Boolean = true,
    val showVisualizer: Boolean = true,
    val showQueuePreview: Boolean = true,
    val showAiButton: Boolean = true
)

data class NotuneComponentConfig(
    val showRecentlyPlayed: Boolean = true,
    val showForYou: Boolean = true,
    val showMusicDna: Boolean = true,
    val showRooms: Boolean = true,
    val showDiscovery: Boolean = true
)

data class NotuneCustomizationConfig(
    val theme: NotuneThemeConfig = NotuneThemeConfig(),
    val typography: NotuneTypographyConfig = NotuneTypographyConfig(),
    val geometry: NotuneGeometryConfig = NotuneGeometryConfig(),
    val effects: NotuneEffectsConfig = NotuneEffectsConfig(),
    val player: NotunePlayerConfig = NotunePlayerConfig(),
    val components: NotuneComponentConfig = NotuneComponentConfig()
)

@Singleton
class NotuneCustomizationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val CustomizationJsonKey = stringPreferencesKey("notune_customization_config_json")
    }

    val customizationConfig: Flow<NotuneCustomizationConfig> = context.dataStore.data.map { prefs ->
        val jsonStr = prefs[CustomizationJsonKey] ?: ""
        if (jsonStr.isBlank()) NotuneCustomizationConfig()
        else parseConfigJson(jsonStr)
    }

    suspend fun getConfigOnce(): NotuneCustomizationConfig = withContext(Dispatchers.IO) {
        customizationConfig.first()
    }

    suspend fun updateConfig(transform: (NotuneCustomizationConfig) -> NotuneCustomizationConfig) = withContext(Dispatchers.IO) {
        val current = getConfigOnce()
        val updated = transform(current)
        val jsonStr = serializeConfigJson(updated)
        context.dataStore.edit { prefs ->
            prefs[CustomizationJsonKey] = jsonStr
        }
    }

    fun exportThemeToJson(config: NotuneCustomizationConfig): String {
        return serializeConfigJson(config)
    }

    fun importThemeFromJson(jsonString: String): NotuneCustomizationConfig? {
        return try {
            val parsed = parseConfigJson(jsonString)
            parsed
        } catch (_: Exception) {
            null
        }
    }

    private fun serializeConfigJson(config: NotuneCustomizationConfig): String {
        val root = JSONObject()
        val themeObj = JSONObject().apply {
            put("preset", config.theme.preset.name)
            put("primaryColorHex", config.theme.primaryColorHex)
            put("secondaryColorHex", config.theme.secondaryColorHex)
            put("backgroundColorHex", config.theme.backgroundColorHex)
            put("surfaceColorHex", config.theme.surfaceColorHex)
            put("playerColorHex", config.theme.playerColorHex)
            put("lyricsColorHex", config.theme.lyricsColorHex)
        }
        val typoObj = JSONObject().apply {
            put("fontScale", config.typography.fontScale.toDouble())
            put("useMonospaceMode", config.typography.useMonospaceMode)
            put("letterSpacingScale", config.typography.letterSpacingScale.toDouble())
        }
        val geoObj = JSONObject().apply {
            put("preset", config.geometry.preset.name)
            put("cornerRadiusDp", config.geometry.cornerRadiusDp)
        }
        val effObj = JSONObject().apply {
            put("blurIntensity", config.effects.blurIntensity.toDouble())
            put("glassmorphismEnabled", config.effects.glassmorphismEnabled)
            put("reducedMotion", config.effects.reducedMotion)
            put("glowEffectsEnabled", config.effects.glowEffectsEnabled)
        }
        val playerObj = JSONObject().apply {
            put("showArtwork", config.player.showArtwork)
            put("showLyrics", config.player.showLyrics)
            put("showVisualizer", config.player.showVisualizer)
            put("showQueuePreview", config.player.showQueuePreview)
            put("showAiButton", config.player.showAiButton)
        }
        val compObj = JSONObject().apply {
            put("showRecentlyPlayed", config.components.showRecentlyPlayed)
            put("showForYou", config.components.showForYou)
            put("showMusicDna", config.components.showMusicDna)
            put("showRooms", config.components.showRooms)
            put("showDiscovery", config.components.showDiscovery)
        }
        root.put("theme", themeObj)
        root.put("typography", typoObj)
        root.put("geometry", geoObj)
        root.put("effects", effObj)
        root.put("player", playerObj)
        root.put("components", compObj)
        return root.toString(2)
    }

    private fun parseConfigJson(jsonStr: String): NotuneCustomizationConfig {
        val root = JSONObject(jsonStr)

        val themeObj = root.optJSONObject("theme") ?: JSONObject()
        val presetName = themeObj.optString("preset", NotuneThemePreset.NOTUNE_RED.name)
        val themePreset = runCatching { NotuneThemePreset.valueOf(presetName) }.getOrDefault(NotuneThemePreset.NOTUNE_RED)
        val theme = NotuneThemeConfig(
            preset = themePreset,
            primaryColorHex = themeObj.optString("primaryColorHex", "#FF0031"),
            secondaryColorHex = themeObj.optString("secondaryColorHex", "#333333"),
            backgroundColorHex = themeObj.optString("backgroundColorHex", "#0A0A0A"),
            surfaceColorHex = themeObj.optString("surfaceColorHex", "#121212"),
            playerColorHex = themeObj.optString("playerColorHex", "#1A1A1A"),
            lyricsColorHex = themeObj.optString("lyricsColorHex", "#FF0031")
        )

        val typoObj = root.optJSONObject("typography") ?: JSONObject()
        val typography = NotuneTypographyConfig(
            fontScale = typoObj.optDouble("fontScale", 1.0).toFloat(),
            useMonospaceMode = typoObj.optBoolean("useMonospaceMode", false),
            letterSpacingScale = typoObj.optDouble("letterSpacingScale", 1.0).toFloat()
        )

        val geoObj = root.optJSONObject("geometry") ?: JSONObject()
        val geoPresetName = geoObj.optString("preset", NotuneGeometryPreset.SOFT.name)
        val geoPreset = runCatching { NotuneGeometryPreset.valueOf(geoPresetName) }.getOrDefault(NotuneGeometryPreset.SOFT)
        val geometry = NotuneGeometryConfig(
            preset = geoPreset,
            cornerRadiusDp = geoObj.optInt("cornerRadiusDp", 12)
        )

        val effObj = root.optJSONObject("effects") ?: JSONObject()
        val effects = NotuneEffectsConfig(
            blurIntensity = effObj.optDouble("blurIntensity", 0.5).toFloat(),
            glassmorphismEnabled = effObj.optBoolean("glassmorphismEnabled", true),
            reducedMotion = effObj.optBoolean("reducedMotion", false),
            glowEffectsEnabled = effObj.optBoolean("glowEffectsEnabled", true)
        )

        val playerObj = root.optJSONObject("player") ?: JSONObject()
        val player = NotunePlayerConfig(
            showArtwork = playerObj.optBoolean("showArtwork", true),
            showLyrics = playerObj.optBoolean("showLyrics", true),
            showVisualizer = playerObj.optBoolean("showVisualizer", true),
            showQueuePreview = playerObj.optBoolean("showQueuePreview", true),
            showAiButton = playerObj.optBoolean("showAiButton", true)
        )

        val compObj = root.optJSONObject("components") ?: JSONObject()
        val components = NotuneComponentConfig(
            showRecentlyPlayed = compObj.optBoolean("showRecentlyPlayed", true),
            showForYou = compObj.optBoolean("showForYou", true),
            showMusicDna = compObj.optBoolean("showMusicDna", true),
            showRooms = compObj.optBoolean("showRooms", true),
            showDiscovery = compObj.optBoolean("showDiscovery", true)
        )

        return NotuneCustomizationConfig(
            theme = theme,
            typography = typography,
            geometry = geometry,
            effects = effects,
            player = player,
            components = components
        )
    }
}
