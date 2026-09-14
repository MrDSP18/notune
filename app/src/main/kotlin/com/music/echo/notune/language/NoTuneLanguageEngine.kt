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
    val region: String = "Global",
    val isRtl: Boolean = false
)

object NoTuneLanguageEngine {

    val SUPPORTED_LANGUAGES = listOf(
        // --- ALL OFFICIAL & REGIONAL INDIAN LANGUAGES ---
        SupportedLanguage("ta", "Tamil", "தமிழ்", "🇮🇳", region = "India"),
        SupportedLanguage("hi", "Hindi", "हिन्दी", "🇮🇳", region = "India"),
        SupportedLanguage("te", "Telugu", "తెలుగు", "🇮🇳", region = "India"),
        SupportedLanguage("ml", "Malayalam", "മലയാളം", "🇮🇳", region = "India"),
        SupportedLanguage("kn", "Kannada", "ಕನ್ನಡ", "🇮🇳", region = "India"),
        SupportedLanguage("mr", "Marathi", "मराठी", "🇮🇳", region = "India"),
        SupportedLanguage("bn", "Bengali", "বাংলা", "🇮🇳", region = "India"),
        SupportedLanguage("gu", "Gujarati", "ગુજરાતી", "🇮🇳", region = "India"),
        SupportedLanguage("pa", "Punjabi", "ਪੰਜਾਬੀ", "🇮🇳", region = "India"),
        SupportedLanguage("or", "Odia", "ଓଡ଼ିଆ", "🇮🇳", region = "India"),
        SupportedLanguage("ur", "Urdu", "اردو", "🇮🇳", region = "India", isRtl = true),
        SupportedLanguage("as", "Assamese", "অসমীয়া", "🇮🇳", region = "India"),
        SupportedLanguage("mai", "Maithili", "मैथिली", "🇮🇳", region = "India"),
        SupportedLanguage("sat", "Santali", "ᱥᱟᱱᱛᱟᱲᱤ", "🇮🇳", region = "India"),
        SupportedLanguage("ks", "Kashmiri", "कॉशुर / كॉشور", "🇮🇳", region = "India"),
        SupportedLanguage("ne", "Nepali", "नेपाली", "🇮🇳", region = "India"),
        SupportedLanguage("kok", "Konkani", "कोंकणी", "🇮🇳", region = "India"),
        SupportedLanguage("doi", "Dogri", "डोगरी", "🇮🇳", region = "India"),
        SupportedLanguage("mni", "Manipuri (Meitei)", "ꯃꯩꯇꯩꯂꯣꯟ", "🇮🇳", region = "India"),
        SupportedLanguage("brx", "Bodo", "बड़ो", "🇮🇳", region = "India"),
        SupportedLanguage("sa", "Sanskrit", "संस्कृतम्", "🇮🇳", region = "India"),
        SupportedLanguage("sd", "Sindhi", "सिन्धी / سنڌي", "🇮🇳", region = "India"),
        SupportedLanguage("bho", "Bhojpuri", "भोजपुरी", "🇮🇳", region = "India"),
        SupportedLanguage("raj", "Rajasthani", "राजस्थानी", "🇮🇳", region = "India"),
        SupportedLanguage("hne", "Chhattisgarhi", "छत्तीसगढ़ी", "🇮🇳", region = "India"),
        SupportedLanguage("bgc", "Haryanvi", "हरियाणवी", "🇮🇳", region = "India"),
        SupportedLanguage("tcy", "Tulu", "ತುಳು", "🇮🇳", region = "India"),

        // --- GLOBAL LANGUAGES ---
        SupportedLanguage("en", "English", "English", "🇺🇸"),
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
