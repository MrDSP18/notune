
package echo.music.iad1tya.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import echo.music.iad1tya.R
import echo.music.iad1tya.ui.theme.NothingFont

@Composable
fun WelcomeDialog(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    var stage by remember { mutableIntStateOf(0) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        stage++
    }

    Dialog(
        onDismissRequest = { /* Force onboarding */ },
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
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (stage) {
                    0 -> OnboardingIntro { stage++ }
                    1 -> OnboardingPermissions {
                        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            arrayOf(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                        permissionLauncher.launch(perms)
                    }
                    2 -> OnboardingAiFeatures { stage++ }
                    3 -> OnboardingFinal { onDismissRequest() }
                }
            }
        }
    }
}

@Composable
private fun OnboardingIntro(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFFFF0031), RoundedCornerShape(2.dp))
        ) {
            Box(modifier = Modifier.size(24.dp).background(Color.White, RoundedCornerShape(1.dp)))
        }
        
        Spacer(Modifier.height(32.dp))
        
        Text(
            text = "WELCOME TO NØTUNE",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = NothingFont,
                letterSpacing = 2.sp,
                color = Color.White
            ),
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = "The futuristic music ecosystem. Minimalist design. Intelligent core. Fully customizable.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(Modifier.height(48.dp))
        
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(2.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
        ) {
            Text("INITIALIZE", style = MaterialTheme.typography.labelLarge.copy(fontFamily = NothingFont))
        }
    }
}

@Composable
private fun OnboardingPermissions(onGrant: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(R.drawable.library_music),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(Modifier.height(32.dp))
        
        Text(
            text = "ACCESS YOUR MUSIC",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = NothingFont,
                color = Color.White
            )
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = "NØTUNE needs permission to scan your device for local audio files and show notifications.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(48.dp))
        
        Button(
            onClick = onGrant,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(2.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
        ) {
            Text("GRANT ACCESS", style = MaterialTheme.typography.labelLarge.copy(fontFamily = NothingFont))
        }
    }
}

@Composable
private fun OnboardingAiFeatures(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(R.drawable.sparks),
            contentDescription = null,
            tint = Color(0xFFFF0031),
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(Modifier.height(32.dp))
        
        Text(
            text = "AI-POWERED EXPERIENCE",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = NothingFont,
                color = Color.White
            )
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = "Discover music like never before with AI DJ, personalized recommendations, and natural language search. Connect your Gemini or Groq keys in settings later.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(48.dp))
        
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(2.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
        ) {
            Text("UNDERSTOOD", style = MaterialTheme.typography.labelLarge.copy(fontFamily = NothingFont))
        }
    }
}

@Composable
private fun OnboardingFinal(onFinish: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(R.drawable.check_circle),
            contentDescription = null,
            tint = Color(0xFF00FF41),
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(Modifier.height(32.dp))
        
        Text(
            text = "SYSTEM READY",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = NothingFont,
                color = Color.White
            )
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = "NØTUNE is now configured and ready for your first track.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(48.dp))
        
        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(2.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0031), contentColor = Color.White)
        ) {
            Text("START LISTENING", style = MaterialTheme.typography.labelLarge.copy(fontFamily = NothingFont))
        }
    }
}
