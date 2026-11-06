package echo.music.iad1tya.ui.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.music.echo.notune.lyrics.LyricsDisplayMode
import com.music.echo.notune.lyrics.LyricsLanguage
import com.music.echo.notune.lyrics.LyricsTransliterator
import dagger.hilt.android.EntryPointAccessors
import echo.music.iad1tya.LocalDatabase
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.R
import echo.music.iad1tya.db.entities.LyricsEntity
import echo.music.iad1tya.lyrics.LyricsUtils.parseLyrics
import echo.music.iad1tya.models.MediaMetadata
import echo.music.iad1tya.ui.component.shimmer.ShimmerHost
import echo.music.iad1tya.ui.component.shimmer.TextPlaceholder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

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
    var displayMode by remember { mutableStateOf(LyricsDisplayMode.DUAL_LYRICS) }
    val transliterator = remember { LyricsTransliterator() }
    
    LaunchedEffect(mediaMetadata?.id, currentLyrics) {
        if (mediaMetadata != null && currentLyrics == null) {
            delay(500)
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val existing = database.lyrics(mediaMetadata.id).firstOrNull()
                    if (existing != null) return@launch
                    val entryPoint = EntryPointAccessors.fromApplication(
                        context.applicationContext,
                        echo.music.iad1tya.di.LyricsHelperEntryPoint::class.java
                    )
                    val lyricsHelper = entryPoint.lyricsHelper()
                    val fetchedLyricsWithProvider = lyricsHelper.getLyrics(mediaMetadata)
                    database.query {
                        upsert(LyricsEntity(mediaMetadata.id, fetchedLyricsWithProvider.lyrics ?: "", fetchedLyricsWithProvider.providerName))
                    }
                } catch (e: Exception) {
                    // Ignore failures
                }
            }
        }
    }
    
    val lines = remember(currentLyrics) {
        val lyricsText = currentLyrics?.lyrics?.trim()
        if (lyricsText.isNullOrEmpty() || !lyricsText.startsWith("[")) return@remember emptyList()
        parseLyrics(lyricsText).filter { it.text.isNotBlank() }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = echo.music.iad1tya.constants.PlayerHorizontalPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentLyrics == null) {
                // Loading skeleton
                ShimmerHost(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextPlaceholder(
                        height = 20.dp,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(0.6f)
                    )
                }
            } else if (lines.isEmpty()) {
                // No synced lyrics found - just empty state
            } else {
                val effectivePosition = positionProvider()
                val currentLineIndex = remember(effectivePosition, lines) {
                    val index = lines.indexOfLast { it.time <= effectivePosition }
                    if (index >= 0) index else 0
                }
                val currentLineRaw = lines.getOrNull(currentLineIndex)?.text ?: ""
                
                // Phonetic transliteration (pronunciation)
                val pronunciationText = remember(currentLineRaw) {
                    transliterator.transliterateToPronunciation(currentLineRaw, LyricsLanguage.TAMIL)
                }

                Box(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = currentLineRaw to displayMode,
                        transitionSpec = {
                            (fadeIn() + slideInVertically { height -> height }).togetherWith(
                                fadeOut() + slideOutVertically { height -> -height }
                            ).using(SizeTransform(clip = false))
                        },
                        label = "SyncedLyrics"
                    ) { (lineText, mode) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Cycle display modes on tap
                                    val modes = LyricsDisplayMode.entries
                                    val nextIndex = (mode.ordinal + 1) % modes.size
                                    displayMode = modes[nextIndex]
                                }
                        ) {
                            when (mode) {
                                LyricsDisplayMode.ORIGINAL -> {
                                    Text(
                                        text = lineText,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Left
                                    )
                                }
                                LyricsDisplayMode.PRONUNCIATION -> {
                                    Text(
                                        text = pronunciationText,
                                        color = Color(0xFFFF0031),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Left
                                    )
                                }
                                LyricsDisplayMode.MEANING -> {
                                    Text(
                                        text = lineText,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Left
                                    )
                                }
                                LyricsDisplayMode.DUAL_LYRICS -> {
                                    Text(
                                        text = lineText,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Left
                                    )
                                    Text(
                                        text = pronunciationText,
                                        color = Color(0xFFFF0031),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Left,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                                LyricsDisplayMode.TRIPLE_LYRICS -> {
                                    Text(
                                        text = lineText,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Left
                                    )
                                    Text(
                                        text = pronunciationText,
                                        color = Color(0xFFFF0031),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Left,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                    Text(
                                        text = lineText,
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.Left,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Surface(
                    onClick = {
                        val modes = LyricsDisplayMode.entries
                        val nextIndex = (displayMode.ordinal + 1) % modes.size
                        displayMode = modes[nextIndex]
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = displayMode.label.take(4).uppercase(),
                        color = Color(0xFFFF0031),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = { navController?.navigate("notune/ask") },
                    modifier = Modifier.size(24.dp).padding(start = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.sparks),
                        contentDescription = "Explain Lyrics",
                        tint = Color(0xFFFF0031),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

