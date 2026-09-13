package com.music.echo.notune.personalization.model

import androidx.compose.runtime.Immutable

enum class DiscoveryPreference(val label: String, val familiarRatio: Float) {
    MOSTLY_FAMILIAR("Mostly Familiar", 0.80f),
    BALANCED("Balanced", 0.50f),
    MOSTLY_NEW("Mostly New", 0.30f)
}

@Immutable
data class SelectedArtist(
    val id: String,
    val name: String,
    val imageUrl: String? = null
)

@Immutable
data class TasteProfile(
    val appLanguage: String = "en",
    val musicLanguages: Set<String> = setOf("English"),
    val favoriteArtists: List<SelectedArtist> = emptyList(),
    val favoriteGenres: Set<String> = emptySet(),
    val preferredEras: Set<String> = emptySet(),
    val preferredMoods: Set<String> = emptySet(),
    val discoveryPreference: DiscoveryPreference = DiscoveryPreference.BALANCED,
    val avoidRepetition: Boolean = true,
    val visualStyle: String = "Minimal",
    val onboardingVersion: Int = 1,
    val isOnboardingCompleted: Boolean = false,
    val isOnboardingSkipped: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
