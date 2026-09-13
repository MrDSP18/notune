package com.music.echo.notune.personalization.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.music.echo.notune.personalization.model.TasteProfile
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.ui.theme.NothingFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasteProfileScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: PersonalizationViewModel = hiltViewModel()
) {
    val profile by viewModel.tasteProfile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("YOUR TASTE", style = MaterialTheme.typography.titleLarge.copy(fontFamily = NothingFont, letterSpacing = 2.sp)) },
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
            // Header Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFF0031).copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("NØTUNE PERSONAL INTELLIGENCE", style = MaterialTheme.typography.labelSmall.copy(fontFamily = NothingFont, color = Color(0xFFFF0031), letterSpacing = 1.5.sp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("TASTE PROFILE & SIGNALS", style = MaterialTheme.typography.headlineSmall.copy(fontFamily = NothingFont))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Your preferences seed NØTUNE FLOW and evolve dynamically as you listen.", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Languages Section
            Text("MUSIC LANGUAGES", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp), color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(10.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (profile.musicLanguages.isEmpty()) {
                        Text("No languages selected", style = MaterialTheme.typography.bodySmall)
                    } else {
                        profile.musicLanguages.forEach { lang ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(lang, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                    Text("Active", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFF0031)))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { 0.85f },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = Color(0xFFFF0031),
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Genres Section
            Text("GENRES & STYLES", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp), color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(10.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (profile.favoriteGenres.isEmpty()) {
                        Text("No genres selected", style = MaterialTheme.typography.bodySmall)
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            profile.favoriteGenres.forEach { genre ->
                                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFF0031).copy(alpha = 0.15f)) {
                                    Text(genre, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium.copy(color = Color.White))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Favorite Artists Section
            Text("FAVORITE ARTISTS", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp), color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(10.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (profile.favoriteArtists.isEmpty()) {
                        Text("No favorite artists selected", style = MaterialTheme.typography.bodySmall)
                    } else {
                        profile.favoriteArtists.forEach { artist ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFFF0031)), contentAlignment = Alignment.Center) {
                                    Text(artist.name.take(1), style = MaterialTheme.typography.titleSmall.copy(color = Color.White))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(artist.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Recommendation Explainability Card ("Why Am I Seeing This?")
            Text("RECOMMENDATION EXPLAINABILITY", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp), color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(10.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.sparks), contentDescription = null, tint = Color(0xFFFF0031), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Why am I seeing recommendations?", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Recommendations are scored using your ${profile.discoveryPreference.label.lowercase()} discovery style (${(profile.discoveryPreference.familiarRatio * 100).toInt()}% familiar ratio), selected music languages (${profile.musicLanguages.joinToString()}), and favorite artists.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
