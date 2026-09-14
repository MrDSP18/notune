package com.music.echo.notune.language

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.runtime.Immutable
import java.util.Locale

@Immutable
data class SupportedLanguage(
    val code: String,
    val nameInEnglish: String,
    val nativeName: String,
    val flagEmoji: String,
    val isRtl: Boolean = false
)

object NoTuneLanguageEngine {

    val SUPPORTED_LANGUAGES = listOf(
        SupportedLanguage("en", "English", "English", "🇺🇸"),
        SupportedLanguage("ta", "Tamil", "தமிழ்", "🇮🇳"),
        SupportedLanguage("hi", "Hindi", "हिन्दी", "🇮🇳"),
        SupportedLanguage("te", "Telugu", "తెలుగు", "🇮🇳"),
        SupportedLanguage("ml", "Malayalam", "മലയാളം", "🇮🇳"),
        SupportedLanguage("kn", "Kannada", "ಕನ್ನಡ", "🇮🇳"),
        SupportedLanguage("mr", "Marathi", "मराठी", "🇮🇳"),
        SupportedLanguage("bn", "Bengali", "বাংলা", "🇮🇳"),
        SupportedLanguage("gu", "Gujarati", "ગુજરાતી", "🇮🇳"),
        SupportedLanguage("pa", "Punjabi", "ਪੰਜਾਬੀ", "🇮🇳"),
        SupportedLanguage("or", "Odia", "ଓଡ଼ିଆ", "🇮🇳"),
        SupportedLanguage("ur", "Urdu", "اردو", "🇵🇰", isRtl = true),
        SupportedLanguage("es", "Spanish", "Español", "🇪🇸"),
        SupportedLanguage("fr", "French", "Français", "🇫🇷"),
        SupportedLanguage("de", "German", "Deutsch", "🇩🇪"),
        SupportedLanguage("it", "Italian", "Italiano", "🇮🇹"),
        SupportedLanguage("pt", "Portuguese", "Português", "🇧🇷"),
        SupportedLanguage("ru", "Russian", "Русский", "🇷🇺"),
        SupportedLanguage("ja", "Japanese", "日本語", "🇯🇵"),
        SupportedLanguage("ko", "Korean", "한국어", "🇰🇷"),
        SupportedLanguage("zh-CN", "Chinese (Simplified)", "简体中文", "🇨🇳"),
        SupportedLanguage("zh-TW", "Chinese (Traditional)", "繁體中文", "🇹🇼"),
        SupportedLanguage("ar", "Arabic", "العربية", "🇸🇦", isRtl = true),
        SupportedLanguage("he", "Hebrew", "עברית", "🇮🇱", isRtl = true),
        SupportedLanguage("tr", "Turkish", "Türkçe", "🇹🇷"),
        SupportedLanguage("nl", "Dutch", "Nederlands", "🇳🇱"),
        SupportedLanguage("pl", "Polish", "Polski", "🇵🇱"),
        SupportedLanguage("sv", "Swedish", "Svenska", "🇸🇪"),
        SupportedLanguage("vi", "Vietnamese", "Tiếng Việt", "🇻🇳"),
        SupportedLanguage("th", "Thai", "ไทย", "🇹🇭"),
        SupportedLanguage("id", "Indonesian", "Bahasa Indonesia", "🇮🇩"),
        SupportedLanguage("ms", "Malay", "Bahasa Melayu", "🇲🇾"),
        SupportedLanguage("el", "Greek", "Ελληνικά", "🇬🇷"),
        SupportedLanguage("uk", "Ukrainian", "Українська", "🇺🇦"),
        SupportedLanguage("cs", "Czech", "Čeština", "🇨🇿"),
        SupportedLanguage("hu", "Hungarian", "Magyar", "🇭🇺"),
        SupportedLanguage("ro", "Romanian", "Română", "🇷🇴"),
        SupportedLanguage("fi", "Finnish", "Suomi", "🇫🇮"),
        SupportedLanguage("da", "Danish", "Dansk", "🇩🇰"),
        SupportedLanguage("no", "Norwegian", "Norsk", "🇳🇴")
    )

    fun applyLanguage(languageCode: String) {
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)
    }

    fun getLanguageByCode(code: String): SupportedLanguage {
        return SUPPORTED_LANGUAGES.find { it.code.equals(code, ignoreCase = true) }
            ?: SUPPORTED_LANGUAGES.first()
    }
}
