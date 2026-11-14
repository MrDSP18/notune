package com.music.echo.notune.lyrics

import echo.music.iad1tya.notune.ai.AiEngine
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber

@Singleton
class LyricsTranslationEngine @Inject constructor(
    private val aiEngine: AiEngine? = null
) {
    /**
     * Translates only lyrics already supplied by an authorized provider or the user.
     * Original line IDs, timestamps, words and ordering are retained.
     */
    suspend fun generateTranslation(
        lines: List<LyricLine>,
        targetLanguage: LyricsLanguage
    ): List<LyricLine> {
        if (lines.isEmpty() || targetLanguage == LyricsLanguage.ENGLISH || aiEngine == null) {
            return lines
        }

        val translated = ArrayList<LyricLine>(lines.size)
        for (batch in lines.chunked(MAX_LINES_PER_REQUEST)) {
            val prompt = buildString {
                appendLine("Translate each supplied lyric line into ${targetLanguage.displayName} (${targetLanguage.nativeName}).")
                appendLine("Preserve meaning, tone, idioms and cultural context.")
                appendLine("Return ONLY a valid JSON array of strings.")
                appendLine("The array must contain exactly ${batch.size} strings in the same order.")
                appendLine("Do not add, remove, explain, summarize, or invent any lyric content.")
                appendLine("Keep names and vocalizations when translating them would change their meaning.")
                appendLine()
                appendLine("SOURCE LINES:")
                batch.forEachIndexed { index, line ->
                    appendLine("${index + 1}. ${line.originalText}")
                }
            }

            val result = runCatching {
                aiEngine.runNeuralMatrixLoop(
                    prompt = prompt,
                    systemInstruction = "You are a precise line-preserving music lyric translator. Translate only the user-provided source lines. Output a JSON string array and nothing else."
                ).getOrThrow()
            }

            val values = result.getOrNull()?.text?.let {
                parseTranslationResponse(it, batch.size)
            }

            if (values == null) {
                result.exceptionOrNull()?.let { Timber.w(it, "LYRICS_AI: Translation request failed.") }
                Timber.w("LYRICS_AI: Invalid translation payload; preserving original lines.")
                translated.addAll(batch)
            } else {
                batch.forEachIndexed { index, line ->
                    translated += line.copy(
                        translatedText = values[index],
                        transformationVersion = 2
                    )
                }
            }
        }
        return translated
    }

    companion object {
        private const val MAX_LINES_PER_REQUEST = 20
    }
}

/**
 * Strictly accepts a JSON string array with one non-empty translation per source line.
 * Returns null rather than displaying malformed, incomplete, or misaligned output.
 */
internal fun parseTranslationResponse(
    response: String,
    expectedLineCount: Int
): List<String>? {
    if (expectedLineCount <= 0 || response.isBlank()) return null

    val cleaned = response.trim()
        .removePrefix("```json")
        .removePrefix("```")
        .removeSuffix("```")
        .trim()

    val start = cleaned.indexOf('[')
    val end = cleaned.lastIndexOf(']')
    if (start < 0 || end <= start) return null

    return runCatching {
        val parsed = Json.parseToJsonElement(cleaned.substring(start, end + 1)) as? JsonArray
            ?: return null
        if (parsed.size != expectedLineCount) return null

        parsed.map { element ->
            val value = element.jsonPrimitive
            if (!value.isString || value.content.isBlank()) return null
            value.content
        }
    }.getOrNull()
}
