
package echo.music.iad1tya.ui.menu

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import androidx.navigation.NavController
import echo.music.iad1tya.LocalDatabase
import echo.music.iad1tya.LocalPlayerConnection
import echo.music.iad1tya.R
import echo.music.iad1tya.constants.*
import echo.music.iad1tya.db.entities.PlaylistEntity
import echo.music.iad1tya.models.MediaMetadata
import echo.music.iad1tya.playback.ExoDownloadService
import echo.music.iad1tya.ui.component.*
import echo.music.iad1tya.ui.theme.NothingFont
import echo.music.iad1tya.utils.rememberEnumPreference
import echo.music.iad1tya.utils.rememberPreference
import android.content.res.Configuration

@Composable
fun PlayerMenu(
    mediaMetadata: MediaMetadata?,
    navController: NavController,
    playerBottomSheetState: BottomSheetState,
    isQueueTrigger: Boolean? = false,
    onShowDetailsDialog: () -> Unit,
    onDismiss: () -> Unit,
) {
    mediaMetadata ?: return
    val context = LocalContext.current
    val database = LocalDatabase.current
    val playerConnection = LocalPlayerConnection.current ?: return
    
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager }
    var systemVolume by remember { mutableFloatStateOf(audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC).toFloat()) }
    var maxSystemVolume by remember { mutableFloatStateOf(audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC).toFloat()) }

    DisposableEffect(context) {
        val volumeChangeReceiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context, intent: android.content.Intent) {
                if (intent.action == "android.media.VOLUME_CHANGED_ACTION") {
                    val streamType = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1)
                    if (streamType == android.media.AudioManager.STREAM_MUSIC) {
                        systemVolume = audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC).toFloat()
                        maxSystemVolume = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC).toFloat()
                    }
                }
            }
        }
        context.registerReceiver(
            volumeChangeReceiver,
            android.content.IntentFilter("android.media.VOLUME_CHANGED_ACTION")
        )
        onDispose {
            context.unregisterReceiver(volumeChangeReceiver)
        }
    }
    
    val castHandler = remember(playerConnection) {
        try {
            playerConnection.service.castConnectionHandler
        } catch (e: Exception) {
            null
        }
    }
    val isCasting by castHandler?.isCasting?.collectAsState() ?: remember { mutableStateOf(false) }
    val castDeviceName by castHandler?.deviceName?.collectAsState() ?: remember { mutableStateOf(null) }

    val (enableExportAsMp3) = rememberPreference(EnableExportAsMp3Key, defaultValue = false)
    val isExporting by playerConnection.service.isExporting.collectAsState()
    val exportProgress by playerConnection.service.exportProgress.collectAsState()
    val isExported by produceState(initialValue = false, mediaMetadata.id) {
        value = playerConnection.service.isExported(mediaMetadata.id)
    }

    var showPitchTempoDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showPitchTempoDialog) {
        TempoPitchDialog(
            onDismiss = { showPitchTempoDialog = false },
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        if (isQueueTrigger != true) {
            if (isCasting && castDeviceName != null) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 24.dp, bottom = 6.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.cast),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(R.string.casting_to, castDeviceName ?: ""),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        item {
            NewMenuContent(
                actionGrid = {
                    NewActionGrid(
                        actions = listOf(
                            NewAction(
                                icon = { Icon(painter = painterResource(R.drawable.sparks), contentDescription = null) },
                                text = "EXPLAIN",
                                onClick = {
                                    onDismiss()
                                    navController.navigate("notune/ask")
                                }
                            ),
                            NewAction(
                                icon = { Icon(painter = painterResource(R.drawable.share), contentDescription = null) },
                                text = "SHARE",
                                onClick = {
                                    onDismiss()
                                    // Share logic
                                }
                            ),
                            NewAction(
                                icon = { Icon(painter = painterResource(R.drawable.timer), contentDescription = null) },
                                text = "SLEEP",
                                onClick = {
                                    onDismiss()
                                    // Sleep timer
                                }
                            )
                        )
                    )
                }
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            Material3MenuGroup(
                items = listOf(
                    Material3MenuItemData(
                        title = { Text(text = stringResource(R.string.view_artist)) },
                        icon = { Icon(painter = painterResource(R.drawable.artist), contentDescription = null) },
                        onClick = {
                            onDismiss()
                            mediaMetadata.artists.firstOrNull()?.id?.let {
                                navController.navigate("artist/$it")
                                playerBottomSheetState.collapseSoft()
                            }
                        }
                    ),
                    Material3MenuItemData(
                        title = { Text(text = stringResource(R.string.view_album)) },
                        icon = { Icon(painter = painterResource(R.drawable.album), contentDescription = null) },
                        onClick = {
                            onDismiss()
                            mediaMetadata.album?.id?.let {
                                navController.navigate("album/$it")
                                playerBottomSheetState.collapseSoft()
                            }
                        }
                    )
                )
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            Material3MenuGroup(
                items = listOf(
                    Material3MenuItemData(
                        title = { Text(text = stringResource(R.string.add_to_playlist)) },
                        icon = { Icon(painter = painterResource(R.drawable.playlist_add), contentDescription = null) },
                        onClick = {
                            onDismiss()
                            // Add to playlist logic
                        }
                    ),
                    Material3MenuItemData(
                        title = { Text(text = stringResource(R.string.action_download)) },
                        icon = { Icon(painter = painterResource(R.drawable.download), contentDescription = null) },
                        onClick = {
                            onDismiss()
                            val downloadRequest = DownloadRequest.Builder(mediaMetadata.id, mediaMetadata.id.toUri())
                                .setCustomCacheKey(mediaMetadata.id)
                                .setData(mediaMetadata.title.toByteArray())
                                .build()
                            DownloadService.sendAddDownload(context, ExoDownloadService::class.java, downloadRequest, false)
                        }
                    )
                )
            )
        }

        if (enableExportAsMp3) {
            item { Spacer(modifier = Modifier.height(12.dp)) }
            item {
                Material3MenuGroup(
                    items = listOf(
                        Material3MenuItemData(
                            title = { Text(text = stringResource(R.string.action_export)) },
                            icon = { Icon(painter = painterResource(R.drawable.folder), contentDescription = null) },
                            onClick = {
                                onDismiss()
                                playerConnection.service.exportAsMp3(mediaMetadata)
                            }
                        )
                    )
                )
            }
        }
        
        item { Spacer(modifier = Modifier.height(12.dp)) }
        item {
            Material3MenuGroup(
                items = listOf(
                    Material3MenuItemData(
                        title = { Text(text = stringResource(R.string.details)) },
                        icon = { Icon(painter = painterResource(R.drawable.info), contentDescription = null) },
                        onClick = {
                            onDismiss()
                            onShowDetailsDialog()
                        }
                    )
                )
            )
        }
    }
}

@Composable
fun TempoPitchDialog(onDismiss: () -> Unit) {
    // Basic implementation for now to satisfy imports
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(12.dp)) {
            Text("Tempo & Pitch Settings", modifier = Modifier.padding(24.dp))
        }
    }
}
