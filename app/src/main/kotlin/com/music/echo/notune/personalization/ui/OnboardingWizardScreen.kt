package com.music.echo.notune.personalization.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.music.echo.notune.personalization.model.DiscoveryPreference
import com.music.echo.notune.personalization.model.SelectedArtist
import com.music.echo.notune.personalization.model.TasteProfile
import com.music.echo.notune.personalization.repository.TasteProfileRepository
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.theme.NothingFont
import kotlinx.coroutines.launch

@Composable
fun OnboardingWizardScreen(
    tasteProfileRepository: TasteProfileRepository,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 10

    var selectedMusicLanguages by remember { mutableStateOf(setOf("English", "Tamil")) }
    var selectedArtists by remember { mutableStateOf(listOf<SelectedArtist>()) }
    var selectedGenres by remember { mutableStateOf(setOf("Pop", "Electronic")) }
    var selectedEras by remember { mutableStateOf(setOf("2010s", "2020s")) }
    var selectedMoods by remember { mutableStateOf(setOf("Chill", "Energetic")) }
    var discoveryPref by remember { mutableStateOf(DiscoveryPreference.BALANCED) }
    var appLanguage by remember { mutableStateOf("en") }
    var visualStyle by remember { mutableStateOf("Minimal") }
    var historyEnabled by remember { mutableStateOf(true) }
    var aiPersonalizationEnabled by remember { mutableStateOf(true) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        currentStep++
    }

    Dialog(
        onDismissRequest = { /* Force onboarding completion or explicit skip */ },
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header Step Indicator
                if (currentStep in 1 until totalSteps - 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STEP $currentStep / ${totalSteps - 2}",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, color = Color(0xFFFF0031), letterSpacing = 1.5.sp)
                        )
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    tasteProfileRepository.skipOnboarding()
                                    onComplete()
                                }
                            }
                        ) {
                            Text("SKIP", style = MaterialTheme.typography.labelMedium.copy(color = Color.White.copy(alpha = 0.6f)))
                        }
                    }
                    LinearProgressIndicator(
                        progress = { currentStep.toFloat() / (totalSteps - 2) },
                        modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
                        color = Color(0xFFFF0031),
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                    Spacer(Modifier.height(16.dp))
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = currentStep,
                        transitionSpec = {
                            (fadeIn(tween(400)) + slideInHorizontally { it }).togetherWith(
                                fadeOut(tween(300)) + slideOutHorizontally { -it }
                            )
                        },
                        label = "OnboardingStep"
                    ) { step ->
                        when (step) {
                            0 -> WelcomeStep(onGetStarted = { currentStep++ }, onSkip = {
                                coroutineScope.launch {
                                    tasteProfileRepository.skipOnboarding()
                                    onComplete()
                                }
                            })
                            1 -> PermissionsStep(onGrant = {
                                val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    arrayOf(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }
                                permissionLauncher.launch(perms)
                            })
                            2 -> AppLanguageStep(selectedLang = appLanguage, onSelect = { appLanguage = it; currentStep++ })
                            3 -> VisualStyleStep(selectedStyle = visualStyle, onSelect = { visualStyle = it; currentStep++ })
                            4 -> MusicLanguagesStep(selectedLangs = selectedMusicLanguages, onToggle = { lang ->
                                selectedMusicLanguages = if (lang in selectedMusicLanguages) selectedMusicLanguages - lang else selectedMusicLanguages + lang
                            }, onNext = { currentStep++ })
                            5 -> FavoriteArtistsStep(selected = selectedArtists, onToggle = { artist ->
                                selectedArtists = if (selectedArtists.any { it.name == artist.name }) {
                                    selectedArtists.filterNot { it.name == artist.name }
                                } else {
                                    selectedArtists + artist
                                }
                            }, onNext = { currentStep++ })
                            6 -> FavoriteGenresStep(selected = selectedGenres, onToggle = { genre ->
                                selectedGenres = if (genre in selectedGenres) selectedGenres - genre else selectedGenres + genre
                            }, onNext = { currentStep++ })
                            7 -> ErasAndMoodsStep(
                                selectedEras = selectedEras,
                                selectedMoods = selectedMoods,
                                onToggleEra = { era -> selectedEras = if (era in selectedEras) selectedEras - era else selectedEras + era },
                                onToggleMood = { mood -> selectedMoods = if (mood in selectedMoods) selectedMoods - mood else selectedMoods + mood },
                                onNext = { currentStep++ }
                            )
                            8 -> ListeningStyleStep(selected = discoveryPref, onSelect = { discoveryPref = it }, onNext = { currentStep++ })
                            9 -> PrivacyAndFinishStep(
                                historyEnabled = historyEnabled,
                                aiEnabled = aiPersonalizationEnabled,
                                onHistoryToggle = { historyEnabled = it },
                                onAiToggle = { aiPersonalizationEnabled = it },
                                onFinish = {
                                    coroutineScope.launch {
                                        tasteProfileRepository.updateTasteProfile { p ->
                                            p.copy(
                                                appLanguage = appLanguage,
                                                musicLanguages = selectedMusicLanguages,
                                                favoriteArtists = selectedArtists,
                                                favoriteGenres = selectedGenres,
                                                preferredEras = selectedEras,
                                                preferredMoods = selectedMoods,
                                                discoveryPreference = discoveryPref,
                                                visualStyle = visualStyle,
                                                isOnboardingCompleted = true
                                            )
                                        }
                                        onComplete()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep(onGetStarted: () -> Unit, onSkip: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(80.dp).background(Color(0xFFFF0031), RoundedCornerShape(4.dp))
        ) {
            Text("NØ", style = MaterialTheme.typography.headlineLarge.copy(fontFamily = NothingFont, color = Color.White))
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "NØTUNE",
            style = MaterialTheme.typography.displaySmall.copy(fontFamily = NothingFont, letterSpacing = 4.sp, color = Color.White)
        )
        Text(
            text = "MUSIC. INTELLIGENCE. YOUR WAY.",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, color = Color(0xFFFF0031), letterSpacing = 2.sp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Welcome to your local-first, privacy-aware music ecosystem. Let's calibrate your Taste Profile.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = onGetStarted,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(2.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0031), contentColor = Color.White)
        ) {
            Text("INITIALIZE TASTE PROFILE", style = MaterialTheme.typography.labelLarge.copy(fontFamily = NothingFont))
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = onSkip) {
            Text("CONTINUE WITH DEFAULT SETTINGS", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.5f)))
        }
    }
}

@Composable
private fun PermissionsStep(onGrant: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Icon(painterResource(R.drawable.library_music), contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(24.dp))
        Text("LOCAL STORAGE & NOTIFICATIONS", style = MaterialTheme.typography.titleLarge.copy(fontFamily = NothingFont, color = Color.White))
        Spacer(Modifier.height(12.dp))
        Text(
            "NØTUNE requires access to scan your local audio files and manage media playback notifications.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = onGrant,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(2.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
        ) {
            Text("GRANT PERMISSIONS", style = MaterialTheme.typography.labelLarge.copy(fontFamily = NothingFont))
        }
    }
}

@Composable
private fun AppLanguageStep(selectedLang: String, onSelect: (String) -> Unit) {
    val languages = listOf("English" to "en", "Tamil (தமிழ்)" to "ta", "Hindi (हिन्दी)" to "hi", "Telugu (తెలుగు)" to "te", "Malayalam (മലയാളം)" to "ml", "Spanish (Español)" to "es")
    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Text("CHOOSE APP LANGUAGE", style = MaterialTheme.typography.titleLarge.copy(fontFamily = NothingFont, color = Color.White))
        Spacer(Modifier.height(16.dp))
        languages.forEach { (name, code) ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onSelect(code) },
                colors = CardDefaults.cardColors(containerColor = if (selectedLang == code) Color(0xFFFF0031).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f)),
                border = if (selectedLang == code) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF0031)) else null
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(name, style = MaterialTheme.typography.bodyLarge.copy(color = Color.White), modifier = Modifier.weight(1f))
                    if (selectedLang == code) {
                        Icon(painterResource(R.drawable.check), contentDescription = null, tint = Color(0xFFFF0031))
                    }
                }
            }
        }
    }
}

@Composable
private fun VisualStyleStep(selectedStyle: String, onSelect: (String) -> Unit) {
    val styles = listOf("Minimal" to "Clean, high-contrast dark theme with sharp typography", "Glassmorphic" to "Translucent glass panels and dynamic blurred backgrounds", "Vibrant" to "Expressive dynamic color accents based on current playback")
    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Text("VISUAL DESIGN STYLE", style = MaterialTheme.typography.titleLarge.copy(fontFamily = NothingFont, color = Color.White))
        Spacer(Modifier.height(16.dp))
        styles.forEach { (title, desc) ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onSelect(title) },
                colors = CardDefaults.cardColors(containerColor = if (selectedStyle == title) Color(0xFFFF0031).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f)),
                border = if (selectedStyle == title) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF0031)) else null
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium.copy(fontFamily = NothingFont, color = Color.White))
                    Spacer(Modifier.height(4.dp))
                    Text(desc, style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.6f)))
                }
            }
        }
    }
}

@Composable
private fun MusicLanguagesStep(selectedLangs: Set<String>, onToggle: (String) -> Unit, onNext: () -> Unit) {
    val musicLangs = listOf("English", "Tamil", "Hindi", "Telugu", "Malayalam", "Kannada", "Korean", "Japanese", "Spanish", "Punjabi", "Other")
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("MUSIC LANGUAGES YOU LISTEN TO", style = MaterialTheme.typography.titleLarge.copy(fontFamily = NothingFont, color = Color.White))
        Text("Select all that apply to calibrate FLOW", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.6f)))
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(musicLangs) { lang ->
                val isSelected = lang in selectedLangs
                FilterChip(
                    selected = isSelected,
                    onClick = { onToggle(lang) },
                    label = { Text(lang) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFF0031),
                        selectedLabelColor = Color.White,
                        containerColor = Color.White.copy(alpha = 0.08f),
                        labelColor = Color.White
                    )
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(2.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)) {
            Text("CONTINUE (${selectedLangs.size} SELECTED)", style = MaterialTheme.typography.labelLarge.copy(fontFamily = NothingFont))
        }
    }
}

@Composable
private fun FavoriteArtistsStep(selected: List<SelectedArtist>, onToggle: (SelectedArtist) -> Unit, onNext: () -> Unit) {
    val popularArtists = listOf(
        SelectedArtist("1", "A.R. Rahman"), SelectedArtist("2", "Anirudh Ravichander"),
        SelectedArtist("3", "Taylor Swift"), SelectedArtist("4", "The Weeknd"),
        SelectedArtist("5", "Arijit Singh"), SelectedArtist("6", "Sid Sriram"),
        SelectedArtist("7", "BTS"), SelectedArtist("8", "Drake"),
        SelectedArtist("9", "Kendrick Lamar"), SelectedArtist("10", "Yuvan Shankar Raja")
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("FAVORITE ARTISTS", style = MaterialTheme.typography.titleLarge.copy(fontFamily = NothingFont, color = Color.White))
        Text("Select artists you love to seed your recommendations", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.6f)))
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(popularArtists) { artist ->
                val isSelected = selected.any { it.name == artist.name }
                Card(
                    modifier = Modifier.clickable { onToggle(artist) },
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFFF0031).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f)),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF0031)) else null
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Text(artist.name.take(1), style = MaterialTheme.typography.titleSmall.copy(color = Color.White))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(artist.name, style = MaterialTheme.typography.bodyMedium.copy(color = Color.White), maxLines = 1, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(2.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)) {
            Text("CONTINUE (${selected.size} ARTISTS)", style = MaterialTheme.typography.labelLarge.copy(fontFamily = NothingFont))
        }
    }
}

@Composable
private fun FavoriteGenresStep(selected: Set<String>, onToggle: (String) -> Unit, onNext: () -> Unit) {
    val genres = listOf("Pop", "Rock", "Hip-Hop / Rap", "Tamil Pop", "Bollywood", "Electronic / EDM", "R&B / Soul", "K-Pop", "Indie / Alternative", "Ambient / Chill", "Classical", "Jazz", "Metal", "Folk")
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("GENRE PREFERENCES", style = MaterialTheme.typography.titleLarge.copy(fontFamily = NothingFont, color = Color.White))
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(genres) { genre ->
                val isSelected = genre in selected
                FilterChip(
                    selected = isSelected,
                    onClick = { onToggle(genre) },
                    label = { Text(genre) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFF0031),
                        selectedLabelColor = Color.White,
                        containerColor = Color.White.copy(alpha = 0.08f),
                        labelColor = Color.White
                    )
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(2.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)) {
            Text("CONTINUE", style = MaterialTheme.typography.labelLarge.copy(fontFamily = NothingFont))
        }
    }
}

@Composable
private fun ErasAndMoodsStep(
    selectedEras: Set<String>,
    selectedMoods: Set<String>,
    onToggleEra: (String) -> Unit,
    onToggleMood: (String) -> Unit,
    onNext: () -> Unit
) {
    val eras = listOf("70s", "80s", "90s", "2000s", "2010s", "2020s")
    val moods = listOf("Chill", "Happy", "Energetic", "Romantic", "Melancholic", "Focus", "Party")
    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Text("ERAS & MOODS", style = MaterialTheme.typography.titleLarge.copy(fontFamily = NothingFont, color = Color.White))
        Spacer(Modifier.height(16.dp))
        Text("Preferred Eras", style = MaterialTheme.typography.titleSmall.copy(color = Color.White.copy(alpha = 0.8f)))
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            eras.forEach { era ->
                FilterChip(
                    selected = era in selectedEras,
                    onClick = { onToggleEra(era) },
                    label = { Text(era) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFF0031), selectedLabelColor = Color.White)
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("Preferred Moods", style = MaterialTheme.typography.titleSmall.copy(color = Color.White.copy(alpha = 0.8f)))
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            moods.forEach { mood ->
                FilterChip(
                    selected = mood in selectedMoods,
                    onClick = { onToggleMood(mood) },
                    label = { Text(mood) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFF0031), selectedLabelColor = Color.White)
                )
            }
        }
        Spacer(Modifier.height(32.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(2.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)) {
            Text("CONTINUE", style = MaterialTheme.typography.labelLarge.copy(fontFamily = NothingFont))
        }
    }
}

@Composable
private fun ListeningStyleStep(selected: DiscoveryPreference, onSelect: (DiscoveryPreference) -> Unit, onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Text("DISCOVERY PREFERENCE", style = MaterialTheme.typography.titleLarge.copy(fontFamily = NothingFont, color = Color.White))
        Text("How would you like NØTUNE FLOW to recommend music?", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.6f)))
        Spacer(Modifier.height(20.dp))
        DiscoveryPreference.entries.forEach { pref ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onSelect(pref) },
                colors = CardDefaults.cardColors(containerColor = if (selected == pref) Color(0xFFFF0031).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f)),
                border = if (selected == pref) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF0031)) else null
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(pref.label, style = MaterialTheme.typography.titleMedium.copy(fontFamily = NothingFont, color = Color.White))
                        Text("Familiar track ratio: ${(pref.familiarRatio * 100).toInt()}%", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.5f)))
                    }
                    RadioButton(selected = selected == pref, onClick = { onSelect(pref) }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF0031)))
                }
            }
        }
        Spacer(Modifier.height(32.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(2.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)) {
            Text("CONTINUE", style = MaterialTheme.typography.labelLarge.copy(fontFamily = NothingFont))
        }
    }
}

@Composable
private fun PrivacyAndFinishStep(
    historyEnabled: Boolean,
    aiEnabled: Boolean,
    onHistoryToggle: (Boolean) -> Unit,
    onAiToggle: (Boolean) -> Unit,
    onFinish: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Text("PRIVACY & COMPLETE", style = MaterialTheme.typography.titleLarge.copy(fontFamily = NothingFont, color = Color.White))
        Spacer(Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Record Local Listening History", style = MaterialTheme.typography.bodyLarge.copy(color = Color.White), modifier = Modifier.weight(1f))
                    Switch(checked = historyEnabled, onCheckedChange = onHistoryToggle)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("AI-Powered Personalization", style = MaterialTheme.typography.bodyLarge.copy(color = Color.White), modifier = Modifier.weight(1f))
                    Switch(checked = aiEnabled, onCheckedChange = onAiToggle)
                }
            }
        }
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(2.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0031), contentColor = Color.White)
        ) {
            Text("FINISH & START FLOW", style = MaterialTheme.typography.labelLarge.copy(fontFamily = NothingFont))
        }
    }
}
