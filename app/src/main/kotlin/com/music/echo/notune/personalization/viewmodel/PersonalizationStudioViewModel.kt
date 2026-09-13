package com.music.echo.notune.personalization.viewmodel

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import echo.music.iad1tya.constants.*
import echo.music.iad1tya.utils.dataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PersonalizationStudioState(
    val appearanceMode: AppearanceMode = AppearanceMode.SYSTEM,
    val themePresetId: String = "notune_pure",
    val fontStyle: FontFamilyStyle = FontFamilyStyle.SANS,
    val logoVariant: LogoVariant = LogoVariant.WORDMARK,
    val playerStyle: PlayerStyleVariant = PlayerStyleVariant.MINIMAL,
    val miniPlayerStyle: MiniPlayerStyleVariant = MiniPlayerStyleVariant.COMPACT,
    val navStyle: NavigationStyleVariant = NavigationStyleVariant.BOTTOM_BAR,
    val cardStyle: CardStyleVariant = CardStyleVariant.CLEAN,
    val iconStyle: IconStyleVariant = IconStyleVariant.MINIMAL,
    val uiContext: UiContext = UiContext.NORMAL,
    val density: UiDensity = UiDensity.BALANCED,
    val animationLevel: AnimationLevel = AnimationLevel.FULL,
    val cornerRadius: CornerRadiusStyle = CornerRadiusStyle.ROUNDED,
    val appLanguage: String = "en"
)

@HiltViewModel
class PersonalizationStudioViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    val uiState: StateFlow<PersonalizationStudioState> = context.dataStore.data.map { prefs ->
        PersonalizationStudioState(
            appearanceMode = AppearanceMode.fromName(prefs[AppearanceModeKey] ?: AppearanceMode.SYSTEM.name),
            themePresetId = prefs[ThemePresetVariantKey] ?: "notune_pure",
            fontStyle = FontFamilyStyle.fromName(prefs[FontFamilyStyleKey] ?: FontFamilyStyle.SANS.name),
            logoVariant = LogoVariant.fromName(prefs[LogoVariantKey] ?: LogoVariant.WORDMARK.name),
            playerStyle = PlayerStyleVariant.fromName(prefs[PlayerStyleKey] ?: PlayerStyleVariant.MINIMAL.name),
            miniPlayerStyle = MiniPlayerStyleVariant.fromName(prefs[MiniPlayerStyleKey] ?: MiniPlayerStyleVariant.COMPACT.name),
            navStyle = NavigationStyleVariant.fromName(prefs[NavigationStyleKey] ?: NavigationStyleVariant.BOTTOM_BAR.name),
            cardStyle = CardStyleVariant.fromName(prefs[CardStyleKey] ?: CardStyleVariant.CLEAN.name),
            iconStyle = IconStyleVariant.fromName(prefs[IconStyleKey] ?: IconStyleVariant.MINIMAL.name),
            uiContext = UiContext.fromName(prefs[UiContextKey] ?: UiContext.NORMAL.name),
            density = UiDensity.fromName(prefs[UiDensityKey] ?: UiDensity.BALANCED.name),
            animationLevel = AnimationLevel.fromName(prefs[AnimationLevelKey] ?: AnimationLevel.FULL.name),
            cornerRadius = CornerRadiusStyle.fromName(prefs[CornerRadiusStyleKey] ?: CornerRadiusStyle.ROUNDED.name),
            appLanguage = prefs[AppLanguageKey] ?: "en"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PersonalizationStudioState()
    )

    fun updateAppearanceMode(mode: AppearanceMode) = viewModelScope.launch {
        context.dataStore.edit { prefs -> prefs[AppearanceModeKey] = mode.name }
    }

    fun updateThemePreset(themeId: String) = viewModelScope.launch {
        context.dataStore.edit { prefs -> prefs[ThemePresetVariantKey] = themeId }
    }

    fun updateFontStyle(fontStyle: FontFamilyStyle) = viewModelScope.launch {
        context.dataStore.edit { prefs -> prefs[FontFamilyStyleKey] = fontStyle.name }
    }

    fun updateLogoVariant(variant: LogoVariant) = viewModelScope.launch {
        context.dataStore.edit { prefs -> prefs[LogoVariantKey] = variant.name }
    }

    fun updatePlayerStyle(style: PlayerStyleVariant) = viewModelScope.launch {
        context.dataStore.edit { prefs -> prefs[PlayerStyleKey] = style.name }
    }

    fun updateMiniPlayerStyle(style: MiniPlayerStyleVariant) = viewModelScope.launch {
        context.dataStore.edit { prefs -> prefs[MiniPlayerStyleKey] = style.name }
    }

    fun updateNavigationStyle(style: NavigationStyleVariant) = viewModelScope.launch {
        context.dataStore.edit { prefs -> prefs[NavigationStyleKey] = style.name }
    }

    fun updateCardStyle(style: CardStyleVariant) = viewModelScope.launch {
        context.dataStore.edit { prefs -> prefs[CardStyleKey] = style.name }
    }

    fun updateUiContext(uiContext: UiContext) = viewModelScope.launch {
        context.dataStore.edit { prefs -> prefs[UiContextKey] = uiContext.name }
    }

    fun updateUiDensity(density: UiDensity) = viewModelScope.launch {
        context.dataStore.edit { prefs -> prefs[UiDensityKey] = density.name }
    }

    fun updateAnimationLevel(level: AnimationLevel) = viewModelScope.launch {
        context.dataStore.edit { prefs -> prefs[AnimationLevelKey] = level.name }
    }

    fun updateCornerRadius(radius: CornerRadiusStyle) = viewModelScope.launch {
        context.dataStore.edit { prefs -> prefs[CornerRadiusStyleKey] = radius.name }
    }

    fun resetAllCustomizations() = viewModelScope.launch {
        context.dataStore.edit { prefs ->
            prefs.remove(AppearanceModeKey)
            prefs.remove(ThemePresetVariantKey)
            prefs.remove(FontFamilyStyleKey)
            prefs.remove(LogoVariantKey)
            prefs.remove(PlayerStyleKey)
            prefs.remove(MiniPlayerStyleKey)
            prefs.remove(NavigationStyleKey)
            prefs.remove(CardStyleKey)
            prefs.remove(IconStyleKey)
            prefs.remove(UiContextKey)
            prefs.remove(UiDensityKey)
            prefs.remove(AnimationLevelKey)
            prefs.remove(CornerRadiusStyleKey)
        }
    }
}
