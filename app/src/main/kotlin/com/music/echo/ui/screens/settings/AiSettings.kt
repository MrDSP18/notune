
package echo.music.iad1tya.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.*
import echo.music.iad1tya.ui.component.*
import echo.music.iad1tya.ui.theme.NothingFont
import echo.music.iad1tya.utils.rememberPreference
import echo.music.iad1tya.utils.rememberEnumPreference
import echo.music.iad1tya.notune.ai.AiProviderType

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import echo.music.iad1tya.notune.ai.AiEngine
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    highlightKey: String? = null
) {
    val (aiDjEnabled, onAiDjEnabledChange) = rememberPreference(AiDjEnabledKey, true)
    val (aiMusicFinderEnabled, onAiMusicFinderEnabledChange) = rememberPreference(AiMusicFinderEnabledKey, true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI SETTINGS", style = MaterialTheme.typography.titleLarge.copy(fontFamily = NothingFont, letterSpacing = 2.sp)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(painter = painterResource(R.drawable.arrow_back), contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Free Built-in AI Banner
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFF0031).copy(alpha = 0.05f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF0031).copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Icon(painter = painterResource(R.drawable.sparks), contentDescription = null, tint = Color(0xFFFF0031), modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("NØTUNE NEURAL CORE ACTIVE", style = MaterialTheme.typography.titleMedium.copy(fontFamily = NothingFont, color = Color.White))
                        Spacer(Modifier.height(4.dp))
                        Text("All AI features are managed autonomously by the NØTUNE Cloud Network. Zero configuration required for the ultimate musical intelligence.", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.6f)))
                    }
                }
            }

            PreferenceGroupTitle(title = "Core Intelligence")

            SwitchPreference(
                title = { Text("Autonomous AI DJ") },
                description = "NØTUNE will intelligently analyze and introduce songs in real-time.",
                checked = aiDjEnabled,
                onCheckedChange = onAiDjEnabledChange
            )

            SwitchPreference(
                title = { Text("Neural Music Finder") },
                description = "Enable natural language semantic search across the entire library.",
                checked = aiMusicFinderEnabled,
                onCheckedChange = onAiMusicFinderEnabledChange
            )

            PreferenceGroupTitle(title = "NØTUNE Neural Suite (52 AI Features Active)")

            val featuresList = listOf(
                "AI DJ Commentary Engine", "AI FLOW Radio Curator", "AI Smart Playlist Synthesizer", "AI Song Recommendation Radar",
                "AI Natural Language Search", "AI Lyric Multi-Lang Translator", "AI Sing-Along Phonetic Guide", "AI Song Meaning & Story Explainer",
                "AI Playlist Doctor & Auto-Fixer", "AI Audio EQ Presets", "AI Mood-Adaptive Theme Selector", "AI Dynamic Logo Variant Selector",
                "AI Cover Art Palette Extractor", "AI Canvas Visualizer Presets", "AI Dynamic Typography Engine", "AI Lock Screen Customizer",
                "AI Notification Aesthetic Engine", "AI Home Screen Widget Curator", "Ask NØTUNE Conversational Agent", "AI Voice Command Interpreter",
                "AI Natural Language Queue Manager", "AI Instant Genre Blender", "AI Decade Time-Traveler", "AI Sleep Timer Predictor",
                "AI Audio Focus Manager", "AI Music DNA Taste Analyzer", "AI Daily Music Horoscope & Vibe Check", "AI Listening Habit Predictor",
                "AI Artist Journey Deep Dive", "AI Era & Origin Mapper", "AI Album Transition Analyzer", "AI Acoustic Energy Meter",
                "AI Similarity Vector Radar", "AI Listen Together Room Moderator", "AI Couple Mode Vibe Matcher", "AI Synchronized Room DJ",
                "AI Shared Queue Conflict Resolver", "AI Room Chat Music Assistant", "AI Lyric Sentiment Matcher", "AI Offline Cloud Sync Predictor",
                "AI Network Quality Saver", "AI Smart Storage Cleaner", "AI Privacy Request Sanitizer", "AI Incognito Session Guard",
                "AI Weather-to-Music Synthesizer", "AI Time-of-Day Tempo Matcher", "AI Workout Pace Matcher", "AI Duplicate Song Cleaner",
                "AI Explicit Content Filter", "AI Interactive Music Trivia", "AI Smart Backup Engine", "AI Playground Prompt Lab"
            )

            featuresList.forEachIndexed { index, featureName ->
                PreferenceEntry(
                    title = { Text("#${index + 1} $featureName") },
                    description = "100% Free • Zero-Config • Active",
                    icon = { Icon(painter = painterResource(R.drawable.sparks), contentDescription = null, tint = Color(0xFFFF0031)) }
                )
            }

            PreferenceGroupTitle(title = "NØTUNE Lab")
            
            PreferenceEntry(
                title = { Text("AI Playground (52 Tools)") },
                description = "Test models, tool calls, and prompts directly.",
                icon = { Icon(painter = painterResource(R.drawable.edit), contentDescription = null) },
                onClick = { navController.navigate("notune/lab/playground") }
            )
        }
    }
}
