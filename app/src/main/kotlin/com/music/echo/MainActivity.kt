
package echo.music.iad1tya
import echo.music.iad1tya.R
import echo.music.iad1tya.BuildConfig
import echo.music.iad1tya.ui.screens.settings.RingtoneViewModel
import echo.music.iad1tya.ui.component.RingtoneTrimmerDialog
import echo.music.iad1tya.ui.component.RingtoneProgressDialog
import echo.music.iad1tya.ui.component.AppFloatingNavBar
import com.music.echo.auth.GoogleAuthManager
import com.music.echo.auth.GoogleLoginPopupDialog
import com.music.echo.auth.GoogleUserAccount
import echo.music.iad1tya.ui.component.floatingtabbar.rememberFloatingTabBarScrollConnection
import echo.music.iad1tya.constants.UseFloatingNavBarKey
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import android.Manifest
import android.annotation.SuppressLint
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.CubicBezierEasing

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalView
import android.view.HapticFeedbackConstants
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.util.Consumer
import androidx.core.view.WindowCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.toBitmap
import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import echo.music.iad1tya.constants.AppBarHeight
import echo.music.iad1tya.constants.AiRecommendationsKey
import echo.music.iad1tya.constants.AppLanguageKey
import echo.music.iad1tya.constants.DarkModeKey
import echo.music.iad1tya.constants.DefaultOpenTabKey
import echo.music.iad1tya.constants.DisableScreenshotKey
import echo.music.iad1tya.constants.DynamicThemeKey
import echo.music.iad1tya.constants.EnableHighRefreshRateKey
import echo.music.iad1tya.constants.FloatingToolbarBottomPadding
import echo.music.iad1tya.constants.FloatingToolbarHorizontalPadding
import echo.music.iad1tya.constants.ListenTogetherInTopBarKey
import echo.music.iad1tya.constants.ListenTogetherUsernameKey
import echo.music.iad1tya.constants.MiniPlayerBottomSpacing
import echo.music.iad1tya.constants.MiniPlayerHeight
import echo.music.iad1tya.constants.NavigationBarAnimationSpec
import echo.music.iad1tya.constants.NavigationBarHeight
import echo.music.iad1tya.notune.updater.checkForUpdate
import echo.music.iad1tya.notune.updater.getAutoUpdateCheckSetting
import echo.music.iad1tya.notune.updater.isNewerVersion
import echo.music.iad1tya.notune.updater.saveUpdateAvailableState
import echo.music.iad1tya.notune.updater.getUpdateNotificationsSetting
import echo.music.iad1tya.notune.UpdateNotificationHelper
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import echo.music.iad1tya.constants.PauseListenHistoryKey
import echo.music.iad1tya.constants.PauseSearchHistoryKey
import echo.music.iad1tya.constants.PureBlackKey
import echo.music.iad1tya.constants.SYSTEM_DEFAULT
import echo.music.iad1tya.constants.SelectedThemeColorKey
import echo.music.iad1tya.constants.StopMusicOnTaskClearKey
import echo.music.iad1tya.constants.UseNewMiniPlayerDesignKey
import echo.music.iad1tya.constants.*
import echo.music.iad1tya.ui.component.shimmer.getShimmerTheme
import echo.music.iad1tya.db.MusicDatabase
import echo.music.iad1tya.db.entities.SearchHistory
import echo.music.iad1tya.extensions.toEnum
import echo.music.iad1tya.models.toMediaMetadata
import echo.music.iad1tya.playback.DownloadUtil
import echo.music.iad1tya.playback.MusicService
import echo.music.iad1tya.playback.MusicService.MusicBinder
import echo.music.iad1tya.playback.PlayerConnection
import echo.music.iad1tya.playback.queues.YouTubeQueue
import echo.music.iad1tya.ui.component.*
import echo.music.iad1tya.ui.component.backdrop.backdrops.rememberLayerBackdrop
import echo.music.iad1tya.ui.component.backdrop.backdrops.layerBackdrop
import echo.music.iad1tya.ui.menu.YouTubeSongMenu
import echo.music.iad1tya.ui.player.BottomSheetPlayer
import echo.music.iad1tya.ui.screens.Screens
import echo.music.iad1tya.ui.screens.SettingDialoge
import echo.music.iad1tya.ui.screens.WelcomeDialog
import echo.music.iad1tya.ui.screens.navigationBuilder
import echo.music.iad1tya.ui.screens.settings.DarkMode
import echo.music.iad1tya.ui.screens.settings.NavigationTab
import echo.music.iad1tya.ui.theme.ColorSaver
import echo.music.iad1tya.ui.theme.DefaultThemeColor
import echo.music.iad1tya.ui.theme.notuneTheme
import echo.music.iad1tya.ui.theme.extractThemeColor
import echo.music.iad1tya.ui.utils.appBarScrollBehavior
import echo.music.iad1tya.ui.utils.resetHeightOffset
import echo.music.iad1tya.utils.SyncUtils
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.get
import echo.music.iad1tya.utils.rememberEnumPreference
import echo.music.iad1tya.utils.rememberPreference
import echo.music.iad1tya.utils.reportException
import echo.music.iad1tya.utils.setAppLocale
import echo.music.iad1tya.viewmodels.HomeViewModel
import com.valentinilk.shimmer.LocalShimmerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.Locale
import javax.inject.Inject

val EmphasizedEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

@Suppress("DEPRECATION", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        const val ACTION_SEARCH = "echo.music.iad1tya.action.SEARCH"
        const val ACTION_LIBRARY = "echo.music.iad1tya.action.LIBRARY"
        const val ACTION_RECOGNITION = "echo.music.iad1tya.action.RECOGNITION"
        const val EXTRA_AUTO_START_RECOGNITION = "auto_start_recognition"
    }

    @Inject
    lateinit var database: MusicDatabase

    @Inject
    lateinit var downloadUtil: DownloadUtil

    @Inject
    lateinit var syncUtils: SyncUtils

    @Inject
    lateinit var listenTogetherManager: echo.music.iad1tya.listentogether.ListenTogetherManager

    @Inject
    lateinit var aiRecommendationHelper: echo.music.iad1tya.ai.AiRecommendationHelper

    @Inject
    lateinit var tasteProfileRepository: com.music.echo.notune.personalization.repository.TasteProfileRepository

    @Inject
    lateinit var connectionManager: echo.music.iad1tya.playback.PlayerConnectionManager

    private lateinit var navController: NavHostController
    private var pendingIntent: Intent? = null

    override fun onStart() {
        super.onStart()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1000)
            }
        }

        connectionManager.bind()
    }

    override fun onStop() {
        connectionManager.unbind()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        val playerConnection = connectionManager.playerConnection.value
        if (dataStore.get(StopMusicOnTaskClearKey, false) &&
            playerConnection?.isPlaying?.value == true &&
            isFinishing
        ) {
            stopService(Intent(this, MusicService::class.java))
            connectionManager.unbind()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (::navController.isInitialized) {
            handleDeepLinkIntent(intent, navController)
            handleRecognitionIntent(intent, navController)
            handleAssistantSearchIntent(intent, navController)
        } else {
            pendingIntent = intent
        }
    }

    override fun startForegroundService(service: Intent): android.content.ComponentName? {
        return try {
            super.startForegroundService(service)
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && e is android.app.ForegroundServiceStartNotAllowedException) {
                Timber.e(e, "Suppressed ForegroundServiceStartNotAllowedException in MainActivity")
                null
            } else {
                throw e
            }
        }
    }

    @Inject
    lateinit var aiEngine: echo.music.iad1tya.notune.ai.AiEngine

    @Inject
    lateinit var aiToolManager: echo.music.iad1tya.notune.ai.tools.AiToolManager

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                lifecycleScope.launch {
                    val result = aiEngine.generateResponse(
                        prompt = spokenText,
                        tools = aiEngine.getMusicTools(),
                        systemInstruction = "You are NØTUNE, handling a voice command. Execute the most relevant tool. Spoken command: $spokenText"
                    )
                    result.onSuccess { response ->
                        response.toolCalls?.forEach { aiToolManager.executeTool(it) }
                        if (response.text.isNotBlank()) {
                            Toast.makeText(this@MainActivity, response.text, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        try {
            startService(Intent(this, echo.music.iad1tya.playback.MusicService::class.java))
        } catch (e: Exception) {
            timber.log.Timber.e(e, "Failed to pre-start MusicService for warmup")
        }

        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        WindowCompat.setDecorFitsSystemWindows(window, false)

        
        listenTogetherManager.initialize()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            val locale = dataStore[AppLanguageKey]
                ?.takeUnless { it == SYSTEM_DEFAULT }
                ?.let { Locale.forLanguageTag(it) }
                ?: Locale.getDefault()
            setAppLocale(this, locale)
        }

        if (java.io.File(filesDir, "clear_export_state").exists()) {
            lifecycleScope.launch {
                dataStore.edit { preferences ->
                    preferences.remove(echo.music.iad1tya.constants.ExportingSongIdsKey)
                    preferences.remove(echo.music.iad1tya.constants.ExportedSongIdsKey)
                    preferences.remove(echo.music.iad1tya.constants.ExportProgressKey)
                }
                java.io.File(filesDir, "clear_export_state").delete()
            }
        }

        lifecycleScope.launch {
            dataStore.data
                .map { (try { it[DisableScreenshotKey] } catch(e: Exception) { null }) ?: false }
                .distinctUntilChanged()
                .collectLatest {
                    if (it) {
                        window.setFlags(
                            WindowManager.LayoutParams.FLAG_SECURE,
                            WindowManager.LayoutParams.FLAG_SECURE,
                        )
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    }
                }
        }
        
        lifecycleScope.launch {
            dataStore.data
                .map { (try { it[AiRecommendationsKey] } catch(e: Exception) { null }) ?: false }
                .distinctUntilChanged()
                .collectLatest { enabled ->
                    val workManager = androidx.work.WorkManager.getInstance(this@MainActivity)
                    if (enabled) {
                        val request = androidx.work.PeriodicWorkRequestBuilder<echo.music.iad1tya.ai.AiRecommendationWorker>(1, java.util.concurrent.TimeUnit.DAYS)
                            .setConstraints(androidx.work.Constraints.Builder().setRequiredNetworkType(androidx.work.NetworkType.CONNECTED).build())
                            .build()
                        workManager.enqueueUniquePeriodicWork(
                            "AiRecommendationWorker",
                            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                            request
                        )
                    } else {
                        workManager.cancelUniqueWork("AiRecommendationWorker")
                    }
                }
        }

        setContent {
            val playerConnection by connectionManager.playerConnection.collectAsState()
            
            LaunchedEffect(playerConnection) {
                listenTogetherManager.setPlayerConnection(playerConnection)
                aiToolManager.playerConnection = playerConnection
            }

            notuneApp(
                playerConnection = playerConnection,
                database = database,
                downloadUtil = downloadUtil,
                syncUtils = syncUtils,
            )
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    private fun notuneApp(
        playerConnection: PlayerConnection?,
        database: MusicDatabase,
        downloadUtil: DownloadUtil,
        syncUtils: SyncUtils,
    ) {
        val enableDynamicTheme by rememberPreference(DynamicThemeKey, defaultValue = true)
        val enableHighRefreshRate by rememberPreference(EnableHighRefreshRateKey, defaultValue = true)
        val context = LocalContext.current
        var showUpdateDialog by remember { androidx.compose.runtime.mutableStateOf(false) }
        var availableUpdateVersion by remember { androidx.compose.runtime.mutableStateOf("") }
        var availableUpdateChangelog by remember { androidx.compose.runtime.mutableStateOf<List<echo.music.iad1tya.notune.updater.ChangelogSection>>(emptyList()) }
        var availableUpdateDescription by remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
        var whatsNewInfo by remember { androidx.compose.runtime.mutableStateOf<echo.music.iad1tya.notune.updater.WhatsNewInfo?>(null) }

        LaunchedEffect(Unit) {
            val currentVersion = BuildConfig.VERSION_NAME
            val lastSeenVersion = echo.music.iad1tya.notune.updater.getLastSeenChangelogVersion(context)
            if (lastSeenVersion.isEmpty()) {
                echo.music.iad1tya.notune.updater.saveLastSeenChangelogVersion(context, currentVersion)
            } else if (lastSeenVersion != currentVersion) {
                whatsNewInfo = echo.music.iad1tya.notune.updater.fetchChangelogForVersion(currentVersion)
            }
        }

        LaunchedEffect(Unit) {
            if (getAutoUpdateCheckSetting(context)) {
                delay(2000L)
                checkForUpdate(
                    context = context,
                    onSuccess = { latestVersion, isAvailable, changelog, _, _, description, _, _ ->
                        saveUpdateAvailableState(context, isAvailable)
                        if (isAvailable) {
                            availableUpdateVersion = latestVersion
                            availableUpdateChangelog = changelog
                            availableUpdateDescription = description
                            showUpdateDialog = true
                        }
                        if (isAvailable && getUpdateNotificationsSetting(context)) {
                            UpdateNotificationHelper.showUpdateNotification(context, latestVersion)
                        }
                    },
                    onError = { }
                )
            }
        }

        LaunchedEffect(enableHighRefreshRate) {
            val window = this@MainActivity.window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val layoutParams = window.attributes
                if (enableHighRefreshRate) {
                    layoutParams.preferredDisplayModeId = 0
                } else {
                    val modes = window.windowManager.defaultDisplay.supportedModes
                    val mode60 = modes.firstOrNull { kotlin.math.abs(it.refreshRate - 60f) < 1f }
                        ?: modes.minByOrNull { kotlin.math.abs(it.refreshRate - 60f) }

                    if (mode60 != null) {
                        layoutParams.preferredDisplayModeId = mode60.modeId
                    }
                }
                window.attributes = layoutParams
            } else {
                val params = window.attributes
                if (enableHighRefreshRate) {
                    params.preferredRefreshRate = 0f
                } else {
                    params.preferredRefreshRate = 60f
                }
                window.attributes = params
            }
        }

        val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
        val isSystemInDarkTheme = isSystemInDarkTheme()
        val useDarkTheme = remember(darkTheme, isSystemInDarkTheme) {
            if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
        }

        LaunchedEffect(useDarkTheme) {
            setSystemBarAppearance(useDarkTheme)
        }

        val pureBlackEnabled by rememberPreference(PureBlackKey, defaultValue = false)
        val pureBlack = remember(pureBlackEnabled, useDarkTheme) {
            pureBlackEnabled && useDarkTheme
        }

        val (selectedThemeColorInt) = rememberPreference(SelectedThemeColorKey, defaultValue = DefaultThemeColor.toArgb())
        val selectedThemeColor = Color(selectedThemeColorInt)

        var themeColor by rememberSaveable(stateSaver = ColorSaver) {
            mutableStateOf(selectedThemeColor)
        }

        LaunchedEffect(selectedThemeColor) {
            if (!enableDynamicTheme) {
                themeColor = selectedThemeColor
            }
        }

        LaunchedEffect(playerConnection, enableDynamicTheme, selectedThemeColor) {
            val pc = playerConnection
            if (!enableDynamicTheme || pc == null) {
                themeColor = selectedThemeColor
                return@LaunchedEffect
            }

            pc.service.currentMediaMetadata.collectLatest { song ->
                if (song?.thumbnailUrl != null) {
                    withContext(Dispatchers.IO) {
                        try {
                            val result = imageLoader.execute(
                                ImageRequest.Builder(this@MainActivity)
                                    .data(song.thumbnailUrl)
                                    .allowHardware(false)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .networkCachePolicy(CachePolicy.ENABLED)
                                    .crossfade(false)
                                    .build()
                            )
                            themeColor = result.image?.toBitmap()?.extractThemeColor() ?: selectedThemeColor
                        } catch (e: Exception) {
                            themeColor = selectedThemeColor
                        }
                    }
                } else {
                    themeColor = selectedThemeColor
                }
            }
        }

        val (enableHaptics) = rememberPreference(echo.music.iad1tya.constants.EnableHapticsKey, defaultValue = false)
        val view = LocalView.current
        var lastScrollHapticTime by remember { mutableStateOf(0L) }

        notuneTheme(
            darkTheme = useDarkTheme,
            pureBlack = pureBlack,
        ) {
            if (showUpdateDialog) {
                echo.music.iad1tya.notune.component.UpdateAvailableDialog(
                    version = availableUpdateVersion,
                    changelog = availableUpdateChangelog,
                    description = availableUpdateDescription,
                    onDismiss = { showUpdateDialog = false }
                )
            } else {
                whatsNewInfo?.let { info ->
                    echo.music.iad1tya.notune.updater.WhatsNewDialog(
                        version = BuildConfig.VERSION_NAME,
                        info = info,
                        onDismiss = {
                            echo.music.iad1tya.notune.updater.saveLastSeenChangelogVersion(
                                context,
                                BuildConfig.VERSION_NAME,
                            )
                            whatsNewInfo = null
                        }
                    )
                }
            }
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (pureBlack) Color.Black else MaterialTheme.colorScheme.surface)
                    .pointerInput(enableHaptics) {
                        if (enableHaptics) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(androidx.compose.ui.input.pointer.PointerEventPass.Initial)
                                    val isClick = event.changes.any { it.changedToDown() }
                                    val isScroll = event.changes.any { it.positionChange() != Offset.Zero && it.pressed }
                                    if (isClick) {
                                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                                    } else if (isScroll) {
                                        val currentTime = System.currentTimeMillis()
                                        if (currentTime - lastScrollHapticTime > 100) {
                                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                                            lastScrollHapticTime = currentTime
                                        }
                                    }
                                }
                            }
                        }
                    }
            ) {
                val configuration = LocalWindowInfo.current
                val cutoutInsets = WindowInsets.displayCutout
                val windowsInsets = WindowInsets.systemBars
                val bottomInset = with(LocalDensity.current) { windowsInsets.getBottom(this).toDp() }
                val bottomInsetDp = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

                val navController = rememberNavController()
                this@MainActivity.navController = navController
                val homeViewModel: HomeViewModel = hiltViewModel()
                val accountImageUrl by homeViewModel.accountImageUrl.collectAsState()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val (previousTab, setPreviousTab) = rememberSaveable { mutableStateOf("home") }

                val (listenTogetherInTopBar) = rememberPreference(ListenTogetherInTopBarKey, defaultValue = true)
                val navigationItems = remember(listenTogetherInTopBar) { 
                    if (listenTogetherInTopBar) {
                        Screens.MainScreens.filter { it != Screens.ListenTogether }
                    } else {
                        Screens.MainScreens
                    }
                }
                val (useNewMiniPlayerDesign) = rememberPreference(UseNewMiniPlayerDesignKey, defaultValue = true)
                val defaultOpenTab = remember {
                    dataStore[DefaultOpenTabKey].toEnum(defaultValue = NavigationTab.HOME)
                }
                val tabOpenedFromShortcut = remember {
                    when (intent?.action) {
                        ACTION_SEARCH -> NavigationTab.LIBRARY
                        ACTION_LIBRARY -> NavigationTab.SEARCH
                        else -> null
                    }
                }

                val topLevelScreens = remember {
                    listOf(
                        Screens.Home.route,
                        Screens.Library.route,
                        Screens.ListenTogether.route,
                        "settings",
                    )
                }

                val currentRoute by remember {
                    derivedStateOf { navBackStackEntry?.destination?.route }
                }

                val inSearchScreen by remember {
                    derivedStateOf { currentRoute?.startsWith("search/") == true }
                }
                val navigationItemRoutes = remember(navigationItems) {
                    navigationItems.map { it.route }.toSet()
                }

                val shouldShowNavigationBar = remember(currentRoute, navigationItemRoutes) {
                    currentRoute == null ||
                        navigationItemRoutes.contains(currentRoute) ||
                        currentRoute!!.startsWith("search/") ||
                        currentRoute!!.startsWith("album/") ||
                        currentRoute!!.startsWith("online_playlist/") ||
                        currentRoute!!.startsWith("local_playlist/") ||
                        currentRoute!!.startsWith("artist/")
                }

                val isLandscape = configuration.containerDpSize.width > configuration.containerDpSize.height
                val showRail = isLandscape && !inSearchScreen && currentRoute != "ambient_mode"

                val navPadding = if (shouldShowNavigationBar && !showRail) {
                    NavigationBarHeight + FloatingToolbarBottomPadding
                } else {
                    0.dp
                }

                val navigationBarHeight by animateDpAsState(
                    targetValue = if (shouldShowNavigationBar && !showRail) NavigationBarHeight else 0.dp,
                    animationSpec = NavigationBarAnimationSpec,
                    label = "navBarHeight",
                )

                val (useFloatingNavBar) = rememberPreference(UseFloatingNavBarKey, defaultValue = false)
                val floatingNavBarScrollConnection = rememberFloatingTabBarScrollConnection()

                val playerBottomSheetState = rememberBottomSheetState(
                    dismissedBound = 0.dp,
                    collapsedBound = if (useFloatingNavBar && !showRail && shouldShowNavigationBar) {
                        0.dp
                    } else {
                        bottomInset +
                            (if (!showRail && shouldShowNavigationBar) navPadding else 0.dp) +
                            (if (useNewMiniPlayerDesign) MiniPlayerBottomSpacing else 0.dp) +
                            MiniPlayerHeight
                    },
                    expandedBound = maxHeight,
                )

                val onShuffleClick: (() -> Unit)? = remember(playerConnection, playerBottomSheetState) {
                    playerConnection?.let { connection ->
                        {
                            if (playerBottomSheetState.isExpanded) {
                                playerBottomSheetState.collapseSoft()
                            }
                            connection.player.shuffleModeEnabled = !connection.player.shuffleModeEnabled
                        }
                    }
                }
                val shuffleEnabled by playerConnection?.shuffleModeEnabled?.collectAsState() ?: remember { mutableStateOf(false) }

                val onMusicRecognitionClick: (() -> Unit) = remember(navController, playerBottomSheetState) {
                    {
                        if (playerBottomSheetState.isExpanded) {
                            playerBottomSheetState.collapseSoft()
                        }
                        navController.navigate("recognition") {
                            launchSingleTop = true
                        }
                    }
                }

                val playerMediaMetadata = playerConnection?.player?.currentMediaItem?.mediaMetadata
                val hasDockedPlayerAccessory =
                    useFloatingNavBar && playerMediaMetadata != null && !showRail && shouldShowNavigationBar

                val playerAwareWindowInsets = remember(
                    bottomInset,
                    shouldShowNavigationBar,
                    playerBottomSheetState.isDismissed,
                    showRail,
                ) {
                    var bottom = bottomInset
                    if (shouldShowNavigationBar && !showRail) {
                        bottom += NavigationBarHeight
                    }
                    if (!playerBottomSheetState.isDismissed) bottom += MiniPlayerHeight
                    windowsInsets
                        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                        .add(WindowInsets(top = AppBarHeight, bottom = bottom))
                }

                val topAppBarScrollBehavior = appBarScrollBehavior(
                    canScroll = {
                        !inSearchScreen &&
                            (playerBottomSheetState.isCollapsed || playerBottomSheetState.isDismissed)
                    },
                )

                LaunchedEffect(navBackStackEntry) {
                    if (navigationItems.fastAny { it.route == navBackStackEntry?.destination?.route }) {
                        if (navigationItems.fastAny { it.route == previousTab }) {
                            topAppBarScrollBehavior.state.resetHeightOffset()
                        }
                    }
                    topAppBarScrollBehavior.state.resetHeightOffset()
                    navController.currentBackStackEntry?.destination?.route?.let {
                        setPreviousTab(it)
                    }
                }

                LaunchedEffect(playerConnection) {
                    val pc = playerConnection ?: return@LaunchedEffect
                    if (pc.player.currentMediaItem == null) {
                        if (!playerBottomSheetState.isDismissed) {
                            playerBottomSheetState.dismiss()
                        }
                    } else {
                        if (playerBottomSheetState.isDismissed) {
                            playerBottomSheetState.collapseSoft()
                        }
                    }
                }

                DisposableEffect(playerConnection, playerBottomSheetState) {
                    val pc = playerConnection ?: return@DisposableEffect onDispose { }
                    val listener = object : Player.Listener {
                        override fun onMediaItemTransition(
                            mediaItem: MediaItem?,
                            reason: Int,
                        ) {
                            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED &&
                                mediaItem != null &&
                                playerBottomSheetState.isDismissed
                            ) {
                                playerBottomSheetState.collapseSoft()
                            }
                        }
                    }
                    pc.player.addListener(listener)
                    onDispose {
                        pc.player.removeListener(listener)
                    }
                }

                var shouldShowTopBar by rememberSaveable { mutableStateOf(false) }
                LaunchedEffect(navBackStackEntry, listenTogetherInTopBar) {
                    val cr = navBackStackEntry?.destination?.route
                    val isListenTogetherScreen = cr == Screens.ListenTogether.route || 
                        cr == "listen_together_from_topbar"
                    shouldShowTopBar = cr in topLevelScreens &&
                        cr != "settings" &&
                        !(isListenTogetherScreen && listenTogetherInTopBar)
                }

                val coroutineScope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }
                var showSettingDialoge by remember { mutableStateOf(false) }

                val (lastOpenedVersionCode, setLastOpenedVersionCode) = rememberPreference(echo.music.iad1tya.constants.LastOpenedVersionCodeKey, -1)
                var showWelcomeDialog by remember { mutableStateOf(false) }
                val googleAuthManager = remember { GoogleAuthManager(applicationContext) }
                val googleAccount by googleAuthManager.googleAccountFlow.collectAsState(initial = GoogleUserAccount())

                LaunchedEffect(lastOpenedVersionCode) {
                    if (lastOpenedVersionCode < BuildConfig.VERSION_CODE) {
                        showWelcomeDialog = true
                    }
                }

                val currentTitle = when (navBackStackEntry?.destination?.route) {
                    Screens.Home.route -> "NØTUNE"
                    Screens.Search.route -> stringResource(R.string.search)
                    Screens.Library.route -> stringResource(R.string.filter_library)
                    Screens.ListenTogether.route -> stringResource(R.string.together)
                    else -> ""
                }

                val pauseListenHistory by rememberPreference(PauseListenHistoryKey, defaultValue = false)
                val eventCount by database.eventCount().collectAsState(initial = 0)
                val showHistoryButton = remember(pauseListenHistory, eventCount) {
                    !(pauseListenHistory && eventCount == 0)
                }

                val (liquidGlassGlobalEnabled) = rememberPreference(LiquidGlassGlobalEnabledKey, defaultValue = false)
                val (liquidGlassVibrancy) = rememberPreference(LiquidGlassVibrancyKey, defaultValue = 1f)
                val (liquidGlassBlurRadius) = rememberPreference(LiquidGlassBlurRadiusKey, defaultValue = 8f)
                val (liquidGlassSurfaceOpacity) = rememberPreference(LiquidGlassSurfaceOpacityKey, defaultValue = 0.4f)
                val (themePreset) = rememberEnumPreference(ThemePresetKey, ThemePreset.NOTHING)
                
                val glassEffectConfig = remember(
                    liquidGlassGlobalEnabled, useFloatingNavBar, liquidGlassVibrancy, liquidGlassBlurRadius,
                    liquidGlassSurfaceOpacity, themePreset
                ) {
                    GlassEffectConfig(
                        globalEnabled = liquidGlassGlobalEnabled || themePreset == ThemePreset.NOTHING,
                        vibrancy = liquidGlassVibrancy,
                        blurRadius = liquidGlassBlurRadius,
                        surfaceOpacity = liquidGlassSurfaceOpacity,
                        surfaceTintColor = Color.White,
                        playerEnabled = true,
                        miniPlayerEnabled = true,
                        navBarEnabled = true,
                    )
                }
                
                val baseBg = if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainer
                val appBackdrop = rememberLayerBackdrop {
                    drawRect(baseBg)
                    drawContent()
                }

                val ringtoneViewModel: RingtoneViewModel = viewModel()
                val ringtoneUiState by ringtoneViewModel.uiState.collectAsState()

                CompositionLocalProvider(
                    LocalRingtoneViewModel provides ringtoneViewModel,
                    LocalDatabase provides database,
                    LocalContentColor provides if (pureBlack) Color.White else contentColorFor(MaterialTheme.colorScheme.surface),
                    LocalPlayerConnection provides playerConnection,
                    LocalPlayerAwareWindowInsets provides playerAwareWindowInsets,
                    LocalDownloadUtil provides downloadUtil,
                    LocalShimmerTheme provides getShimmerTheme(),
                    LocalSyncUtils provides syncUtils,
                    LocalListenTogetherManager provides listenTogetherManager,
                    LocalGlassEffectConfig provides glassEffectConfig,
                    LocalAppBackdrop provides appBackdrop,
                ) {

                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            AnimatedVisibility(
                                visible = shouldShowTopBar,
                                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                                exit = fadeOut(animationSpec = tween(durationMillis = 200))
                            ) {
                                TopAppBar(
                                    title = {
                                        Text(
                                            text = currentTitle,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 24.sp
                                            ),
                                        )
                                    },
                                    actions = {
                                        if (showHistoryButton) {
                                            IconButton(onClick = { navController.navigate("history") }) {
                                                Icon(
                                                    painter = painterResource(R.drawable.music_history),
                                                    contentDescription = stringResource(R.string.history)
                                                )
                                            }
                                        }
                                        IconButton(onClick = { navController.navigate("stats") }) {
                                            Icon(
                                                painter = painterResource(R.drawable.stats),
                                                contentDescription = stringResource(R.string.stats)
                                            )
                                        }
                                        IconButton(onClick = { navController.navigate("social_hub") }) {
                                            Icon(
                                                painter = painterResource(R.drawable.group_filled),
                                                contentDescription = "Social Hub"
                                            )
                                        }
                                        if (listenTogetherInTopBar) {
                                            IconButton(onClick = { navController.navigate("listen_together_from_topbar") }) {
                                                Icon(
                                                    painter = painterResource(R.drawable.group_outlined),
                                                    contentDescription = stringResource(R.string.together)
                                                )
                                            }
                                        }
                                         IconButton(onClick = { showSettingDialoge = true }) {
                                            BadgedBox(badge = {}) {
                                                if (accountImageUrl != null) {
                                                    AsyncImage(
                                                        model = accountImageUrl,
                                                        contentDescription = stringResource(R.string.account),
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .clip(CircleShape)
                                                    )
                                                 } else {
                                                     Icon(
                                                         painter = painterResource(R.drawable.settings),
                                                         contentDescription = stringResource(R.string.account),
                                                         modifier = Modifier.size(24.dp)
                                                     )
                                                 }
                                            }
                                        }
                                    },
                                    scrollBehavior = topAppBarScrollBehavior,
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainer,
                                        scrolledContainerColor = if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainer,
                                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                                        actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
                                    modifier = Modifier.windowInsetsPadding(
                                        if (showRail) WindowInsets(left = NavigationBarHeight).add(cutoutInsets.only(WindowInsetsSides.Start))
                                        else cutoutInsets.only(WindowInsetsSides.Start + WindowInsetsSides.End)
                                    )
                                )
                            }
                        },
                        bottomBar = {
                            val onNavItemClick: (Screens, Boolean) -> Unit = remember(navController, coroutineScope, topAppBarScrollBehavior, playerBottomSheetState) {
                                { screen: Screens, isSelected: Boolean ->
                                    if (playerBottomSheetState.isExpanded) playerBottomSheetState.collapseSoft()
                                    if (isSelected) {
                                        navController.currentBackStackEntry?.savedStateHandle?.set("scrollToTop", true)
                                        coroutineScope.launch { topAppBarScrollBehavior.state.resetHeightOffset() }
                                    } else {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            }

                            if (!showRail && currentRoute != "update" && currentRoute != "listen_together/chat" && currentRoute != "ambient_mode" && currentRoute != "uptime" && currentRoute?.startsWith("settings") != true) {
                                Box {
                                    BottomSheetPlayer(
                                        state = playerBottomSheetState,
                                        navController = navController,
                                        pureBlack = pureBlack
                                    )

                                    val navSlideDistance = bottomInset + FloatingToolbarBottomPadding + NavigationBarHeight
                                    val navOffsetY = if (navigationBarHeight == 0.dp) navSlideDistance else {
                                        val slideOffset = navSlideDistance * playerBottomSheetState.progress.coerceIn(0f, 1f)
                                        val hideOffset = navSlideDistance * (1 - navigationBarHeight.coerceAtMost(NavigationBarHeight) / NavigationBarHeight)
                                        slideOffset + hideOffset
                                    }

                                    if (useFloatingNavBar) {
                                        AppFloatingNavBar(
                                            navigationItems = navigationItems,
                                            currentRoute = currentRoute,
                                            onItemClick = onNavItemClick,
                                            scrollConnection = floatingNavBarScrollConnection,
                                            pureBlack = pureBlack,
                                            showPlayerAccessory = hasDockedPlayerAccessory,
                                            onAccessoryClick = { playerBottomSheetState.expandSoft() },
                                            onMusicRecognitionClick = onMusicRecognitionClick,
                                            musicRecognitionContentDescription = stringResource(R.string.recognition),
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .padding(horizontal = 16.dp)
                                                .padding(bottom = bottomInset + 8.dp)
                                                .graphicsLayer {
                                                    val hiddenOffset = size.height + (bottomInset + 8.dp).toPx()
                                                    val navBarHeightPx = navigationBarHeight.toPx()
                                                    translationY = if (navBarHeightPx == 0f) hiddenOffset else {
                                                        val progress = playerBottomSheetState.progress.coerceIn(0f, 1f)
                                                        val slideOffset = hiddenOffset * progress
                                                        val hideOffset = hiddenOffset * (1 - navBarHeightPx / NavigationBarHeight.toPx())
                                                        slideOffset + hideOffset
                                                    }
                                                }
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .height(navSlideDistance)
                                                .offset(y = navOffsetY),
                                        ) {
                                            FloatingNavigationToolbar(
                                                items = navigationItems,
                                                pureBlack = pureBlack,
                                                onShuffleClick = onShuffleClick,
                                                shuffleEnabled = shuffleEnabled,
                                                shuffleIconRes = R.drawable.shuffle,
                                                shuffleContentDescription = stringResource(R.string.shuffle),
                                                onMusicRecognitionClick = onMusicRecognitionClick,
                                                musicRecognitionContentDescription = stringResource(R.string.recognition),
                                                onAiHubClick = { navController.navigate("settings/ai") { launchSingleTop = true } },
                                                aiHubIconRes = R.drawable.sparks,
                                                aiHubContentDescription = stringResource(R.string.ai_lyrics_translation),
                                                isSelected = { screen -> currentRoute == screen.route || currentRoute?.startsWith("${screen.route}/") == true },
                                                onItemClick = onNavItemClick,
                                                modifier = Modifier
                                                    .align(Alignment.BottomCenter)
                                                    .padding(start = FloatingToolbarHorizontalPadding, end = FloatingToolbarHorizontalPadding, bottom = bottomInset + FloatingToolbarBottomPadding)
                                                    .height(NavigationBarHeight)
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.BottomCenter)
                                                .height(bottomInsetDp)
                                                .graphicsLayer { alpha = if (playerBottomSheetState.progress > 0f || (useNewMiniPlayerDesign && !shouldShowNavigationBar)) 0f else 1f }
                                                .background(baseBg)
                                        )
                                    }
                                }
                            } else {
                                if (currentRoute != "update" && currentRoute != "listen_together/chat" && currentRoute != "ambient_mode" && currentRoute != "uptime" && currentRoute?.startsWith("settings") != true) {
                                    BottomSheetPlayer(
                                        state = playerBottomSheetState,
                                        navController = navController,
                                        pureBlack = pureBlack
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .height(bottomInsetDp)
                                        .graphicsLayer { alpha = if (playerBottomSheetState.progress > 0f || (useNewMiniPlayerDesign && !shouldShowNavigationBar)) 0f else 1f }
                                        .background(baseBg)
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                            .then(if (useFloatingNavBar) Modifier.nestedScroll(floatingNavBarScrollConnection) else Modifier)
                    ) {
                        Row(Modifier.fillMaxSize()) {
                            if (showRail && currentRoute != "update") {
                                AppNavigationRail(
                                    navigationItems = navigationItems,
                                    currentRoute = currentRoute,
                                    onItemClick = { screen, isSelected ->
                                        if (playerBottomSheetState.isExpanded) playerBottomSheetState.collapseSoft()
                                        if (isSelected) {
                                            navController.currentBackStackEntry?.savedStateHandle?.set("scrollToTop", true)
                                            coroutineScope.launch { topAppBarScrollBehavior.state.resetHeightOffset() }
                                        } else {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    pureBlack = pureBlack,
                                    onSearchLongClick = { navController.navigate("recognition") { launchSingleTop = true } },
                                    onMusicRecognitionClick = onMusicRecognitionClick,
                                    musicRecognitionContentDescription = stringResource(R.string.recognition),
                                    onShuffleClick = onShuffleClick,
                                    shuffleEnabled = shuffleEnabled,
                                    shuffleIconRes = R.drawable.shuffle,
                                    shuffleContentDescription = stringResource(R.string.shuffle),
                                    onAiHubClick = { navController.navigate("settings/ai") { launchSingleTop = true } },
                                    aiHubIconRes = R.drawable.sparks,
                                    aiHubContentDescription = stringResource(R.string.ai_lyrics_translation)
                                )
                            }
                            Box(Modifier.weight(1f)) {
                                NavHost(
                                    navController = navController,
                                    startDestination = when (tabOpenedFromShortcut ?: defaultOpenTab) {
                                        NavigationTab.HOME -> Screens.Home
                                        NavigationTab.LIBRARY -> Screens.Library
                                        else -> Screens.Home
                                    }.route,
                                    enterTransition = {
                                        val ci = navigationItems.indexOfFirst { it.route == targetState.destination.route }
                                        val pi = navigationItems.indexOfFirst { it.route == initialState.destination.route }
                                        if (ci == -1 || ci > pi) slideInHorizontally(tween(400, easing = EmphasizedEasing)) { it / 8 } + fadeIn(tween(400, easing = EmphasizedEasing))
                                        else slideInHorizontally(tween(400, easing = EmphasizedEasing)) { -it / 8 } + fadeIn(tween(400, easing = EmphasizedEasing))
                                    },
                                    exitTransition = {
                                        val ci = navigationItems.indexOfFirst { it.route == initialState.destination.route }
                                        val ti = navigationItems.indexOfFirst { it.route == targetState.destination.route }
                                        if (ti == -1 || ti > ci) slideOutHorizontally(tween(400, easing = EmphasizedEasing)) { -it / 8 } + fadeOut(tween(400, easing = EmphasizedEasing))
                                        else slideOutHorizontally(tween(400, easing = EmphasizedEasing)) { it / 8 } + fadeOut(tween(400, easing = EmphasizedEasing))
                                    },
                                    modifier = Modifier.layerBackdrop(appBackdrop).nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                                ) {
                                    navigationBuilder(navController, topAppBarScrollBehavior, this@MainActivity, snackbarHostState)
                                }
                            }
                        }
                    }

                    BottomSheetMenu(state = LocalMenuState.current, modifier = Modifier.align(Alignment.BottomCenter))
                    BottomSheetPage(state = LocalBottomSheetPageState.current, modifier = Modifier.align(Alignment.BottomCenter))

                    RingtoneTrimmerDialog(
                        isVisible = ringtoneUiState.showTrimmer,
                        songId = ringtoneUiState.targetSongId,
                        songTitle = ringtoneUiState.targetSongTitle,
                        duration = ringtoneUiState.targetSongDuration,
                        onDismiss = { ringtoneViewModel.hideTrimmer() },
                        onResolveStreamUrl = { ringtoneViewModel.getStreamUrl(this@MainActivity, it) },
                        onConfirm = { start, end -> ringtoneViewModel.setAsRingtone(this@MainActivity, start, end) }
                    )

                    if (ringtoneUiState.showProgress) {
                        RingtoneProgressDialog(
                            isVisible = ringtoneUiState.showProgress,
                            progress = ringtoneUiState.progress,
                            statusMessage = ringtoneUiState.statusMessage,
                            isComplete = ringtoneUiState.isComplete,
                            isSuccess = ringtoneUiState.isSuccess,
                            onDismiss = { ringtoneViewModel.dismissProgress() },
                            onOpenSettings = { ringtoneViewModel.openRingtoneSettings(this@MainActivity) }
                        )
                    }

                    if (showSettingDialoge) {
                        SettingDialoge(
                            onDismissRequest = { showSettingDialoge = false },
                            onNavigate = { route -> showSettingDialoge = false; navController.navigate(route) },
                            homeViewModel = homeViewModel
                        )
                    }

                    if (!googleAccount.isLoggedIn) {
                        GoogleLoginPopupDialog(authManager = googleAuthManager, onLoginSuccess = { })
                    }

                    if (showWelcomeDialog) {
                        WelcomeDialog(
                            tasteProfileRepository = tasteProfileRepository,
                            onDismissRequest = {
                                showWelcomeDialog = false
                                setLastOpenedVersionCode(BuildConfig.VERSION_CODE)
                                lifecycleScope.launch { aiRecommendationHelper.generateRecommendations(this@MainActivity) }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun handleDeepLinkIntent(intent: Intent, navController: NavHostController) {
        var uri = intent.data
        if (uri == null) {
            val extraText = intent.extras?.getString(Intent.EXTRA_TEXT)
            if (extraText != null) {
                val urlRegex = "(https?://[^\\s]+)".toRegex()
                val match = urlRegex.find(extraText)
                if (match != null) uri = match.value.toUri()
            }
        }
        if (uri == null) return
        intent.data = null
        intent.removeExtra(Intent.EXTRA_TEXT)
        val coroutineScope = lifecycle.coroutineScope
        val listenCode = uri.getQueryParameter("code") ?: uri.getQueryParameter("room") ?: uri.pathSegments.getOrNull(1)
        val isListenLink = uri.pathSegments.firstOrNull() == "listen" || uri.host?.equals("listen", ignoreCase = true) == true || uri.host?.equals("github.com", ignoreCase = true) == true || !listenCode.isNullOrBlank()
        if (!listenCode.isNullOrBlank() && isListenLink) {
            val username = dataStore.get(ListenTogetherUsernameKey, "").ifBlank { "Guest" }
            listenTogetherManager.joinRoom(listenCode, username)
            return
        }
        when (val path = uri.pathSegments.firstOrNull()) {
            "playlist" -> uri.getQueryParameter("list")?.let { playlistId ->
                if (playlistId.startsWith("OLAK5uy_")) {
                    coroutineScope.launch(Dispatchers.IO) {
                        YouTube.albumSongs(playlistId).onSuccess { songs ->
                            songs.firstOrNull()?.album?.id?.let { browseId -> withContext(Dispatchers.Main) { navController.navigate("album/$browseId") } }
                        }.onFailure { reportException(it) }
                    }
                } else navController.navigate("online_playlist/$playlistId")
            }
            "browse" -> uri.lastPathSegment?.let { browseId -> navController.navigate("album/$browseId") }
            "channel", "c" -> uri.lastPathSegment?.let { artistId -> navController.navigate("artist/$artistId") }
            "search" -> uri.getQueryParameter("q")?.let { navController.navigate("search/${URLEncoder.encode(it, "UTF-8")}") }
            else -> {
                val videoId = when {
                    path == "watch" -> uri.getQueryParameter("v")
                    uri.host == "youtu.be" || uri.host == "share.notune.fun" -> uri.pathSegments.firstOrNull()
                    else -> null
                }
                val playlistId = uri.getQueryParameter("list")
                if (videoId != null) {
                    coroutineScope.launch(Dispatchers.IO) {
                        YouTube.queue(listOf(videoId), playlistId).onSuccess { queue ->
                            withContext(Dispatchers.Main) {
                                var attempts = 0
                                while (connectionManager.playerConnection.value == null && attempts < 20) { delay(100); attempts++ }
                                connectionManager.playerConnection.value?.playQueue(YouTubeQueue(WatchEndpoint(videoId = queue.firstOrNull()?.id, playlistId = playlistId), queue.firstOrNull()?.toMediaMetadata()))
                            }
                        }.onFailure { reportException(it) }
                    }
                } else if (playlistId != null) {
                    coroutineScope.launch(Dispatchers.IO) {
                        YouTube.queue(null, playlistId).onSuccess { queue ->
                            val firstItem = queue.firstOrNull()
                            withContext(Dispatchers.Main) {
                                var attempts = 0
                                while (connectionManager.playerConnection.value == null && attempts < 20) { delay(100); attempts++ }
                                connectionManager.playerConnection.value?.playQueue(YouTubeQueue(WatchEndpoint(videoId = firstItem?.id, playlistId = playlistId), firstItem?.toMediaMetadata()))
                            }
                        }.onFailure { reportException(it) }
                    }
                }
            }
        }
    }

    private fun setSystemBarAppearance(isDark: Boolean) {
        WindowCompat.getInsetsController(window, window.decorView.rootView).apply {
            isAppearanceLightStatusBars = !isDark
            isAppearanceLightNavigationBars = !isDark
        }
    }
    private fun handleRecognitionIntent(intent: Intent, navController: NavHostController) {
        if (intent.action != ACTION_RECOGNITION) return
        val autoStart = intent.getBooleanExtra(EXTRA_AUTO_START_RECOGNITION, false)
        intent.removeExtra(EXTRA_AUTO_START_RECOGNITION)
        navController.navigate(if (autoStart) "recognition?autoStart=true" else "recognition") { launchSingleTop = true }
    }
    private fun handleAssistantSearchIntent(intent: Intent, navController: NavHostController) {
        if (intent.action == android.provider.MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH) {
            val query = intent.getStringExtra(android.app.SearchManager.QUERY) ?: return
            navController.navigate("search/${URLEncoder.encode(query, "UTF-8")}")
        }
    }
}

val LocalDatabase = staticCompositionLocalOf<MusicDatabase> { error("No database provided") }
val LocalRingtoneViewModel = compositionLocalOf<RingtoneViewModel> { error("No RingtoneViewModel provided") }
val LocalPlayerConnection = staticCompositionLocalOf<PlayerConnection?> { error("No PlayerConnection provided") }
val LocalPlayerAwareWindowInsets = compositionLocalOf<WindowInsets> { error("No WindowInsets provided") }
val LocalDownloadUtil = staticCompositionLocalOf<DownloadUtil> { error("No DownloadUtil provided") }
val LocalSyncUtils = staticCompositionLocalOf<SyncUtils> { error("No SyncUtils provided") }
val LocalListenTogetherManager = staticCompositionLocalOf<echo.music.iad1tya.listentogether.ListenTogetherManager?> { null }
val LocalIsPlayerExpanded = compositionLocalOf { false }
