
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

            PreferenceGroupTitle(title = "NØTUNE Lab")
            
            PreferenceEntry(
                title = { Text("AI Playground") },
                description = "Test models and prompts directly.",
                icon = { Icon(painter = painterResource(R.drawable.edit), contentDescription = null) },
                onClick = { navController.navigate("notune/lab/playground") }
            )
        }
    }
}
