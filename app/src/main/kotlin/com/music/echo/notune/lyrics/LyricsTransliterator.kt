package com.music.echo.notune.lyrics

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Phonetic Transliteration Engine for NØTUNE Lyrics Intelligence 2.0.
 * Preserves exact pronunciation converting Indic and regional scripts to Latin script phonetics.
 */
@Singleton
class LyricsTransliterator @Inject constructor() {

    fun transliterateToPronunciation(text: String, sourceLanguage: LyricsLanguage): String {
        if (text.isBlank()) return text

        val trimmed = text.trim()
        val lowerText = trimmed.lowercase()

        // 1. Check exact phrase override dictionary
        EXACT_PHRASE_PRONUNCIATION[lowerText]?.let { return it }

        // 2. Transliterate based on language / script family
        return when (sourceLanguage) {
            LyricsLanguage.TAMIL -> transliterateTamil(trimmed)
            LyricsLanguage.HINDI, LyricsLanguage.MARATHI, LyricsLanguage.SANSKRIT, LyricsLanguage.KONKANI -> transliterateDevanagari(trimmed)
            LyricsLanguage.TELUGU -> transliterateTelugu(trimmed)
            LyricsLanguage.KANNADA -> transliterateKannada(trimmed)
            LyricsLanguage.MALAYALAM -> transliterateMalayalam(trimmed)
            LyricsLanguage.BENGALI, LyricsLanguage.ASSAMESE -> transliterateBengali(trimmed)
            LyricsLanguage.PUNJABI -> transliterateGurmukhi(trimmed)
            LyricsLanguage.GUJARATI -> transliterateGujarati(trimmed)
            LyricsLanguage.ODIA -> transliterateOdia(trimmed)
            else -> transliterateGenericIndic(trimmed)
        }
    }

    private fun transliterateTamil(text: String): String {
        var result = text
        // Direct word/token replacements for common Tamil lyrics terms
        TAMIL_WORD_PRONUNCIATION.forEach { (native, phonetic) ->
            result = result.replace(native, phonetic)
        }

        val sb = StringBuilder()
        var i = 0
        while (i < result.length) {
            val ch = result[i]
            val mapped = TAMIL_CHAR_MAP[ch]
            if (mapped != null) {
                sb.append(mapped)
            } else {
                sb.append(ch)
            }
            i++
        }
        return cleanupPhonetics(sb.toString())
    }

    private fun transliterateDevanagari(text: String): String {
        var result = text
        DEVANAGARI_WORD_PRONUNCIATION.forEach { (native, phonetic) ->
            result = result.replace(native, phonetic)
        }

        val sb = StringBuilder()
        var i = 0
        while (i < result.length) {
            val ch = result[i]
            val mapped = DEVANAGARI_CHAR_MAP[ch]
            if (mapped != null) {
                sb.append(mapped)
            } else {
                sb.append(ch)
            }
            i++
        }
        return cleanupPhonetics(sb.toString())
    }

    private fun transliterateTelugu(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            sb.append(TELUGU_CHAR_MAP[ch] ?: ch)
        }
        return cleanupPhonetics(sb.toString())
    }

    private fun transliterateKannada(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            sb.append(KANNADA_CHAR_MAP[ch] ?: ch)
        }
        return cleanupPhonetics(sb.toString())
    }

    private fun transliterateMalayalam(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            sb.append(MALAYALAM_CHAR_MAP[ch] ?: ch)
        }
        return cleanupPhonetics(sb.toString())
    }

    private fun transliterateBengali(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            sb.append(BENGALI_CHAR_MAP[ch] ?: ch)
        }
        return cleanupPhonetics(sb.toString())
    }

    private fun transliterateGurmukhi(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            sb.append(GURMUKHI_CHAR_MAP[ch] ?: ch)
        }
        return cleanupPhonetics(sb.toString())
    }

    private fun transliterateGujarati(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            sb.append(GUJARATI_CHAR_MAP[ch] ?: ch)
        }
        return cleanupPhonetics(sb.toString())
    }

    private fun transliterateOdia(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            sb.append(ODIA_CHAR_MAP[ch] ?: ch)
        }
        return cleanupPhonetics(sb.toString())
    }

    private fun transliterateGenericIndic(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            val code = ch.code
            when {
                code in 0x0900..0x097F -> sb.append(DEVANAGARI_CHAR_MAP[ch] ?: ch)
                code in 0x0B80..0x0BFF -> sb.append(TAMIL_CHAR_MAP[ch] ?: ch)
                code in 0x0C00..0x0C7F -> sb.append(TELUGU_CHAR_MAP[ch] ?: ch)
                code in 0x0C80..0x0CFF -> sb.append(KANNADA_CHAR_MAP[ch] ?: ch)
                code in 0x0D00..0x0D7F -> sb.append(MALAYALAM_CHAR_MAP[ch] ?: ch)
                else -> sb.append(ch)
            }
        }
        return cleanupPhonetics(sb.toString())
    }

    private fun cleanupPhonetics(input: String): String {
        return input
            .replace(Regex("\\s+"), " ")
            .replace(Regex("a{3,}"), "aa")
            .replace(Regex("e{3,}"), "ee")
            .replace(Regex("o{3,}"), "oo")
            .trim()
    }

    companion object {
        val EXACT_PHRASE_PRONUNCIATION: Map<String, String> = mapOf(
            "ஜூலை காற்றின் நேரத்தில்" to "July kaatrin nerathil",
            "உன் பார்வை பட்டால்" to "Un paarvai pattaal",
            "तुम्ही हो बंधु" to "Tumhi ho bandhu",
            "केसरिया तेरा इश्क" to "Kesariya tera ishq",
            "నా రోజా నువ్వే" to "Naa Roja Nuvve"
        )

        val TAMIL_WORD_PRONUNCIATION: Map<String, String> = mapOf(
            "ஜூலை" to "July",
            "காற்றின்" to "kaatrin",
            "நேரத்தில்" to "nerathil",
            "காதல்" to "kaadhal",
            "உன்னை" to "unnai",
            "என்" to "en",
            "அன்பே" to "anbe",
            "நெஞ்சில்" to "nenjil"
        )

        val DEVANAGARI_WORD_PRONUNCIATION: Map<String, String> = mapOf(
            "केसरिया" to "Kesariya",
            "तुम्ही" to "tumhi",
            "बन्धु" to "bandhu",
            "दिल" to "dil",
            "प्यार" to "pyaar",
            "तुम" to "tum",
            "हम" to "hum"
        )

        val TAMIL_CHAR_MAP: Map<Char, String> = mapOf(
            'அ' to "a", 'ஆ' to "aa", 'இ' to "i", 'ஈ' to "ee", 'உ' to "u", 'ஊ' to "oo",
            'எ' to "e", 'ஏ' to "ae", 'ஐ' to "ai", 'ஒ' to "o", 'ஓ' to "oh", 'ஔ' to "au",
            'ஃ' to "k", 'க' to "ka", 'ங' to "nga", 'ச' to "cha", 'ஞ' to "nja",
            'ட' to "ta", 'ண' to "na", 'த' to "tha", 'ந' to "na", 'ப' to "pa",
            'ம' to "ma", 'ய' to "ya", 'ர' to "ra", 'ல' to "la", 'வ' to "va",
            'ழ' to "zha", 'ள' to "la", 'ற' to "ra", 'ன' to "na", 'ஜ' to "ja",
            'ஷ' to "sha", 'ஸ' to "sa", 'ஹ' to "ha", '்' to ""
        )

        val DEVANAGARI_CHAR_MAP: Map<Char, String> = mapOf(
            'अ' to "a", 'आ' to "aa", 'इ' to "i", 'ई' to "ee", 'उ' to "u", 'ऊ' to "oo",
            'ए' to "e", 'ऐ' to "ai", 'ओ' to "o", 'औ' to "au",
            'क' to "ka", 'ख' to "kha", 'ग' to "ga", 'घ' to "gha",
            'च' to "cha", 'छ' to "chha", 'ज' to "ja", 'झ' to "jha",
            'ट' to "ta", 'ठ' to "tha", 'ड' to "da", 'ढ' to "dha",
            'त' to "ta", 'थ' to "tha", 'द' to "da", 'ध' to "dha", 'न' to "na",
            'प' to "pa", 'फ' to "pha", 'ब' to "ba", 'भ' to "bha", 'म' to "ma",
            'य' to "ya", 'र' to "ra", 'ल' to "la", 'व' to "va",
            'श' to "sha", 'ष' to "sha", 'स' to "sa", 'ह' to "ha", '्' to ""
        )

        val TELUGU_CHAR_MAP: Map<Char, String> = mapOf(
            'అ' to "a", 'ఆ' to "aa", 'ఇ' to "i", 'ఈ' to "ee", 'ఉ' to "u", 'ఊ' to "oo",
            'ఎ' to "e", 'ఏ' to "ae", 'ఐ' to "ai", 'ఒ' to "o", 'ఓ' to "oh",
            'క' to "ka", 'గ' to "ga", 'చ' to "cha", 'జ' to "ja", 'ట' to "ta",
            'డ' to "da", 'త' to "tha", 'ద' to "da", 'న' to "na", 'ప' to "pa",
            'బ' to "ba", 'మ' to "ma", 'య' to "ya", 'ర' to "ra", 'ల' to "la",
            'వ' to "va", 'శ' to "sha", 'ష' to "sha", 'స' to "sa", 'హ' to "ha", '్' to ""
        )

        val KANNADA_CHAR_MAP: Map<Char, String> = mapOf(
            'ಅ' to "a", 'ಆ' to "aa", 'ಇ' to "i", 'ಈ' to "ee", 'ಉ' to "u", 'ಊ' to "oo",
            'ಎ' to "e", 'ಏ' to "ae", 'ಐ' to "ai", 'ఒ' to "o", 'ఓ' to "oh",
            'ಕ' to "ka", 'గ' to "ga", 'ಚ' to "cha", 'ಜ' to "ja", 'ಟ' to "ta",
            'ಡ' to "da", 'త' to "tha", 'ద' to "da", 'న' to "na", 'ప' to "pa",
            'ಬ' to "ba", 'మ' to "ma", 'య' to "ya", 'ರ' to "ra", 'ಲ' to "la",
            'ವ' to "va", 'ಶ' to "sha", 'ಷ' to "sha", 'ಸ' to "sa", 'హ' to "ha", '್' to ""
        )

        val MALAYALAM_CHAR_MAP: Map<Char, String> = mapOf(
            'അ' to "a", 'ആ' to "aa", 'ഇ' to "i", 'ഈ' to "ee", 'ഉ' to "u", 'ഊ' to "oo",
            'എ' to "e", 'ഏ' to "ae", 'ഐ' to "ai", 'ഒ' to "o", 'ഓ' to "oh",
            'ക' to "ka", 'ഗ' to "ga", 'ച' to "cha", 'ജ' to "ja", 'ട' to "ta",
            'ഡ' to "da", 'ത' to "tha", 'ദ' to "da", 'ന' to "na", 'പ' to "pa",
            'ബ' to "ba", 'മ' to "ma", 'യ' to "ya", 'ര' to "ra", 'ല' to "la",
            'വ' to "va", 'ശ' to "sha", 'ഷ' to "sha", 'സ' to "sa", 'ഹ' to "ha", '്' to ""
        )

        val BENGALI_CHAR_MAP: Map<Char, String> = mapOf(
            'অ' to "o", 'আ' to "aa", 'ই' to "i", 'ঈ' to "ee", 'উ' to "u", 'ঊ' to "oo",
            'ক' to "ko", 'খ' to "kho", 'গ' to "go", 'ঘ' to "gho", 'চ' to "cho",
            'ছ' to "chho", 'জ' to "jo", 'ঝ' to "jho", 'ত' to "to", 'থ' to "tho",
            'দ' to "do", 'ধ' to "dho", 'ন' to "no", 'প' to "po", 'ফ' to "pho",
            'ব' to "bo", 'ভ' to "bho", 'ম' to "mo", 'র' to "ro", 'ল' to "lo"
        )

        val GURMUKHI_CHAR_MAP: Map<Char, String> = mapOf(
            'ਅ' to "a", 'ਆ' to "aa", 'ਇ' to "i", 'ਈ' to "ee", 'ਉ' to "u", 'ਊ' to "oo",
            'ਕ' to "ka", 'ਖ' to "kha", 'ਗ' to "ga", 'ਘ' to "gha", 'ਚ' to "cha",
            'ਛ' to "chha", 'ਜ' to "ja", 'ਝ' to "jha", 'ਤ' to "ta", 'ਥ' to "tha",
            'ਦ' to "da", 'ਧ' to "dha", 'ਨ' to "na", 'ਪ' to "pa", 'ਫ' to "pha",
            'ਬ' to "ba", 'ਭ' to "bha", 'ਮ' to "ma", 'ਰ' to "ra", 'ਲ' to "la"
        )

        val GUJARATI_CHAR_MAP: Map<Char, String> = mapOf(
            'અ' to "a", 'આ' to "aa", 'ઇ' to "i", 'ઈ' to "ee", 'ઉ' to "u", 'ઊ' to "oo",
            'ક' to "ka", 'ખ' to "kha", 'ગ' to "ga", 'ઘ' to "gha", 'ચ' to "cha",
            'છ' to "chha", 'જ' to "ja", 'ઝ' to "jha", 'ત' to "ta", 'થ' to "tha",
            'દ' to "da", 'ધ' to "dha", 'ન' to "na", 'પ' to "pa", 'ફ' to "pha",
            'બ' to "ba", 'ભ' to "bha", 'મ' to "ma", 'ર' to "ra", 'લ' to "la"
        )

        val ODIA_CHAR_MAP: Map<Char, String> = mapOf(
            'ଅ' to "a", 'ଆ' to "aa", 'ଇ' to "i", 'ଈ' to "ee", 'ଉ' to "u", 'ଊ' to "oo",
            'କ' to "ka", 'ଖ' to "kha", 'ଗ' to "ga", 'ଘ' to "gha", 'ଚ' to "cha",
            'ଛ' to "chha", 'ଜ' to "ja", 'ଝ' to "jha", 'ତ' to "ta", 'ଥ' to "tha",
            'ଦ' to "da", 'ଧ' to "dha", 'ନ' to "na", 'ପ' to "pa", 'ଫ' to "pha",
            'ବ' to "ba", 'ଭ' to "bha", 'ମ' to "ma", 'ର' to "ra", 'ଲ' to "la"
        )
    }
}
