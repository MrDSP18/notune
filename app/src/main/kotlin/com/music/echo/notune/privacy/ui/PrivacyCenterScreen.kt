package com.music.echo.notune.privacy.ui

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.*
import echo.music.iad1tya.ui.component.DefaultDialog
import echo.music.iad1tya.ui.component.IconButton
import echo.music.iad1tya.utils.rememberPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyCenterScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: PrivacyCenterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val (privateSession, onPrivateSessionChange) = rememberPreference(PrivateSessionEnabledKey, false)
    val (aiPersonalization, onAiPersonalizationChange) = rememberPreference(AiPersonalizationKey, true)
    val (privateAiChat, onPrivateAiChatChange) = rememberPreference(PrivateAiChatKey, false)
    val (disableScreenshot, onDisableScreenshotChange) = rememberPreference(DisableScreenshotKey, false)

    val summary by viewModel.inventorySummary.collectAsState()
    val exportFile by viewModel.exportFile.collectAsState()
    val isExporting by viewModel.isExporting.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DefaultDialog(
            onDismiss = { showDeleteDialog = false },
            content = {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Delete All Local Data & Account?",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "This action will physically purge your local database, listening history, AI cache, and preferences. Data cannot be recovered.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            buttons = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.fullDataReset()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Everything", color = Color.White)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "NØTUNE Privacy Center",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
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
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // 1. Core Principle Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.security),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "YOUR MUSIC TASTE BELONGS TO YOU.",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "NØTUNE prioritizes local-first processing. Your listening history, Flow preferences, and Music DNA remain under your control.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Private Session Toggle Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (privateSession) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Private Session Mode",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (privateSession) "ON — History recording & Flow learning are paused" else "OFF — Standard local personalization active",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = privateSession,
                        onCheckedChange = onPrivateSessionChange
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Privacy at a Glance Dashboard
            Text(
                text = "YOUR DATA DASHBOARD",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Listening History", style = MaterialTheme.typography.bodySmall)
                            Text("${summary.listeningHistoryCount} items", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Column {
                            Text("Favorites", style = MaterialTheme.typography.bodySmall)
                            Text("${summary.favoritesCount} tracks", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Column {
                            Text("Playlists", style = MaterialTheme.typography.bodySmall)
                            Text("${summary.playlistsCount} lists", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. AI & Personalization Controls
            Text(
                text = "AI PRIVACY & DATA FLOW",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Private AI Chat", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("Conversations are not stored or synced", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(checked = privateAiChat, onCheckedChange = onPrivateAiChatChange)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Disable App Screenshots", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("Protects Sensitive Rooms and Private Vaults", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(checked = disableScreenshot, onCheckedChange = onDisableScreenshotChange)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Data Ownership Actions (Export & Delete)
            Text(
                text = "DATA CONTROL & EXPORT",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { viewModel.triggerDataExport() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isExporting,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isExporting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Exporting Portable Data...")
                } else {
                    Icon(painterResource(R.drawable.download), contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export My NØTUNE Data (JSON Zip)")
                }
            }

            exportFile?.let { file ->
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/zip"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share NØTUNE Data Export"))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Share / Save Export File")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { viewModel.clearListeningHistory() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Clear Listening History")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { viewModel.clearAiData() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Clear AI Cache")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Delete All Local Data & Account", color = Color.White)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
