
package echo.music.iad1tya.notune.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.ui.theme.NothingFont
import kotlinx.coroutines.launch
import javax.inject.Inject

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiPlaygroundScreen(
    navController: NavController,
    viewModel: AiPlaygroundViewModel = hiltViewModel()
) {
    val aiEngine = viewModel.aiEngine
    var prompt by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("") }
    var selectedProvider by remember { mutableStateOf<AiProvider?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var providers by remember { mutableStateOf<List<AiProvider>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        providers = aiEngine.getProviders()
        selectedProvider = providers.firstOrNull()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI PLAYGROUND", style = MaterialTheme.typography.titleLarge.copy(fontFamily = NothingFont, letterSpacing = 2.sp)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(painter = painterResource(R.drawable.arrow_back), contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Select Provider", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                providers.forEach { provider ->
                    FilterChip(
                        selected = selectedProvider == provider,
                        onClick = { selectedProvider = provider },
                        label = { Text(provider.type.name) },
                        shape = RoundedCornerShape(4.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color.White,
                            selectedLabelColor = Color.Black,
                            containerColor = Color.White.copy(alpha = 0.05f),
                            labelColor = Color.White
                        )
                    )
                }
            }

            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Enter Prompt", color = Color.White.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                shape = RoundedCornerShape(4.dp)
            )

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        val result = aiEngine.generateResponse(prompt, preferredProvider = selectedProvider?.type)
                        responseText = result.fold(
                            onSuccess = { "Model: ${it.modelId}\n\n${it.text}" },
                            onFailure = { "Error: ${it.message}" }
                        )
                        isLoading = false
                    }
                },
                modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(2.dp),
                enabled = !isLoading && prompt.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                else Text("EXECUTE")
            }

            if (responseText.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(4.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .padding(16.dp)
                ) {
                    Text(responseText, color = Color.White, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
