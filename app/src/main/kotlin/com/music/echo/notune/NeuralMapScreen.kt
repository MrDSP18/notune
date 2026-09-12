
package echo.music.iad1tya.notune

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import echo.music.iad1tya.LocalDatabase
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.ui.theme.NothingFont
import kotlinx.coroutines.flow.first
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeuralMapScreen(
    navController: NavController
) {
    val database = LocalDatabase.current
    var topArtists by remember { mutableStateOf<List<String>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        topArtists = database.allArtistsByPlayTime().first().take(8).map { it.artist.name }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NEURAL MAP", style = MaterialTheme.typography.titleLarge.copy(fontFamily = NothingFont, letterSpacing = 2.sp)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(painter = androidx.compose.ui.res.painterResource(id = R.drawable.arrow_back), contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (topArtists.isNotEmpty()) {
                NeuralGraph(artists = topArtists)
            } else {
                Text("Analyzing your music DNA...", color = Color.White.copy(alpha = 0.5f), fontFamily = NothingFont)
            }
        }
    }
}

@Composable
fun NeuralGraph(artists: List<String>) {
    Canvas(modifier = Modifier.size(300.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 2 - 40.dp.toPx()
        
        // Draw connections
        for (i in artists.indices) {
            val angle = i * (2 * Math.PI / artists.size)
            val x = center.x + radius * cos(angle).toFloat()
            val y = center.y + radius * sin(angle).toFloat()
            
            drawLine(
                color = Color.White.copy(alpha = 0.1f),
                start = center,
                end = Offset(x, y),
                strokeWidth = 1.dp.toPx()
            )
            
            drawCircle(
                color = Color(0xFFFF0031),
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
        }
        
        drawCircle(
            color = Color.White,
            radius = 6.dp.toPx(),
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}
