package echo.music.iad1tya.ui.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AdaptiveLayout(
    windowInfo: AdaptiveWindowInfo = rememberAdaptiveWindowInfo(),
    modifier: Modifier = Modifier,
    navigationBarSlot: @Composable (AdaptiveWindowInfo) -> Unit,
    navigationRailSlot: @Composable (AdaptiveWindowInfo) -> Unit = navigationBarSlot,
    mainContentSlot: @Composable (AdaptiveWindowInfo) -> Unit,
    contextPanelSlot: (@Composable (AdaptiveWindowInfo) -> Unit)? = null,
    miniPlayerSlot: @Composable (AdaptiveWindowInfo) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(NotuneDesignSystem.colors.background)
    ) {
        val calculatedWindowInfo = rememberAdaptiveWindowInfo(
            availableWidthDp = maxWidth,
            availableHeightDp = maxHeight
        )

        when (calculatedWindowInfo.widthClass) {
            WindowWidthClass.COMPACT -> {
                // Phone layout: Main Content + MiniPlayer + Bottom Navigation Bar
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            mainContentSlot(calculatedWindowInfo)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        miniPlayerSlot(calculatedWindowInfo)
                        navigationBarSlot(calculatedWindowInfo)
                    }
                }
            }

            WindowWidthClass.MEDIUM -> {
                // Foldable / Small Tablet layout: Navigation Rail on left + Main Content + Bottom/Floating MiniPlayer
                Row(modifier = Modifier.fillMaxSize()) {
                    navigationRailSlot(calculatedWindowInfo)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                mainContentSlot(calculatedWindowInfo)
                            }
                            miniPlayerSlot(calculatedWindowInfo)
                        }
                    }
                }
            }

            WindowWidthClass.EXPANDED -> {
                // 3-Column Tablet Layout: [ Navigation Rail/Drawer | Main Content | Context Panel (Queue/Lyrics/Now Playing) ]
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NotuneDesignSystem.colors.background)
                ) {
                    // Left Navigation Rail/Drawer (220dp fixed)
                    Box(
                        modifier = Modifier
                            .width(220.dp)
                            .fillMaxHeight()
                            .border(1.dp, NotuneDesignSystem.colors.surfaceBorder)
                    ) {
                        navigationRailSlot(calculatedWindowInfo)
                    }

                    // Center Main Content Area
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                mainContentSlot(calculatedWindowInfo)
                            }
                            miniPlayerSlot(calculatedWindowInfo)
                        }
                    }

                    // Right Context Panel (360dp fixed) for Queue, Lyrics, or Now Playing
                    if (contextPanelSlot != null) {
                        Box(
                            modifier = Modifier
                                .width(360.dp)
                                .fillMaxHeight()
                                .border(1.dp, NotuneDesignSystem.colors.surfaceBorder)
                                .background(NotuneDesignSystem.colors.surface)
                        ) {
                            contextPanelSlot(calculatedWindowInfo)
                        }
                    }
                }
            }
        }
    }
}
