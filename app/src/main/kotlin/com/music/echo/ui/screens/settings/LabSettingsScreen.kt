
package echo.music.iad1tya.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabSettingsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val (experimentalAi, onExperimentalAiChange) = rememberPreference(ExperimentalAiFeaturesKey, false)
    val (experimentalVisualizers, onExperimentalVisualizersChange) = rememberPreference(ExperimentalVisualizersKey, false)
    val (experimentalAnimations, onExperimentalAnimationsChange) = rememberPreference(ExperimentalAnimationsKey, false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "NØTUNE LAB",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = NothingFont,
                            letterSpacing = 2.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = null
                        )
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
            PreferenceGroupTitle(title = "AI Engine")
            
            SwitchPreference(
                title = { Text("Experimental AI Features") },
                description = "Enable advanced natural language processing and AI DJ.",
                checked = experimentalAi,
                onCheckedChange = onExperimentalAiChange
            )

            PreferenceGroupTitle(title = "Visuals & Performance")

            SwitchPreference(
                title = { Text("Next-Gen Visualizers") },
                description = "Enable high-fidelity real-time audio spectrums.",
                checked = experimentalVisualizers,
                onCheckedChange = onExperimentalVisualizersChange
            )

            SwitchPreference(
                title = { Text("Ultra-Smooth Animations") },
                description = "Enable 120Hz optimized fluid transitions.",
                checked = experimentalAnimations,
                onCheckedChange = onExperimentalAnimationsChange
            )
            
            PreferenceGroupTitle(title = "Experimental Features")
            
            PreferenceEntry(
                title = { Text("NØTUNE Neural Map") },
                description = "Visualization of your personal music taste and history.",
                onClick = { navController.navigate("notune/neural_map") }
            )

            PreferenceEntry(
                title = { Text("NØTUNE Replay & Time Machine") },
                description = "4-tab listening statistics, forgotten gems, and memories.",
                onClick = { navController.navigate("notune/replay") }
            )

            PreferenceEntry(
                title = { Text("Listening Achievements & Fandom Badges") },
                description = "Track listening level, active streak, and unlocked badges.",
                onClick = { navController.navigate("notune/achievements") }
            )

            PreferenceEntry(
                title = { Text("Mood Journey Engine") },
                description = "Dynamic emotional audio transition architecture.",
                onClick = { navController.navigate("notune/mood_journey") }
            )

            PreferenceEntry(
                title = { Text("Global Neural Search & Hum-To-Search") },
                description = "AI voice, lyrics, and melody recognition search hub.",
                onClick = { navController.navigate("notune/neural_search") }
            )
        }
    }
}
