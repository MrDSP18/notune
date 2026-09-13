package com.music.echo.notune.lyrics

import androidx.compose.runtime.Immutable

@Immutable
enum class LyricsLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val script: String,
    val supportsTransliteration: Boolean = true,
    val supportsTranslation: Boolean = true,
    val supportsPronunciation: Boolean = true
) {
    ENGLISH("en", "English", "English", "Latin"),
    TAMIL("ta", "Tamil", "தமிழ்", "Tamil"),
    TELUGU("te", "Telugu", "తెలుగు", "Telugu"),
    KANNADA("kn", "Kannada", "ಕನ್ನಡ", "Kannada"),
    MALAYALAM("ml", "Malayalam", "മലയാളം", "Malayalam"),
    HINDI("hi", "Hindi", "हिन्दी", "Devanagari"),
    BENGALI("bn", "Bengali", "বাংলা", "Bengali"),
    MARATHI("mr", "Marathi", "मराठी", "Devanagari"),
    GUJARATI("gu", "Gujarati", "ગુજરાતી", "Gujarati"),
    PUNJABI("pa", "Punjabi", "ਪੰਜਾਬੀ", "Gurmukhi"),
    ODIA("or", "Odia", "ଓଡ଼ିଆ", "Odia"),
    ASSAMESE("as", "Assamese", "অসমীয়া", "Bengali"),
    URDU("ur", "Urdu", "اردو", "Perso-Arabic"),
    KONKANI("kok", "Konkani", "कोंकणी", "Devanagari"),
    SANSKRIT("sa", "Sanskrit", "संस्कृतम्", "Devanagari");

    companion object {
        fun fromCode(code: String): LyricsLanguage =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH

        fun fromDisplayName(name: String): LyricsLanguage =
            entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) || it.nativeName.equals(name, ignoreCase = true) } ?: ENGLISH
    }
}
