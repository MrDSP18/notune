package com.music.echo.notune.personalization.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.music.echo.notune.personalization.model.DiscoveryPreference
import com.music.echo.notune.personalization.model.TasteProfile
import com.music.echo.notune.personalization.repository.TasteProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.component.DefaultDialog
import echo.music.iad1tya.ui.component.IconButton
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalizationViewModel @Inject constructor(
    private val tasteProfileRepository: TasteProfileRepository
) : ViewModel() {
    val tasteProfile = tasteProfileRepository.tasteProfile.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        TasteProfile()
    )

    fun updateProfile(transform: (TasteProfile) -> TasteProfile) {
        viewModelScope.launch {
            tasteProfileRepository.updateTasteProfile(transform)
        }
    }

    fun resetProfile() {
        viewModelScope.launch {
            tasteProfileRepository.resetTasteProfile()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalizationSettingsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: PersonalizationViewModel = hiltViewModel()
) {
    val profile by viewModel.tasteProfile.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        DefaultDialog(
            onDismiss = { showResetDialog = false },
            content = {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Reset Taste Profile?", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("This will reset your selected artists, genres, language preferences, and discovery ratios. Listening history will remain intact.")
                }
            },
            buttons = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
                Button(
                    onClick = {
                        showResetDialog = false
                        viewModel.resetProfile()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Reset", color = Color.White) }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personalization & Taste Profile", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(painterResource(R.drawable.arrow_back), contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // View Full Taste Profile Card
            Card(
                onClick = { navController.navigate("taste_profile") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("View Your Music DNA Dashboard", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Inspect language affinities, genre breakdowns, and recommendation signals", style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(painterResource(R.drawable.chevron_right_px), contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("RECOMMENDATION PREFERENCES", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp), color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(10.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Discovery Style", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    DiscoveryPreference.entries.forEach { pref ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            RadioButton(
                                selected = profile.discoveryPreference == pref,
                                onClick = { viewModel.updateProfile { it.copy(discoveryPreference = pref) } }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(pref.label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Avoid Repetition", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("Dampen recently played tracks in FLOW", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = profile.avoidRepetition,
                            onCheckedChange = { value -> viewModel.updateProfile { it.copy(avoidRepetition = value) } }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("TASTE PROFILE DATA", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp), color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(10.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Music Languages (${profile.musicLanguages.size})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text(profile.musicLanguages.joinToString().ifEmpty { "None selected" }, style = MaterialTheme.typography.bodySmall)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    Text("Favorite Artists (${profile.favoriteArtists.size})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text(profile.favoriteArtists.joinToString { it.name }.ifEmpty { "None selected" }, style = MaterialTheme.typography.bodySmall)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    Text("Genres (${profile.favoriteGenres.size})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text(profile.favoriteGenres.joinToString().ifEmpty { "None selected" }, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { showResetDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Reset Taste Profile", color = Color.White)
            }
        }
    }
}
