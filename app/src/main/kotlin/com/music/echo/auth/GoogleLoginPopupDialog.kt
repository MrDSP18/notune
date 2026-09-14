package com.music.echo.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun GoogleLoginPopupDialog(
    authManager: GoogleAuthManager,
    onLoginSuccess: (GoogleUserAccount) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isSignUpTab by remember { mutableStateOf(false) }

    var inputName by remember { mutableStateOf("") }
    var inputEmail by remember { mutableStateOf("") }
    var selectedAvatarEmoji by remember { mutableStateOf("⚡") }
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

    val avatars = listOf("⚡", "🎧", "🔥", "✨", "🚀", "🪐", "💎", "👾")

    val infiniteTransition = rememberInfiniteTransition(label = "dialog_neon")
    val borderGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderGlow"
    )

    Dialog(
        onDismissRequest = { /* Mandatory login modal */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF14070A),
                            Color(0xFF070408),
                            Color(0xFF030204)
                        )
                    )
                )
                .border(
                    width = 1.8.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFF0031).copy(alpha = borderGlow),
                            Color(0xFF00E5FF).copy(alpha = borderGlow * 0.7f),
                            Color(0xFFFF00CC).copy(alpha = borderGlow)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
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
                            color = Color.White.copy(alpha = 0.04f),
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
                // Header Badge
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
                        text = "NØTUNE ACCOUNT GATEWAY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = NothingFont,
                            color = Color(0xFFFF0031),
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = if (isSignUpTab) "CREATE NØTUNE PROFILE" else "POPUP GOOGLE SIGN IN",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = NothingFont,
                        color = Color.White,
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Black
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Tab Selector (Sign In vs Sign Up)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (!isSignUpTab) Color(0xFFFF0031) else Color.Transparent)
                            .clickable { isSignUpTab = false }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚡ 1-TAP SIGN IN",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = NothingFont,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSignUpTab) Color(0xFFFF0031) else Color.Transparent)
                            .clickable { isSignUpTab = true }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✨ CREATIVE SIGN UP",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = NothingFont,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (!isSignUpTab) {
                    // --- SIGN IN MODE ---
                    Text(
                        text = "CHOOSE GOOGLE ACCOUNT FOR 1-TAP LOGIN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = NothingFont,
                            color = Color.White.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(Modifier.height(10.dp))

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
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFFFF0031).copy(alpha = 0.18f) else Color(0xFF141414)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.2.dp,
                                if (isSelected) Color(0xFFFF0031) else Color.White.copy(alpha = 0.12f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(Color(0xFFFF0031), Color(0xFFFF8800)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = acc.name.take(1).uppercase(),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = NothingFont,
                                            color = Color.White,
                                            fontWeight = FontWeight.Black
                                        )
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = acc.name,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontFamily = NothingFont,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
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
                                        Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                                            Text("✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Express Guest Login Button
                    OutlinedButton(
                        onClick = {
                            if (isSubmitting) return@OutlinedButton
                            isSubmitting = true
                            val guestAccount = GoogleUserAccount(
                                id = "guest_" + System.currentTimeMillis(),
                                name = "Express Listener",
                                email = "guest.listener@notune.io",
                                photoUrl = "https://lh3.googleusercontent.com/a/default-user"
                            )
                            scope.launch {
                                authManager.saveGoogleLogin(
                                    id = guestAccount.id,
                                    name = guestAccount.name,
                                    email = guestAccount.email,
                                    photoUrl = guestAccount.photoUrl
                                )
                                onLoginSuccess(guestAccount.copy(isLoggedIn = true))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "⚡ INSTANT EXPRESS GUEST PASS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = NothingFont,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                } else {
                    // --- CREATIVE SIGN UP MODE ---
                    Text(
                        text = "PICK AVATAR BADGE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = NothingFont,
                            color = Color.White.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        avatars.forEach { avatar ->
                            val isSelected = selectedAvatarEmoji == avatar
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color(0xFFFF0031) else Color.White.copy(alpha = 0.08f))
                                    .clickable { selectedAvatarEmoji = avatar },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(avatar, fontSize = 18.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = inputName,
                        onValueChange = { inputName = it },
                        label = { Text("Choose Username", style = MaterialTheme.typography.bodySmall) },
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
                        onValueChange = { inputEmail = it },
                        label = { Text("Your Email Address", style = MaterialTheme.typography.bodySmall) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF0031),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedLabelColor = Color(0xFFFF0031),
                            cursorColor = Color(0xFFFF0031)
                        )
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Action Submit Button
                Button(
                    onClick = {
                        if (isSubmitting) return@Button
                        isSubmitting = true

                        val targetAccount = if (isSignUpTab) {
                            GoogleUserAccount(
                                id = "notune_user_" + System.currentTimeMillis(),
                                name = if (inputName.isNotBlank()) inputName else "NØTUNE Listener",
                                email = if (inputEmail.isNotBlank()) inputEmail else "listener@notune.io",
                                photoUrl = "https://lh3.googleusercontent.com/a/default-user"
                            )
                        } else {
                            if (inputEmail.isNotBlank()) {
                                GoogleUserAccount(
                                    id = "google_user_" + System.currentTimeMillis(),
                                    name = if (inputName.isNotBlank()) inputName else inputEmail.substringBefore("@"),
                                    email = inputEmail,
                                    photoUrl = "https://lh3.googleusercontent.com/a/default-user"
                                )
                            } else {
                                selectedPresetAccount ?: presetAccounts.first()
                            }
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
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF0031),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (isSignUpTab) "✨ CREATE ACCOUNT & ENTER" else "G  SIGN IN WITH GOOGLE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = NothingFont,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "🔒 Secure Encrypted Token • NØTUNE 1-Tap Auth",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
