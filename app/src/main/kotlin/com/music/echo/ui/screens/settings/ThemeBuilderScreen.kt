
package echo.music.iad1tya.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.*
import echo.music.iad1tya.ui.component.*
import echo.music.iad1tya.ui.theme.NothingFont
import echo.music.iad1tya.utils.rememberPreference
import echo.music.iad1tya.utils.rememberEnumPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeBuilderScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val (themePreset, onThemePresetChange) = rememberEnumPreference(ThemePresetKey, ThemePreset.NOTHING)
    val (accentColorInt, onAccentColorChange) = rememberPreference(AccentColorKey, Color(0xFFFF0031).toArgb())
    val (typographyStyle, onTypographyStyleChange) = rememberEnumPreference(TypographyStyleKey, TypographyStyle.NOTHING_DOT_MATRIX)
    val (blurIntensity, onBlurIntensityChange) = rememberPreference(BlurIntensityKey, 12f)
    val (glassIntensity, onGlassIntensityChange) = rememberPreference(GlassIntensityKey, 0.05f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "THEME BUILDER",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = NothingFont,
                            letterSpacing = 2.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(painter = painterResource(R.drawable.arrow_back), contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
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
            PreferenceGroupTitle(title = "Presets")
            
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemePreset.entries.forEach { preset ->
                        FilterChip(
                            selected = themePreset == preset,
                            onClick = { onThemePresetChange(preset) },
                            label = { Text(preset.name.replace("_", " ")) },
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
            }

            PreferenceGroupTitle(title = "Core Customization")
            
            PreferenceEntry(
                title = { Text("Accent Color") },
                description = "Choose the main highlight color.",
                trailingContent = {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(accentColorInt))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    )
                },
                onClick = { /* Open Color Picker Dialog */ }
            )

            EnumListPreference(
                title = { Text("Typography") },
                selectedValue = typographyStyle,
                onValueSelected = onTypographyStyleChange,
                valueText = { it.name.replace("_", " ") },
                icon = { Icon(painter = painterResource(R.drawable.edit), contentDescription = null) }
            )

            PreferenceGroupTitle(title = "Glass & Blur")
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Blur Intensity: ${blurIntensity.toInt()}dp", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
                Slider(
                    value = blurIntensity,
                    onValueChange = onBlurIntensityChange,
                    valueRange = 0f..50f,
                    colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White)
                )
                
                Spacer(Modifier.height(16.dp))
                
                Text("Glass Opacity: ${(glassIntensity * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
                Slider(
                    value = glassIntensity,
                    onValueChange = onGlassIntensityChange,
                    valueRange = 0f..0.5f,
                    colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White)
                )
            }
            
            Spacer(Modifier.height(32.dp))
        }
    }
}
