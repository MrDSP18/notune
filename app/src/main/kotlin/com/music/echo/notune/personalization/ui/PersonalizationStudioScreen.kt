package com.music.echo.notune.personalization.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.music.echo.notune.personalization.viewmodel.PersonalizationStudioViewModel
import com.music.echo.notune.theme.*
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.*

@Composable
fun PersonalizationStudioScreen(
    navController: NavController,
    viewModel: PersonalizationStudioViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var selectedCategory by remember { mutableStateOf("Themes") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Dynamic Ambient Background Visualizer Canvas
        NoTuneAmbientCanvas(
            modifier = Modifier.fillMaxSize(),
            isPlaying = true
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Studio Top Navigation Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                echo.music.iad1tya.ui.component.ResizableIconButton(
                    onClick = { navController.popBackStack() },
                    icon = R.drawable.chevron_leftpx
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ADAPTIVE DESIGN STUDIO",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "NØTUNE Customization Engine",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                    )
                }
                TextButton(onClick = { viewModel.resetAllCustomizations() }) {
                    Text("Reset", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }

            // Live Preview Header Card
            AdaptivePlayerPreview(
                playerStyle = state.playerStyle,
                miniPlayerStyle = state.miniPlayerStyle,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Selector Chips
            val categories = listOf("Themes", "Typography", "Logo & Brand", "Player Stage", "Card Style", "Context Mode")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Customization Studio Controls Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                when (selectedCategory) {
                    "Themes" -> {
                        Text(
                            "APPEARANCE MODE",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AppearanceMode.entries.forEach { mode ->
                                OutlinedButton(
                                    onClick = { viewModel.updateAppearanceMode(mode) },
                                    modifier = Modifier.weight(1f),
                                    colors = if (state.appearanceMode == mode) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors()
                                ) {
                                    Text(mode.label, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            "50 PREMIUM THEME PALETTES",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        NoTuneThemeEngine.ALL_THEMES.forEach { palette ->
                            val isSelected = state.themePresetId.equals(palette.id, ignoreCase = true)
                            NoTuneSurfaceCard(
                                cardStyle = if (isSelected) CardStyleVariant.GLASS else CardStyleVariant.CLEAN,
                                onClick = { viewModel.updateThemePreset(palette.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.sweepGradient(
                                                    listOf(palette.primaryAccent, palette.secondaryAccent, palette.tertiaryAccent, palette.primaryAccent)
                                                )
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(palette.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text(palette.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    if (isSelected) {
                                        Text("✓ Active", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }
                    }

                    "Typography" -> {
                        Text(
                            "TYPOGRAPHY & FONT STYLES",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        FontFamilyStyle.entries.forEach { style ->
                            val isSelected = state.fontStyle == style
                            NoTuneSurfaceCard(
                                cardStyle = if (isSelected) CardStyleVariant.GLASS else CardStyleVariant.CLEAN,
                                onClick = { viewModel.updateFontStyle(style) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = style.displayName,
                                        fontFamily = NoTuneTypographySystem.resolveFontFamily(style),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isSelected) {
                                        Text("✓ Selected", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    "Logo & Brand" -> {
                        Text(
                            "30 NØTUNE LOGO VARIANTS",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        LogoVariant.entries.forEach { variant ->
                            val isSelected = state.logoVariant == variant
                            NoTuneSurfaceCard(
                                cardStyle = if (isSelected) CardStyleVariant.GLASS else CardStyleVariant.CLEAN,
                                onClick = { viewModel.updateLogoVariant(variant) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    NoTuneLogo(variant = variant, size = 36.dp)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = variant.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isSelected) {
                                        Text("✓ Active", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    "Player Stage" -> {
                        Text(
                            "PLAYER STAGE STYLE",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        PlayerStyleVariant.entries.forEach { style ->
                            val isSelected = state.playerStyle == style
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updatePlayerStyle(style) },
                                label = { Text(style.title, fontWeight = FontWeight.Bold) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            "MINI-PLAYER CAPSULE STYLE",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        MiniPlayerStyleVariant.entries.forEach { miniStyle ->
                            val isSelected = state.miniPlayerStyle == miniStyle
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateMiniPlayerStyle(miniStyle) },
                                label = { Text(miniStyle.title, fontWeight = FontWeight.Bold) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            )
                        }
                    }

                    "Card Style" -> {
                        Text(
                            "COMPONENT CARD VARIANTS",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        CardStyleVariant.entries.forEach { cardVariant ->
                            NoTuneSurfaceCard(
                                cardStyle = cardVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Column {
                                    Text(cardVariant.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("Preview container with ${cardVariant.title.lowercase()} styling", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    "Context Mode" -> {
                        Text(
                            "ADAPTIVE UI CONTEXT",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        UiContext.entries.forEach { ctx ->
                            val isSelected = state.uiContext == ctx
                            NoTuneSurfaceCard(
                                cardStyle = if (isSelected) CardStyleVariant.GLASS else CardStyleVariant.CLEAN,
                                onClick = { viewModel.updateUiContext(ctx) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(ctx.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text(ctx.iconDescription, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    if (isSelected) {
                                        Text("✓ Active", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
