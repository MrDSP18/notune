package com.music.echo.notune.personalization.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.music.echo.notune.personalization.model.DiscoveryPreference
import com.music.echo.notune.personalization.model.SelectedArtist
import com.music.echo.notune.personalization.model.TasteProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.utils.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasteProfileRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val AppLanguagePrefKey = stringPreferencesKey("taste_app_language")
        val MusicLanguagesPrefKey = stringSetPreferencesKey("taste_music_languages")
        val FavoriteArtistsJsonPrefKey = stringPreferencesKey("taste_favorite_artists_json")
        val FavoriteGenresPrefKey = stringSetPreferencesKey("taste_favorite_genres")
        val PreferredErasPrefKey = stringSetPreferencesKey("taste_preferred_eras")
        val PreferredMoodsPrefKey = stringSetPreferencesKey("taste_preferred_moods")
        val DiscoveryPreferencePrefKey = stringPreferencesKey("taste_discovery_preference")
        val AvoidRepetitionPrefKey = booleanPreferencesKey("taste_avoid_repetition")
        val VisualStylePrefKey = stringPreferencesKey("taste_visual_style")
        val OnboardingVersionPrefKey = intPreferencesKey("taste_onboarding_version")
        val IsOnboardingCompletedPrefKey = booleanPreferencesKey("taste_onboarding_completed")
        val IsOnboardingSkippedPrefKey = booleanPreferencesKey("taste_onboarding_skipped")
    }

    val tasteProfile: Flow<TasteProfile> = context.dataStore.data.map { prefs ->
        val appLanguage = prefs[AppLanguagePrefKey] ?: "en"
        val musicLanguages = prefs[MusicLanguagesPrefKey] ?: setOf("English")
        val artistsJson = prefs[FavoriteArtistsJsonPrefKey] ?: "[]"
        val genres = prefs[FavoriteGenresPrefKey] ?: emptySet()
        val eras = prefs[PreferredErasPrefKey] ?: emptySet()
        val moods = prefs[PreferredMoodsPrefKey] ?: emptySet()
        val discoveryStr = prefs[DiscoveryPreferencePrefKey] ?: DiscoveryPreference.BALANCED.name
        val discovery = runCatching { DiscoveryPreference.valueOf(discoveryStr) }.getOrDefault(DiscoveryPreference.BALANCED)
        val avoidRepetition = prefs[AvoidRepetitionPrefKey] ?: true
        val visualStyle = prefs[VisualStylePrefKey] ?: "Minimal"
        val onboardingVersion = prefs[OnboardingVersionPrefKey] ?: 1
        val isCompleted = prefs[IsOnboardingCompletedPrefKey] ?: false
        val isSkipped = prefs[IsOnboardingSkippedPrefKey] ?: false

        TasteProfile(
            appLanguage = appLanguage,
            musicLanguages = musicLanguages,
            favoriteArtists = parseArtistsJson(artistsJson),
            favoriteGenres = genres,
            preferredEras = eras,
            preferredMoods = moods,
            discoveryPreference = discovery,
            avoidRepetition = avoidRepetition,
            visualStyle = visualStyle,
            onboardingVersion = onboardingVersion,
            isOnboardingCompleted = isCompleted,
            isOnboardingSkipped = isSkipped
        )
    }

    suspend fun getTasteProfileOnce(): TasteProfile = withContext(Dispatchers.IO) {
        tasteProfile.first()
    }

    suspend fun updateTasteProfile(transform: (TasteProfile) -> TasteProfile) = withContext(Dispatchers.IO) {
        val current = getTasteProfileOnce()
        val updated = transform(current)
        context.dataStore.edit { prefs ->
            prefs[AppLanguagePrefKey] = updated.appLanguage
            prefs[MusicLanguagesPrefKey] = updated.musicLanguages
            prefs[FavoriteArtistsJsonPrefKey] = serializeArtistsJson(updated.favoriteArtists)
            prefs[FavoriteGenresPrefKey] = updated.favoriteGenres
            prefs[PreferredErasPrefKey] = updated.preferredEras
            prefs[PreferredMoodsPrefKey] = updated.preferredMoods
            prefs[DiscoveryPreferencePrefKey] = updated.discoveryPreference.name
            prefs[AvoidRepetitionPrefKey] = updated.avoidRepetition
            prefs[VisualStylePrefKey] = updated.visualStyle
            prefs[OnboardingVersionPrefKey] = updated.onboardingVersion
            prefs[IsOnboardingCompletedPrefKey] = updated.isOnboardingCompleted
            prefs[IsOnboardingSkippedPrefKey] = updated.isOnboardingSkipped
        }
    }

    suspend fun completeOnboarding() = updateTasteProfile {
        it.copy(isOnboardingCompleted = true, isOnboardingSkipped = false)
    }

    suspend fun skipOnboarding() = updateTasteProfile {
        it.copy(isOnboardingCompleted = true, isOnboardingSkipped = true)
    }

    suspend fun resetTasteProfile() = withContext(Dispatchers.IO) {
        context.dataStore.edit { prefs ->
            prefs.remove(AppLanguagePrefKey)
            prefs.remove(MusicLanguagesPrefKey)
            prefs.remove(FavoriteArtistsJsonPrefKey)
            prefs.remove(FavoriteGenresPrefKey)
            prefs.remove(PreferredErasPrefKey)
            prefs.remove(PreferredMoodsPrefKey)
            prefs.remove(DiscoveryPreferencePrefKey)
            prefs.remove(AvoidRepetitionPrefKey)
            prefs.remove(VisualStylePrefKey)
            prefs.remove(IsOnboardingCompletedPrefKey)
            prefs.remove(IsOnboardingSkippedPrefKey)
        }
    }

    private fun parseArtistsJson(jsonStr: String): List<SelectedArtist> {
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<SelectedArtist>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    SelectedArtist(
                        id = obj.optString("id"),
                        name = obj.optString("name"),
                        imageUrl = obj.optString("imageUrl").takeIf { it.isNotBlank() }
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun serializeArtistsJson(artists: List<SelectedArtist>): String {
        val array = JSONArray()
        for (artist in artists) {
            val obj = JSONObject()
            obj.put("id", artist.id)
            obj.put("name", artist.name)
            obj.put("imageUrl", artist.imageUrl ?: "")
            array.put(obj)
        }
        return array.toString()
    }
}
