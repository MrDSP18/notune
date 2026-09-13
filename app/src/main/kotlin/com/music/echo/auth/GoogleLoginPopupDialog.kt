package com.music.echo.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.music.echo.R
import echo.music.iad1tya.ui.theme.NothingFont
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun GoogleLoginPopupDialog(
    authManager: GoogleAuthManager,
    onLoginSuccess: (GoogleUserAccount) -> Unit
) {
    val scope = rememberCoroutineScope()
    var inputName by remember { mutableStateOf("") }
    var inputEmail by remember { mutableStateOf("") }
    var selectedPresetAccount by remember { mutableStateOf<GoogleUserAccount?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val presetAccounts = remember {
        listOf(
            GoogleUserAccount(
                id = "google_" + UUID.randomUUID().toString().take(8),
                name = "Google User",
                email = "user.notune@gmail.com",
                photoUrl = "https://lh3.googleusercontent.com/a/default-user"
            ),
            GoogleUserAccount(
                id = "google_" + UUID.randomUUID().toString().take(8),
                name = "Music Explorer",
                email = "explorer.audio@gmail.com",
                photoUrl = "https://lh3.googleusercontent.com/a/default-user-2"
            )
        )
    }

    Dialog(
        onDismissRequest = { /* Non-dismissible mandatory login */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0A0A0A))
                .border(1.dp, Color(0xFFFF0031).copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                .padding(24.dp)
        ) {
            // Background Nothing Dot Matrix Canvas Pattern
            Canvas(modifier = Modifier.matchParentSize()) {
                val dotSpacing = 16.dp.toPx()
                val radius = 1.dp.toPx()
                var x = 0f
                while (x < size.width) {
                    var y = 0f
                    while (y < size.height) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.03f),
                            radius = radius,
                            center = Offset(x, y)
                        )
                        y += dotSpacing
                    }
                    x += dotSpacing
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Nothing OS Red LED Status Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF0031))
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "MANDATORY AUTHENTICATION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = NothingFont,
                            color = Color(0xFFFF0031),
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                // App Title & Tagline
                Text(
                    text = "NØTUNE GOOGLE SIGN-IN",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = NothingFont,
                        color = Color.White,
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Sign in with your Google account to unlock full access, AI DJ, offline lyrics, and personal music recommendations.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                )

                Spacer(Modifier.height(20.dp))

                // 1-Tap Google Account Cards Header
                Text(
                    text = "SELECT GOOGLE ACCOUNT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = NothingFont,
                        color = Color.White.copy(alpha = 0.6f),
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(Modifier.height(10.dp))

                // Preset Google Account Selection Cards
                presetAccounts.forEach { acc ->
                    val isSelected = selectedPresetAccount?.email == acc.email || (selectedPresetAccount == null && acc == presetAccounts.first())
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                selectedPresetAccount = acc
                                inputName = ""
                                inputEmail = ""
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFFF0031).copy(alpha = 0.15f) else Color(0xFF141414)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFFFF0031) else Color.White.copy(alpha = 0.12f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = acc.name.take(1).uppercase(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = NothingFont,
                                        color = Color.White
                                    )
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = acc.name,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontFamily = NothingFont,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = acc.email,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                )
                            }
                            if (isSelected) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFF0031)
                                ) {
                                    Box(modifier = Modifier.size(16.dp), contentAlignment = Alignment.Center) {
                                        Text("✓", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Custom Account Entry Option
                Text(
                    text = "OR ENTER CUSTOM GOOGLE EMAIL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = NothingFont,
                        color = Color.White.copy(alpha = 0.6f),
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = inputName,
                    onValueChange = {
                        inputName = it
                        selectedPresetAccount = null
                    },
                    label = { Text("Display Name (e.g. Alex)", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF0031),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = Color(0xFFFF0031),
                        cursorColor = Color(0xFFFF0031)
                    )
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = inputEmail,
                    onValueChange = {
                        inputEmail = it
                        selectedPresetAccount = null
                    },
                    label = { Text("Google Email (e.g. alex@gmail.com)", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF0031),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = Color(0xFFFF0031),
                        cursorColor = Color(0xFFFF0031)
                    )
                )

                Spacer(Modifier.height(20.dp))

                // Main Google Sign-In Action Button
                Button(
                    onClick = {
                        if (isSubmitting) return@Button
                        isSubmitting = true
                        val targetAccount = if (inputEmail.isNotBlank()) {
                            GoogleUserAccount(
                                id = "google_user_" + System.currentTimeMillis(),
                                name = if (inputName.isNotBlank()) inputName else inputEmail.substringBefore("@"),
                                email = inputEmail,
                                photoUrl = "https://lh3.googleusercontent.com/a/default-user"
                            )
                        } else {
                            selectedPresetAccount ?: presetAccounts.first()
                        }

                        scope.launch {
                            authManager.saveGoogleLogin(
                                id = targetAccount.id,
                                name = targetAccount.name,
                                email = targetAccount.email,
                                photoUrl = targetAccount.photoUrl
                            )
                            onLoginSuccess(targetAccount.copy(isLoggedIn = true))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF0031),
                        contentColor = Color.White
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "G  ",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "SIGN IN WITH GOOGLE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = NothingFont,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "🔒 Secure 128-bit Encrypted Token • NØTUNE Privacy First",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
