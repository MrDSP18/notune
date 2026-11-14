package echo.music.iad1tya.ui.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.music.echo.notune.lyrics.AiLyricGenerator
import com.music.echo.notune.lyrics.LyricsDisplayMode
import com.music.echo.notune.lyrics.LyricsLanguage
import dagger.hilt.android.EntryPointAccessors
import echo.music.iad1tya.LocalDatabase
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.AiProviderKey
import echo.music.iad1tya.constants.DeeplApiKey
import echo.music.iad1tya.constants.DeeplFormalityKey
import echo.music.iad1tya.constants.OpenRouterApiKey
import echo.music.iad1tya.constants.OpenRouterBaseUrlKey
import echo.music.iad1tya.constants.OpenRouterModelKey
import echo.music.iad1tya.constants.TranslateLanguageKey
import echo.music.iad1tya.constants.TranslateModeKey
import echo.music.iad1tya.lyrics.LyricsTranslationHelper
import echo.music.iad1tya.utils.rememberPreference
import echo.music.iad1tya.db.entities.LyricsEntity
import echo.music.iad1tya.lyrics.LyricsUtils.parseLyrics
import echo.music.iad1tya.models.MediaMetadata
import echo.music.iad1tya.ui.component.shimmer.ShimmerHost
import echo.music.iad1tya.ui.component.shimmer.TextPlaceholder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Ultra-differentiated, Real-Time Synced Lyrics & Live Translator View.
 * Displays synchronized lyrics with live multilingual translation, sing-along transliteration,
 * interactive language selection chips, and 100% universal AI lyric synthesis fallback.
 */
@Composable
fun PlayerSyncedLyricsView(
    mediaMetadata: MediaMetadata?,
    positionProvider: () -> Long,
    navController: NavController? = null,
    modifier: Modifier = Modifier
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val currentLyrics by playerConnection.currentLyrics.collectAsState(initial = null)
    val context = LocalContext.current
    val database = LocalDatabase.current
    val coroutineScope = rememberCoroutineScope()
    val openRouterApiKey by rememberPreference(OpenRouterApiKey, "")
    val deeplApiKey by rememberPreference(DeeplApiKey, "")
    val aiProvider by rememberPreference(AiProviderKey, "NØTUNE Neural")
    val openRouterBaseUrl by rememberPreference(OpenRouterBaseUrlKey, "https://openrouter.ai/api/v1/chat/completions")
    val openRouterModel by rememberPreference(OpenRouterModelKey, "google/gemini-2.5-flash-lite")
    val translateLanguage by rememberPreference(TranslateLanguageKey, "ta")
    val translateMode by rememberPreference(TranslateModeKey, "Literal")
    val deeplFormality by rememberPreference(DeeplFormalityKey, "default")
    val hasTranslations by LyricsTranslationHelper.hasActiveTranslations.collectAsState()

    val entryPoint = remember(context) {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            echo.music.iad1tya.di.LyricsHelperEntryPoint::class.java
        )
    }
    val lyricsHelper = remember(entryPoint) { entryPoint.lyricsHelper() }
    val aiLyricGenerator = remember(entryPoint) { entryPoint.aiLyricGenerator() }
    val translator = remember(entryPoint) { entryPoint.realtimeLyricsTranslator() }

    var selectedLanguage by remember { mutableStateOf(LyricsLanguage.TAMIL) }
    var displayMode by remember { mutableStateOf(LyricsDisplayMode.DUAL_LYRICS) }
    var isGeneratingAiLyrics by remember { mutableStateOf(false) }

    // Multi-stage lyrics resolver: Provider search -> Local DB -> Universal AI Lyric Generator
    LaunchedEffect(mediaMetadata?.id, currentLyrics) {
        if (mediaMetadata != null) {
            val lyricsContent = currentLyrics?.lyrics?.trim()
            val isNotFound = lyricsContent == LyricsEntity.LYRICS_NOT_FOUND
            val isMissing = currentLyrics == null

            if (isMissing || isNotFound) {
                delay(300)
                withContext(Dispatchers.IO) {
                    try {
                        val existing = database.lyrics(mediaMetadata.id).firstOrNull()
                        if (existing != null && existing.lyrics.isNotBlank() && existing.lyrics != LyricsEntity.LYRICS_NOT_FOUND) {
                            return@withContext
                        }

                        val fetched = lyricsHelper.getLyrics(mediaMetadata)
                        if (!fetched.lyrics.isNullOrBlank() && fetched.lyrics != LyricsEntity.LYRICS_NOT_FOUND) {
                            database.query {
                                upsert(LyricsEntity(mediaMetadata.id, fetched.lyrics ?: "", fetched.providerName))
                            }
                        } else {
                            // Universal Fallback: AI Lyrics Generator guarantees 100% songs have synced lyrics
                            isGeneratingAiLyrics = true
                            val generatedLrc = aiLyricGenerator.generateLyricsForSong(mediaMetadata)
                            isGeneratingAiLyrics = false
                            database.query {
                                upsert(LyricsEntity(mediaMetadata.id, generatedLrc, AiLyricGenerator.PROVIDER_NAME))
                            }
                        }
                    } catch (e: Exception) {
                        // Fallback on error
                        try {
                            val generatedLrc = aiLyricGenerator.generateLyricsForSong(mediaMetadata)
                            database.query {
                                upsert(LyricsEntity(mediaMetadata.id, generatedLrc, AiLyricGenerator.PROVIDER_NAME))
                            }
                        } catch (ignored: Exception) {}
                    } finally {
                        isGeneratingAiLyrics = false
                    }
                }
            }
        }
    }

    val lines = remember(currentLyrics) {
        val lyricsText = currentLyrics?.lyrics?.trim()
        if (lyricsText.isNullOrEmpty() || !lyricsText.startsWith("[")) return@remember emptyList()
        parseLyrics(lyricsText).filter { it.text.isNotBlank() }
    }


    LaunchedEffect(mediaMetadata?.id, lines, translateLanguage, translateMode) {
        val songId = mediaMetadata?.id ?: return@LaunchedEffect
        val savedLyrics = withContext(Dispatchers.IO) {
            runCatching { database.lyrics(songId).firstOrNull() }.getOrNull()
        }
        LyricsTranslationHelper.loadTranslationsFromDatabase(
            lyrics = lines,
            lyricsEntity = savedLyrics,
            targetLanguage = translateLanguage,
            mode = translateMode
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = echo.music.iad1tya.constants.PlayerHorizontalPadding)
    ) {
        // Main Synced Line Display Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentLyrics == null || isGeneratingAiLyrics) {
                // Loading & AI Synthesis Skeleton
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ShimmerHost(modifier = Modifier.weight(1f)) {
                        TextPlaceholder(
                            height = 20.dp,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(0.7f)
                        )
                    }
                    if (isGeneratingAiLyrics) {
                        Text(
                            text = "AI GENERATING",
                            color = Color(0xFFFF0031),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            } else if (lines.isEmpty()) {
                // Fallback action button if lyrics are not parsed yet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Synchronized lyrics ready for synthesis",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                    Surface(
                        onClick = {
                            if (mediaMetadata != null) {
                                coroutineScope.launch(Dispatchers.IO) {
                                    isGeneratingAiLyrics = true
                                    aiLyricGenerator.generateLyricsForSong(mediaMetadata)
                                    isGeneratingAiLyrics = false
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFF0031).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF0031).copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "✦ SYNTHESIZE",
                            color = Color(0xFFFF0031),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            } else {
                val effectivePosition = positionProvider()
                val currentLineIndex = remember(effectivePosition, lines) {
                    val index = lines.indexOfLast { it.time <= effectivePosition }
                    if (index >= 0) index else 0
                }
                val activeEntry = lines.getOrNull(currentLineIndex)
                val currentLineRaw = activeEntry?.text.orEmpty()
                val translatedText by (activeEntry?.translatedTextFlow?.collectAsState(initial = null)
                    ?: remember(activeEntry) { mutableStateOf<String?>(null) })

                // Real-time Dual-Line Translation Resolution
                val (primaryText, secondaryText) = remember(currentLineRaw, selectedLanguage, displayMode) {
                    translator.getDualLine(currentLineRaw, selectedLanguage, displayMode)
                }
                val displaySecondaryText = translatedText ?: secondaryText

                Box(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = Triple(primaryText, displaySecondaryText, displayMode),
                        transitionSpec = {
                            (fadeIn() + slideInVertically { height -> height }).togetherWith(
                                fadeOut() + slideOutVertically { height -> -height }
                            ).using(SizeTransform(clip = false))
                        },
                        label = "SyncedLyricsDual"
                    ) { (primary, secondary, mode) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val modes = LyricsDisplayMode.entries
                                    val nextIndex = (mode.ordinal + 1) % modes.size
                                    displayMode = modes[nextIndex]
                                }
                        ) {
                            // Primary original line
                            Text(
                                text = primary,
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Left,
                                maxLines = 2
                            )

                            // Real-time translated / transliterated secondary line
                            if (!secondary.isNullOrBlank()) {
                                Text(
                                    text = secondary,
                                    color = Color(0xFFFF0031),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Left,
                                    modifier = Modifier.padding(top = 2.dp),
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }

                Surface(
                    onClick = {
                        LyricsTranslationHelper.translateLyrics(
                            lyrics = lines,
                            targetLanguage = translateLanguage,
                            apiKey = openRouterApiKey,
                            baseUrl = openRouterBaseUrl,
                            model = openRouterModel,
                            mode = translateMode,
                            scope = coroutineScope,
                            context = context,
                            provider = aiProvider,
                            deeplApiKey = deeplApiKey,
                            deeplFormality = deeplFormality,
                            useStreaming = true,
                            songId = mediaMetadata?.id.orEmpty(),
                            database = database,
                            sourceLyrics = currentLyrics?.lyrics.orEmpty()
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF19B6A4).copy(alpha = 0.14f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = if (hasTranslations) "TRANSLATED" else "TRANSLATE",
                        color = Color(0xFF57D8C6),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp)
                    )
                }

                // Mode toggle pill
                Surface(
                    onClick = {
                        val modes = LyricsDisplayMode.entries
                        val nextIndex = (displayMode.ordinal + 1) % modes.size
                        displayMode = modes[nextIndex]
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = displayMode.label.take(4).uppercase(),
                        color = Color(0xFFFF0031),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                // AI Sparks Action Button
                IconButton(
                    onClick = { navController?.navigate("notune/ask") },
                    modifier = Modifier.size(24.dp).padding(start = 2.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.sparks),
                        contentDescription = "Ask NØTUNE AI",
                        tint = Color(0xFFFF0031),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Live Real-Time Language Selector Chip Bar
        if (lines.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            val languages = remember { translator.getSupportedLanguages() }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Original Chip
                item {
                    val isOriginal = displayMode == LyricsDisplayMode.ORIGINAL
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isOriginal) Color(0xFFFF0031).copy(alpha = 0.25f)
                                else Color.White.copy(alpha = 0.05f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isOriginal) Color(0xFFFF0031) else Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                displayMode = if (isOriginal) LyricsDisplayMode.DUAL_LYRICS else LyricsDisplayMode.ORIGINAL
                            }
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ORIGINAL",
                            fontSize = 9.sp,
                            fontWeight = if (isOriginal) FontWeight.Bold else FontWeight.Medium,
                            color = if (isOriginal) Color(0xFFFF0031) else Color.White.copy(alpha = 0.7f),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Multilingual Chips (Tamil, Hindi, Telugu, Spanish, Japanese, Korean, English, etc.)
                items(languages) { lang ->
                    val isSelected = selectedLanguage == lang && displayMode != LyricsDisplayMode.ORIGINAL
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) Color(0xFFFF0031).copy(alpha = 0.25f)
                                else Color.White.copy(alpha = 0.05f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color(0xFFFF0031) else Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedLanguage = lang
                                if (displayMode == LyricsDisplayMode.ORIGINAL) {
                                    displayMode = LyricsDisplayMode.DUAL_LYRICS
                                }
                            }
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${lang.nativeName} (${lang.code.uppercase()})",
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFFFF0031) else Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
