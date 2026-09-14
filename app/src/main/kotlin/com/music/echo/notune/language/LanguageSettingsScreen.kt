package com.music.echo.notune.language

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.echo.notune.theme.NoTuneAmbientCanvas
import com.music.echo.notune.theme.NoTuneSurfaceCard
import echo.music.iad1tya.constants.AppLanguageKey
import echo.music.iad1tya.constants.CardStyleVariant
import echo.music.iad1tya.utils.rememberPreference

@Composable
fun LanguageSettingsScreen(
    onBackClick: () -> Unit = {}
) {
    var appLanguageCode by rememberPreference(AppLanguageKey, "en")
    var searchQuery by remember { mutableStateOf("") }

    val filteredLanguages = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            NoTuneLanguageEngine.SUPPORTED_LANGUAGES
        } else {
            NoTuneLanguageEngine.SUPPORTED_LANGUAGES.filter {
                it.nameInEnglish.contains(searchQuery, ignoreCase = true) ||
                it.nativeName.contains(searchQuery, ignoreCase = true) ||
                it.code.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NoTuneAmbientCanvas(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Text("←", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "UNIVERSAL LANGUAGE HUB",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "App & AI Multilingual Settings",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                    )
                }
            }

            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search 50+ languages (e.g., தமிழ், Hindi, Español)...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Language Card
            val activeLang = NoTuneLanguageEngine.getLanguageByCode(appLanguageCode)
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                NoTuneSurfaceCard(
                    cardStyle = CardStyleVariant.GLASS,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(activeLang.flagEmoji, fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ACTIVE APP LANGUAGE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text("${activeLang.nativeName} (${activeLang.nameInEnglish})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                        }
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
                            Text("✓ Active", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Language Selection List
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredLanguages) { lang ->
                    val isSelected = appLanguageCode.equals(lang.code, ignoreCase = true)
                    NoTuneSurfaceCard(
                        cardStyle = if (isSelected) CardStyleVariant.GLASS else CardStyleVariant.CLEAN,
                        onClick = {
                            appLanguageCode = lang.code
                            NoTuneLanguageEngine.applyLanguage(lang.code)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(lang.flagEmoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lang.nativeName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${lang.nameInEnglish} • Code: [${lang.code}]",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Text("✓ Selected", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}
