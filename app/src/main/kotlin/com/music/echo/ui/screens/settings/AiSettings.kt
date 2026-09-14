
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
    viewModel: echo.music.iad1tya.notune.ai.AiPlaygroundViewModel = hiltViewModel(), // Reusing playground VM for engine access
    highlightKey: String? = null
) {
    val aiEngine = viewModel.aiEngine
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val (preferredProvider, onPreferredProviderChange) = rememberEnumPreference(PreferredAiProviderKey, AiProviderType.NOTUNE_BASIC)
    val (geminiApiKey, onGeminiApiKeyChange) = rememberPreference(GeminiApiKey, "")
    val (groqApiKey, onGroqApiKeyChange) = rememberPreference(GroqApiKey, "")
    val (openRouterApiKey, onOpenRouterApiKeyChange) = rememberPreference(OpenRouterApiKeyExtra, "")
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
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFF0031).copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF0031).copy(alpha = 0.4f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Icon(painter = painterResource(R.drawable.sparks), contentDescription = null, tint = Color(0xFFFF0031), modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("NØTUNE FREE AI IS ACTIVE", style = MaterialTheme.typography.titleSmall.copy(fontFamily = NothingFont, color = Color.White))
                        Spacer(Modifier.height(2.dp))
                        Text("AI features (AI DJ, Smart Search, Moods, Playlists) are 100% free and work out-of-the-box for all users without setup.", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f)))
                    }
                }
            }

            PreferenceGroupTitle(title = "Provider Configuration")

            EnumListPreference(
                title = { Text("Preferred Provider") },
                selectedValue = preferredProvider,
                onValueSelected = onPreferredProviderChange,
                valueText = { 
                    when(it) {
                        AiProviderType.NOTUNE_BASIC -> "NØTUNE Free Built-in AI (Default - Always Active)"
                        AiProviderType.GEMINI -> "Google Gemini (Optional Key)"
                        AiProviderType.GROQ -> "Groq (Optional Key)"
                        AiProviderType.OPENROUTER -> "OpenRouter (Optional Key)"
                        AiProviderType.OLLAMA -> "Ollama (Local Server)"
                        else -> it.name
                    }
                },
                icon = { Icon(painter = painterResource(R.drawable.sparks), contentDescription = null) }
            )

            EditTextPreference(
                title = { Text("Google Gemini API Key (Optional)") },
                description = if (geminiApiKey.isBlank()) "Free Built-in AI active (Optional Key)" else "Connected Custom Key",
                value = geminiApiKey,
                onValueChange = onGeminiApiKeyChange,
                trailingContent = {
                    TextButton(onClick = {
                        scope.launch {
                            val res = aiEngine.testConnection(AiProviderType.GEMINI)
                            val msg = if (res.isSuccess) "Connection Successful!" else "Error: ${res.exceptionOrNull()?.message}"
                            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }) { Text("TEST", color = Color(0xFFFF0031)) }
                }
            )

            EditTextPreference(
                title = { Text("Groq API Key") },
                description = if (groqApiKey.isBlank()) "Not configured" else "Connected",
                value = groqApiKey,
                onValueChange = onGroqApiKeyChange,
                trailingContent = {
                    TextButton(onClick = {
                        scope.launch {
                            val res = aiEngine.testConnection(AiProviderType.GROQ)
                            val msg = if (res.isSuccess) "Connection Successful!" else "Error: ${res.exceptionOrNull()?.message}"
                            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }) { Text("TEST", color = Color(0xFFFF0031)) }
                }
            )

            EditTextPreference(
                title = { Text("OpenRouter API Key") },
                description = if (openRouterApiKey.isBlank()) "Not configured" else "Connected",
                value = openRouterApiKey,
                onValueChange = onOpenRouterApiKeyChange
            )

            PreferenceGroupTitle(title = "AI Features")

            SwitchPreference(
                title = { Text("AI DJ Commentary") },
                description = "NØTUNE will intelligently introduce songs.",
                checked = aiDjEnabled,
                onCheckedChange = onAiDjEnabledChange
            )

            SwitchPreference(
                title = { Text("AI Music Finder") },
                description = "Enable natural language search in the search bar.",
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
