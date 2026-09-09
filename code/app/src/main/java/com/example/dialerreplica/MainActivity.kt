package com.example.dialerreplica

import android.Manifest
import android.app.PictureInPictureParams
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Rational
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Dialpad
import androidx.compose.material.icons.outlined.MicOff
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dialerreplica.data.AppDatabase
import com.example.dialerreplica.data.CallRecordEntity
import com.example.dialerreplica.data.NumberAttributionRepository
import com.example.dialerreplica.data.SettingsRepository
import com.example.dialerreplica.data.formatPhoneNumber
import com.example.dialerreplica.data.normalizePhoneNumber
import com.example.dialerreplica.media.LocalMediaPlayer
import com.example.dialerreplica.model.CallPhase
import com.example.dialerreplica.model.CallSessionSnapshot
import com.example.dialerreplica.service.CallSessionBus
import com.example.dialerreplica.service.CallSessionService
import com.example.dialerreplica.service.formatDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val DialerGreen = Color(0xFF14C150)
private val HangupRed = Color(0xFFEC3B35)
private val MainInk = Color(0xFF1A1A1A)
private val SecondaryInk = Color(0xFF929292)
private val DisabledInk = Color(0xFFC9C9C9)

// 统一控制底部“电话 / 联系人 / 个人收藏”区域；1.0f 为原始尺寸。
private const val BottomNavigationScale = 0.88f

// 统一控制主页整个拨号面板高度；大于 1.0f 时面板顶部向上扩展。
private const val DialerPanelHeightScale = 1.04f

// 拨号数字列与底部工具键共用同一组水平布局参数，保证三列中心线完全一致。
private val DialPadHorizontalPadding = 29.dp
private val DialPadColumnSpacing = 12.dp

// 通话缩小功能尚未确定实施；false 时隐藏入口并关闭自动画中画。
private const val CallMinimizeVisible = false

private enum class AppScreen { DIALER, CALLING, HISTORY, RECORD_DETAIL, SETTINGS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MaterialTheme { Surface(color = Color.White) { DialerReplicaApp() } } }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        val phase = CallSessionBus.snapshot.value.phase
        if (CallMinimizeVisible && (phase == CallPhase.DIALING || phase == CallPhase.CONNECTED)) {
            enterCallPictureInPicture()
        }
    }

    fun enterCallPictureInPicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isInPictureInPictureMode) {
            enterPictureInPictureMode(
                PictureInPictureParams.Builder().setAspectRatio(Rational(9, 16)).build(),
            )
        }
    }
}

@Composable
private fun DialerReplicaApp() {
    val context = LocalContext.current
    val activity = context as MainActivity
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.get(context) }
    val settings = remember { SettingsRepository(context) }
    val attributionRepository = remember { NumberAttributionRepository(context) }
    val localPlayer = remember { LocalMediaPlayer(context) }
    val session by CallSessionBus.snapshot.collectAsStateWithLifecycle()
    val records by database.callRecordDao().observeAll().collectAsStateWithLifecycle(initialValue = emptyList())
    val displayedRecords = records
    val clipboardAutoFill by settings.booleanFlow(SettingsRepository.CLIPBOARD_AUTO_FILL, true).collectAsStateWithLifecycle(initialValue = true)
    val backgroundUri by settings.stringFlow(SettingsRepository.BACKGROUND_URI).collectAsStateWithLifecycle(initialValue = null)
    val ringbackAudioUri by settings.stringFlow(SettingsRepository.RINGBACK_AUDIO_URI).collectAsStateWithLifecycle(initialValue = null)
    val ringbackVideoUri by settings.stringFlow(SettingsRepository.RINGBACK_VIDEO_URI).collectAsStateWithLifecycle(initialValue = null)

    var screen by rememberSaveable { mutableStateOf(AppScreen.DIALER) }
    var digits by rememberSaveable { mutableStateOf("1") }
    var hadSession by remember { mutableStateOf(false) }
    var lastClipboardNumber by rememberSaveable { mutableStateOf("") }
    var pendingSettingKey by remember { mutableStateOf<Preferences.Key<String>?>(null) }
    var pendingRecordId by remember { mutableStateOf<Long?>(null) }
    var selectedRecordId by rememberSaveable { mutableStateOf<Long?>(null) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    val recordPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) CallSessionService.toggleRecording(context)
    }
    val settingFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val key = pendingSettingKey
        if (uri != null && key != null) {
            takeReadPermission(context, uri)
            scope.launch { settings.setString(key, uri.toString()) }
        }
        pendingSettingKey = null
    }
    val recordingFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val recordId = pendingRecordId
        if (uri != null && recordId != null) {
            takeReadPermission(context, uri)
            val name = queryDisplayName(context, uri) ?: "外部录音"
            val duration = localPlayer.duration(uri.toString())
            scope.launch(Dispatchers.IO) {
                database.callRecordDao().attachRecording(recordId, uri.toString(), name, duration, "IMPORTED")
            }
        }
        pendingRecordId = null
    }

    DisposableEffect(Unit) { onDispose { localPlayer.stop() } }

    LaunchedEffect(session.phase) {
        if (session.phase != CallPhase.IDLE) {
            hadSession = true
            screen = AppScreen.CALLING
        } else if (hadSession) {
            hadSession = false
            screen = AppScreen.HISTORY
        }
    }

    DisposableEffect(lifecycleOwner, clipboardAutoFill) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && clipboardAutoFill && session.phase == CallPhase.IDLE) {
                val clipboard = context.getSystemService(ClipboardManager::class.java)
                val description = clipboard?.primaryClipDescription
                if (description?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true ||
                    description?.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML) == true
                ) {
                    val text = clipboard.primaryClip?.getItemAt(0)?.coerceToText(context)?.toString().orEmpty()
                    detectClipboardPhone(text)?.let { detected ->
                        if (detected != lastClipboardNumber) {
                            lastClipboardNumber = detected
                            digits = detected
                            screen = AppScreen.DIALER
                        }
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val view = LocalView.current
    androidx.compose.runtime.SideEffect {
        val darkIcons = screen != AppScreen.CALLING
        WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = darkIcons
        WindowCompat.getInsetsController(activity.window, view).isAppearanceLightNavigationBars = darkIcons
    }

    fun launchCall(number: String) {
        val normalized = normalizePhoneNumber(number)
        if (normalized.isBlank()) return
        digits = normalized
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        screen = AppScreen.CALLING
        CallSessionService.start(context, normalized)
    }

    fun startCall() = launchCall(digits)

    fun toggleRecording() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            CallSessionService.toggleRecording(context)
        } else recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    fun pickSetting(key: Preferences.Key<String>, mimeTypes: Array<String>) {
        pendingSettingKey = key
        settingFileLauncher.launch(mimeTypes)
    }

    when (screen) {
        AppScreen.DIALER -> DialerScreen(
            digits,
            attributionRepository.lookup(digits).displayText,
            { if (digits.length < 15) digits += it },
            { if (digits.isNotEmpty()) digits = digits.dropLast(1) },
            ::startCall,
            { screen = AppScreen.HISTORY },
        )
        AppScreen.CALLING -> CallingScreen(
            session,
            backgroundUri,
            if (session.phase == CallPhase.DIALING && session.ringbackActive) ringbackVideoUri else null,
            !ringbackAudioUri.isNullOrBlank(),
            ::toggleRecording,
            { CallSessionService.toggleAction(context, it) },
            { CallSessionService.localHangup(context) },
            { screen = AppScreen.SETTINGS },
            activity::enterCallPictureInPicture,
        )
        AppScreen.HISTORY -> CallHistoryScreen(
            displayedRecords,
            { if (digits.length < 15) digits += it },
            { if (digits.isNotEmpty()) digits = digits.dropLast(1) },
            ::startCall,
            { screen = AppScreen.DIALER },
            { screen = AppScreen.SETTINGS },
            { localPlayer.toggle(it) },
            { record ->
                selectedRecordId = record.id
                screen = AppScreen.RECORD_DETAIL
            },
            ::launchCall,
        )
        AppScreen.RECORD_DETAIL -> {
            val selectedRecord = displayedRecords.firstOrNull { it.id == selectedRecordId }
            val numberRecords = selectedRecord?.let { selected ->
                displayedRecords.filter { it.rawNumber == selected.rawNumber }.sortedByDescending { it.endedAt }
            }.orEmpty()
            CallRecordDetailScreen(
                selectedRecord,
                numberRecords,
                { screen = AppScreen.HISTORY },
                { uri -> localPlayer.toggle(uri) },
                { recordId ->
                    if (recordId > 0) {
                        pendingRecordId = recordId
                        recordingFileLauncher.launch(arrayOf("audio/*"))
                    }
                },
                ::launchCall,
                { rawNumber ->
                    selectedRecordId = null
                    screen = AppScreen.HISTORY
                    scope.launch(Dispatchers.IO) { database.callRecordDao().deleteByNumber(rawNumber) }
                },
            )
        }
        AppScreen.SETTINGS -> HiddenSettingsScreen(
            settings,
            { screen = if (session.phase == CallPhase.IDLE) AppScreen.HISTORY else AppScreen.CALLING },
            ::pickSetting,
        )
    }
}

@Composable
private fun DialerScreen(digits: String, location: String, onDigit: (String) -> Unit, onDelete: () -> Unit, onCall: () -> Unit, onHistory: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.White)) {
        BoxWithConstraints(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            val basePanelHeight = maxHeight - 289.dp
            val panelHeight = basePanelHeight * DialerPanelHeightScale
            val headerHeight = maxHeight - panelHeight
            Column(Modifier.fillMaxSize()) {
                NumberHeader(digits, location, Modifier.height(headerHeight))
                DialPadPanel(Modifier.height(panelHeight), true, onDigit, onDelete, onCall, onHistory)
            }
        }
    }
}

@Composable
private fun NumberHeader(digits: String, location: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(49.dp))
        Text(formatPhoneNumber(digits).ifBlank { " " }, color = MainInk, fontSize = 31.sp, fontWeight = FontWeight.Medium, maxLines = 1)
        if (digits.isNotBlank()) Text(location, color = Color(0xFF777777), fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))
    }
}

@Composable
private fun DialPadPanel(modifier: Modifier, showTopActions: Boolean, onDigit: (String) -> Unit, onDelete: () -> Unit, onCall: () -> Unit, onOpenDialer: () -> Unit) {
    Column(modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)).clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)).background(Color.White)) {
        if (showTopActions) TopDialerActions()
        KeypadGrid(Modifier.weight(1f).padding(horizontal = DialPadHorizontalPadding), onDigit)
        UtilityRow(onDelete, onCall)
        BottomDialerNavigation(onOpenDialer, showTopActions)
    }
}

@Composable
private fun TopDialerActions() {
    val actions = listOf("新建联系人", "保存至已有联系人", "视频呼叫", "发送信息")
    Row(Modifier.fillMaxWidth().height(76.dp).padding(horizontal = 10.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
        actions.forEach { label ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                when (label) {
                    "新建联系人" -> Icon(Icons.Outlined.Add, label, tint = MainInk, modifier = Modifier.size(24.dp))
                    "保存至已有联系人" -> Icon(Icons.Outlined.Person, label, tint = MainInk, modifier = Modifier.size(24.dp))
                    "视频呼叫" -> Image(painterResource(R.drawable.dialer_video), label, Modifier.size(24.dp), colorFilter = ColorFilter.tint(MainInk))
                    else -> Image(painterResource(R.drawable.dialer_message), label, Modifier.size(24.dp), colorFilter = ColorFilter.tint(MainInk))
                }
                Text(label, color = Color(0xFF666666), fontSize = 10.sp, maxLines = 1)
            }
        }
    }
}

private data class DialKeySpec(
    val value: String,
    val subLabel: String = "",
    val primaryImageRes: Int? = null,
    val secondaryImageRes: Int? = null,
)

private val keyRows = listOf(
    listOf(
        DialKeySpec("1", secondaryImageRes = R.drawable.dialer_voicemail),
        DialKeySpec("2", "ABC"),
        DialKeySpec("3", "DEF"),
    ),
    listOf(DialKeySpec("4", "GHI"), DialKeySpec("5", "JKL"), DialKeySpec("6", "MNO")),
    listOf(DialKeySpec("7", "PQRS"), DialKeySpec("8", "TUV"), DialKeySpec("9", "WXYZ")),
    listOf(
        DialKeySpec("*", "(P)", primaryImageRes = R.drawable.dialer_xing),
        DialKeySpec("0", "+"),
        DialKeySpec("#", "(W)"),
    ),
)

@Composable
private fun KeypadGrid(modifier: Modifier, onDigit: (String) -> Unit) {
    Column(modifier, verticalArrangement = Arrangement.SpaceEvenly) {
        keyRows.forEach { row ->
            Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(DialPadColumnSpacing)) {
                row.forEach { key ->
                    DialKey(key, Modifier.weight(1f).fillMaxHeight()) {
                        if (key.value.single().isDigit()) onDigit(key.value)
                    }
                }
            }
        }
    }
}

@Composable
private fun DialKey(key: DialKeySpec, modifier: Modifier, onClick: () -> Unit) {
    Column(modifier.clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(Modifier.height(35.dp), contentAlignment = Alignment.Center) {
            if (key.primaryImageRes != null) {
                Image(
                    painter = painterResource(key.primaryImageRes),
                    contentDescription = "星号",
                    modifier = Modifier.size(22.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(MainInk),
                )
            } else {
                Text(key.value, color = MainInk, fontSize = 29.sp, fontWeight = FontWeight.Medium)
            }
        }
        if (key.secondaryImageRes != null) {
            Image(
                painter = painterResource(key.secondaryImageRes),
                contentDescription = "语音信箱",
                modifier = Modifier.width(20.dp).height(12.dp),
                contentScale = ContentScale.Fit,
            )
        } else {
            Text(key.subLabel, color = SecondaryInk, fontSize = 10.sp, letterSpacing = 1.6.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun UtilityRow(onDelete: () -> Unit, onCall: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(68.dp).padding(horizontal = DialPadHorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(DialPadColumnSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Dialpad, "拨号盘", tint = MainInk, modifier = Modifier.size(27.dp))
        }
        Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
            Box(Modifier.size(56.dp).clip(CircleShape).clickable(onClick = onCall), contentAlignment = Alignment.Center) {
                Image(painterResource(R.drawable.dialer_call), "呼叫", Modifier.fillMaxSize(), colorFilter = ColorFilter.tint(DialerGreen))
                Text("HD", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopEnd).padding(top = 9.dp, end = 5.dp))
            }
        }
        Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
            Icon(Icons.AutoMirrored.Outlined.Backspace, "删除", tint = MainInk, modifier = Modifier.size(27.dp).clickable(onClick = onDelete))
        }
    }
}

@Composable
private fun BottomDialerNavigation(onDialer: () -> Unit, selected: Boolean) {
    Row(Modifier.fillMaxWidth().height((72f * BottomNavigationScale).dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
        BottomNavItem(Icons.Outlined.Phone, "电话", if (selected) DialerGreen else DisabledInk, onDialer)
        BottomNavItem(Icons.Outlined.Person, "联系人", DisabledInk) {}
        BottomNavItem(Icons.Outlined.Star, "个人收藏", DisabledInk) {}
    }
}

@Composable
private fun BottomNavItem(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(Modifier.width((92f * BottomNavigationScale).dp).clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, label, tint = color, modifier = Modifier.size((24f * BottomNavigationScale).dp))
        Text(label, color = color, fontSize = (11f * BottomNavigationScale).sp, modifier = Modifier.padding(top = (2f * BottomNavigationScale).dp))
    }
}

@Composable
private fun CallingScreen(session: CallSessionSnapshot, backgroundUri: String?, videoUri: String?, ringbackAudioConfigured: Boolean, onRecord: () -> Unit, onAction: (String) -> Unit, onHangup: () -> Unit, onSettings: () -> Unit, onMinimize: () -> Unit) {
    val connected = session.phase == CallPhase.CONNECTED
    Box(Modifier.fillMaxSize()) {
        CallingBackground(backgroundUri, videoUri, ringbackAudioConfigured)
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = .25f)))
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 31.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth().height(48.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                if (CallMinimizeVisible) {
                    TextButton(onClick = onMinimize) { Text("缩小", color = Color.White.copy(alpha = .8f)) }
                }
            }
            Spacer(Modifier.height(60.dp))
            Text(session.formattedNumber.ifBlank { " " }, color = Color.White, fontSize = 31.sp, fontWeight = FontWeight.Medium)
            Text(session.location, color = Color.White.copy(alpha = .72f), fontSize = 14.sp, modifier = Modifier.padding(top = 6.dp))
            AnimatedCallStateText(session, Modifier.padding(top = 10.dp))
            Spacer(Modifier.weight(1f))
            CallingActionGrid(session, connected, onRecord, onAction, onHangup, onSettings)
            Spacer(Modifier.height(25.dp))
        }
    }
}

@Composable
private fun AnimatedCallStateText(session: CallSessionSnapshot, modifier: Modifier = Modifier) {
    var visibleDotCount by remember(session.sessionId) { mutableIntStateOf(0) }
    LaunchedEffect(session.sessionId, session.phase) {
        visibleDotCount = 0
        if (session.phase == CallPhase.DIALING) {
            while (true) {
                delay(420)
                visibleDotCount = (visibleDotCount + 1) % 4
            }
        }
    }
    val textColor = Color.White.copy(alpha = .82f)
    when (session.phase) {
        CallPhase.DIALING -> {
            Row(modifier, verticalAlignment = Alignment.CenterVertically) {
                Text("正在拨号", color = textColor, fontSize = 14.sp)
                repeat(3) { index ->
                    Text(
                        ".",
                        color = if (index < visibleDotCount) textColor else Color.Transparent,
                        fontSize = 14.sp,
                    )
                }
            }
        }
        CallPhase.CONNECTED -> {
            Row(modifier, verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.call_hd),
                    contentDescription = "HD",
                    modifier = Modifier.width(20.dp).height(11.dp),
                    alpha = .82f,
                )
                Spacer(Modifier.width(5.dp))
                Text(formatDuration(session.callElapsedMs), color = textColor, fontSize = 14.sp)
            }
        }
        else -> Text(callStateText(session), color = textColor, fontSize = 14.sp, modifier = modifier)
    }
}

@Composable
private fun CallingBackground(backgroundUri: String?, videoUri: String?, ringbackAudioConfigured: Boolean) {
    val context = LocalContext.current
    if (!videoUri.isNullOrBlank()) {
        AndroidView(
            factory = { VideoView(it).apply { setVideoURI(Uri.parse(videoUri)); setOnPreparedListener { p -> p.isLooping = true; val v = if (ringbackAudioConfigured) 0f else 1f; p.setVolume(v, v); start() } } },
            update = { if (!it.isPlaying) it.start() },
            modifier = Modifier.fillMaxSize(),
        )
        return
    }
    val bitmap by produceState<androidx.compose.ui.graphics.ImageBitmap?>(null, backgroundUri) {
        value = if (backgroundUri.isNullOrBlank()) null else withContext(Dispatchers.IO) { loadImage(context, Uri.parse(backgroundUri)) }
    }
    if (bitmap != null) Image(bitmap!!, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
    else Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0xFFEEDFE4), Color(0xFF755F55), Color(0xFF241D18)), radius = 730f)))
}

@Composable
private fun CallingActionGrid(session: CallSessionSnapshot, connected: Boolean, onRecord: () -> Unit, onAction: (String) -> Unit, onHangup: () -> Unit, onSettings: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CallingImageActionButton(R.drawable.call_recording, if (session.recording) "录音 ${formatDuration(session.recordingElapsedMs)}" else "录音", connected, onRecord)
            CallingImageActionButton(R.drawable.call_ai_answer, "AI 接听", connected, onClick = { onAction("AI 接听") })
            CallingActionButton(Icons.Outlined.Add, "添加通话", false, false, onClick = {})
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CallingActionButton(Icons.Outlined.Videocam, "视频通话", false, false, onClick = {})
            CallingActionButton(Icons.Outlined.MicOff, "静音", connected, session.activeAction == "静音", onClick = { onAction("静音") })
            CallingActionButton(Icons.Outlined.MoreHoriz, "更多", true, false, onSettings)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CallingActionButton(Icons.Outlined.Dialpad, "拨号盘", connected, session.activeAction == "拨号盘", onClick = { onAction("拨号盘") }, showLabel = false)
            CallingImageActionButton(R.drawable.call_hangup, "挂断", true, onClick = onHangup, showLabel = false)
            CallingActionButton(Icons.AutoMirrored.Outlined.VolumeUp, "扬声器", connected, session.activeAction == "扬声器", onClick = { onAction("扬声器") }, showLabel = false)
        }
    }
}

@Composable
private fun CallingImageActionButton(imageRes: Int, label: String, enabled: Boolean, onClick: () -> Unit, showLabel: Boolean = true) {
    val contentAlpha = if (enabled) 1f else .42f
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(84.dp)) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = label,
            modifier = Modifier.size(68.dp).clip(CircleShape).clickable(enabled = enabled, onClick = onClick),
            contentScale = ContentScale.Fit,
            alpha = contentAlpha,
        )
        if (showLabel) {
            Text(label, color = Color.White.copy(alpha = contentAlpha), fontSize = if (label.length > 6) 10.sp else 13.sp, modifier = Modifier.padding(top = 7.dp), maxLines = 1)
        }
    }
}

@Composable
private fun CallingActionButton(icon: ImageVector, label: String, enabled: Boolean, active: Boolean, onClick: () -> Unit, showLabel: Boolean = true) {
    val background = if (active) Color.White.copy(alpha = .75f) else Color.White.copy(alpha = if (enabled) .25f else .13f)
    val tint = when { active -> MainInk; enabled -> Color.White; else -> Color.White.copy(alpha = .42f) }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(84.dp)) {
        Box(Modifier.size(68.dp).clip(CircleShape).background(background).clickable(enabled = enabled, onClick = onClick), contentAlignment = Alignment.Center) { Icon(icon, label, tint = tint, modifier = Modifier.size(29.dp)) }
        if (showLabel) {
            Text(label, color = tint, fontSize = if (label.length > 6) 10.sp else 13.sp, modifier = Modifier.padding(top = 7.dp), maxLines = 1)
        }
    }
}

@Composable
private fun CallHistoryScreen(records: List<CallRecordEntity>, onDigit: (String) -> Unit, onDelete: () -> Unit, onCall: () -> Unit, onOpenDialer: () -> Unit, onSettings: () -> Unit, onPlayRecording: (String) -> Unit, onOpenRecord: (CallRecordEntity) -> Unit, onRedial: (String) -> Unit) {
    var titleTaps by remember { mutableIntStateOf(0) }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val visibleRecords = if (selectedTab == 0) records else records.filter(CallRecordEntity::isMissedCall)
    Box(Modifier.fillMaxSize().background(Color.White)) {
        Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp)) {
            Row(Modifier.fillMaxWidth().padding(top = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("电话", color = MainInk, fontSize = 30.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { titleTaps++; if (titleTaps >= 5) { titleTaps = 0; onSettings() } })
                Spacer(Modifier.weight(1f))
                Icon(Icons.Outlined.MoreVert, "更多", tint = MainInk, modifier = Modifier.size(25.dp).clickable(onClick = onSettings))
            }
            Spacer(Modifier.height(24.dp))
            HistoryFilterTabs(selectedTab) { selectedTab = it }
            val listModifier = Modifier.weight(1f).fillMaxWidth().padding(top = 12.dp, bottom = 424.dp)
            if (visibleRecords.isEmpty()) {
                Box(listModifier, contentAlignment = Alignment.TopCenter) {
                    Text(if (selectedTab == 0) "暂无通话记录" else "暂无未接来电", color = SecondaryInk, fontSize = 14.sp, modifier = Modifier.padding(top = 30.dp))
                }
            } else {
                LazyColumn(listModifier) {
                    items(visibleRecords, key = { it.id }) { record ->
                        HistoryRow(record, onPlayRecording, onOpenRecord, onRedial)
                        HorizontalDivider(color = Color(0xFFE8E8E8))
                    }
                }
            }
        }
        DialPadPanel(Modifier.align(Alignment.BottomCenter).height(424.dp), false, onDigit, onDelete, onCall, onOpenDialer)
    }
}

@Composable
private fun HistoryFilterTabs(selectedTab: Int, onSelected: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth().height(40.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        listOf("全部通话", "未接来电").forEachIndexed { index, label ->
            val selected = selectedTab == index
            Column(
                Modifier.width(82.dp).fillMaxHeight().clickable { onSelected(index) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Text(label, color = if (selected) DialerGreen else SecondaryInk, fontSize = 16.sp, fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal)
                Spacer(Modifier.height(5.dp))
                Box(Modifier.width(62.dp).height(2.dp).background(if (selected) DialerGreen else Color.Transparent))
            }
        }
    }
}

@Composable
private fun HistoryRow(record: CallRecordEntity, onPlay: (String) -> Unit, onOpen: (CallRecordEntity) -> Unit, onRedial: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().height(72.dp).clickable { onOpen(record) }, verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFB8C1D4)), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Person, null, tint = Color.White, modifier = Modifier.size(26.dp)) }
        Column(Modifier.padding(start = 11.dp).weight(1f)) {
            Text(record.formattedNumber, color = MainInk, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { onRedial(record.rawNumber) })
            Text("▸ HD  ${record.location}".trimEnd(), color = SecondaryInk, fontSize = 10.sp, modifier = Modifier.padding(top = 3.dp))
            record.recordingUri?.let { uri -> Text("▶ 录音 ${formatDuration(record.recordingDurationMs)}", color = DialerGreen, fontSize = 10.sp, modifier = Modifier.clickable { onPlay(uri) }) }
        }
        Text(displayHistoryTime(record.endedAt), color = SecondaryInk, fontSize = 11.sp)
        Image(
            painter = painterResource(R.drawable.detail_des),
            contentDescription = "记录详情",
            modifier = Modifier.padding(start = 6.dp).size(32.dp).clickable { onOpen(record) },
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(SecondaryInk),
        )
    }
}

private fun CallRecordEntity.isMissedCall(): Boolean =
    result == "MISSED" || result == "MISSED_CALL" || result == "INCOMING_MISSED"

@Composable
private fun CallRecordDetailScreen(
    record: CallRecordEntity?,
    numberRecords: List<CallRecordEntity>,
    onBack: () -> Unit,
    onPlay: (String) -> Unit,
    onImport: (Long) -> Unit,
    onRedial: (String) -> Unit,
    onClear: (String) -> Unit,
) {
    BackHandler(onBack = onBack)
    if (record == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }
    val detailBackground = Color(0xFFF4F5F9)
    Box(Modifier.fillMaxSize().background(detailBackground)) {
        LazyColumn(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 12.dp).padding(bottom = 86.dp),
        ) {
            item {
                Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, "返回", tint = MainInk, modifier = Modifier.size(28.dp).clickable(onClick = onBack))
                }
            }
            item {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        Modifier.size(96.dp).clip(CircleShape).background(
                            Brush.verticalGradient(listOf(Color(0xFFC8D1E4), Color(0xFF9EABC4))),
                        ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.Person, null, tint = Color.White, modifier = Modifier.size(62.dp))
                    }
                    Text(record.formattedNumber, color = MainInk, fontSize = 31.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 14.dp).clickable { onRedial(record.rawNumber) })
                    Spacer(Modifier.height(24.dp))
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DetailQuickAction(Icons.Outlined.Phone, "呼叫", Modifier.weight(1f)) { onRedial(record.rawNumber) }
                    DetailQuickImageAction(R.drawable.dialer_message, "信息", Modifier.weight(1f)) {}
                    DetailQuickImageAction(R.drawable.dialer_video, "视频", Modifier.weight(1f)) {}
                }
            }
            item {
                Column(
                    Modifier.fillMaxWidth().padding(top = 12.dp).clip(RoundedCornerShape(18.dp)).background(Color.White).padding(horizontal = 14.dp, vertical = 12.dp),
                ) {
                    Text(record.formattedNumber, color = MainInk, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    if (record.location.isNotBlank()) Text(record.location, color = SecondaryInk, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
            item {
                Row(Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("通话记录", color = MainInk, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.weight(1f))
                    Text("清空", color = DialerGreen, fontSize = 16.sp, modifier = Modifier.clickable { onClear(record.rawNumber) })
                }
            }
            item {
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White).padding(horizontal = 14.dp)) {
                    numberRecords.forEachIndexed { index, itemRecord ->
                        CallDetailRecordRow(itemRecord, onPlay)
                        if (index != numberRecords.lastIndex) HorizontalDivider(color = Color(0xFFE7E7E7))
                    }
                }
            }
            item { Spacer(Modifier.height(12.dp)) }
        }
        DetailBottomActions(
            modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding(),
            onMore = { if (record.id > 0) onImport(record.id) },
        )
    }
}

@Composable
private fun DetailQuickAction(icon: ImageVector, label: String, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier.height(70.dp).clip(RoundedCornerShape(20.dp)).background(Color.White).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, label, tint = Color(0xFF6F6F6F), modifier = Modifier.size(24.dp))
        Text(label, color = Color(0xFF626262), fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))
    }
}

@Composable
private fun DetailQuickImageAction(drawableRes: Int, label: String, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier.height(70.dp).clip(RoundedCornerShape(20.dp)).background(Color.White).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(painterResource(drawableRes), label, Modifier.size(24.dp), colorFilter = ColorFilter.tint(Color(0xFF6F6F6F)))
        Text(label, color = Color(0xFF626262), fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))
    }
}

@Composable
private fun CallDetailRecordRow(record: CallRecordEntity, onPlay: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 11.dp)) {
        Text(displayDetailTime(record.endedAt), color = MainInk, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Text("▸ HD  ${record.formattedNumber}", color = SecondaryInk, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
        Text(detailRecordResult(record), color = SecondaryInk, fontSize = 13.sp, modifier = Modifier.padding(top = 3.dp))
        record.recordingUri?.let { uri ->
            Text("▶ 录音 ${formatDuration(record.recordingDurationMs)}", color = DialerGreen, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp).clickable { onPlay(uri) })
        }
    }
}

@Composable
private fun DetailBottomActions(modifier: Modifier = Modifier, onMore: () -> Unit) {
    Row(modifier.fillMaxWidth().height(86.dp).background(Color(0xFFF4F5F9)), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
        DetailBottomAction(Icons.Outlined.Add, "新建联系人") {}
        DetailBottomAction(Icons.Outlined.Person, "保存至已有联系人") {}
        DetailBottomAction(Icons.Outlined.MoreVert, "更多", onMore)
    }
}

@Composable
private fun DetailBottomAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(Modifier.width(108.dp).clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, label, tint = MainInk, modifier = Modifier.size(27.dp))
        Text(label, color = MainInk, fontSize = 11.sp, modifier = Modifier.padding(top = 5.dp), maxLines = 1)
    }
}

private fun displayDetailTime(timeMillis: Long): String = if (timeMillis <= 0) {
    "--:--"
} else {
    java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(timeMillis))
}

private fun detailRecordResult(record: CallRecordEntity): String = when {
    record.isMissedCall() -> "未接来电"
    record.connectedAt == null -> "未接通"
    else -> "${(record.durationMs / 1_000).coerceAtLeast(0)} 秒"
}

@Composable
private fun HiddenSettingsScreen(settings: SettingsRepository, onBack: () -> Unit, onPick: (Preferences.Key<String>, Array<String>) -> Unit) {
    BackHandler(onBack = onBack)
    val scope = rememberCoroutineScope()
    val clipboardAuto by settings.booleanFlow(SettingsRepository.CLIPBOARD_AUTO_FILL, true).collectAsStateWithLifecycle(true)
    val nextOutcome by settings.stringFlow(SettingsRepository.NEXT_OUTCOME).collectAsStateWithLifecycle("CONNECTED")
    val connectDelaySeconds by settings.intFlow(SettingsRepository.CONNECT_DELAY_SECONDS, SettingsRepository.DEFAULT_CONNECT_DELAY_SECONDS).collectAsStateWithLifecycle(SettingsRepository.DEFAULT_CONNECT_DELAY_SECONDS)
    val remoteHangupSeconds by settings.intFlow(SettingsRepository.REMOTE_HANGUP_SECONDS, SettingsRepository.DEFAULT_REMOTE_HANGUP_SECONDS).collectAsStateWithLifecycle(SettingsRepository.DEFAULT_REMOTE_HANGUP_SECONDS)
    val mediaRows = listOf(
        Triple("通话背景图", SettingsRepository.BACKGROUND_URI, arrayOf("image/*")), Triple("铃声", SettingsRepository.RINGTONE_URI, arrayOf("audio/*")),
        Triple("彩铃音频", SettingsRepository.RINGBACK_AUDIO_URI, arrayOf("audio/*")), Triple("彩铃视频", SettingsRepository.RINGBACK_VIDEO_URI, arrayOf("video/*")),
        Triple("拨号中语音条", SettingsRepository.PROMPT_CALLING_URI, arrayOf("audio/*")), Triple("通话中语音条", SettingsRepository.PROMPT_CONNECTED_URI, arrayOf("audio/*")),
        Triple("无法接通语音条", SettingsRepository.PROMPT_UNREACHABLE_URI, arrayOf("audio/*")), Triple("用户正忙语音条", SettingsRepository.PROMPT_BUSY_URI, arrayOf("audio/*")),
        Triple("通话结束语音条", SettingsRepository.PROMPT_ENDED_URI, arrayOf("audio/*")),
    )
    Column(Modifier.fillMaxSize().background(Color(0xFFF7F7F7)).statusBarsPadding().navigationBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(64.dp).background(Color.White).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, "返回", modifier = Modifier.size(26.dp).clickable(onClick = onBack))
            Text("媒体与模拟状态设置", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 15.dp))
        }
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("从剪贴板自动填入号码", fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Switch(clipboardAuto, { scope.launch { settings.setBoolean(SettingsRepository.CLIPBOARD_AUTO_FILL, it) } })
            }
            HorizontalDivider()
            Text("通话时序", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            DurationSettingRow("自动接通等待", connectDelaySeconds, 2, 30) { scope.launch { settings.setInt(SettingsRepository.CONNECT_DELAY_SECONDS, it) } }
            DurationSettingRow("对方自动挂断", remoteHangupSeconds, 1, 120) { scope.launch { settings.setInt(SettingsRepository.REMOTE_HANGUP_SECONDS, it) } }
            Text("彩铃固定在拨出 2 秒后开始；对方自动挂断时长从接通后开始计算。", color = SecondaryInk, fontSize = 12.sp)
            HorizontalDivider()
            Text("下一次模拟结果", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            listOf("CONNECTED" to "正常接通", "REMOTE_HANGUP" to "接通后对方挂断", "BUSY" to "用户正忙", "UNREACHABLE" to "无法接通").forEach { (value, label) ->
                Button({ scope.launch { settings.setString(SettingsRepository.NEXT_OUTCOME, value) } }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = if (nextOutcome == value) DialerGreen else Color(0xFF888888))) { Text(label) }
            }
            HorizontalDivider()
            Text("本地媒体", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            mediaRows.forEach { (label, key, types) -> MediaSettingRow(label, settings, key) { onPick(key, types) } }
            TextButton({ scope.launch { settings.clearMedia() } }, Modifier.fillMaxWidth()) { Text("恢复全部默认媒体", color = HangupRed) }
            Text("所有文件通过系统文件选择器导入；资源失效时自动使用默认背景和提示。", color = SecondaryInk, fontSize = 12.sp)
        }
    }
}

@Composable
private fun DurationSettingRow(label: String, value: Int, minimum: Int, maximum: Int, onValueChange: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth().height(52.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = MainInk, fontSize = 15.sp, modifier = Modifier.weight(1f))
        TextButton({ onValueChange((value - 1).coerceAtLeast(minimum)) }, enabled = value > minimum) { Text("−") }
        Text("${value} 秒", color = MainInk, fontSize = 15.sp, modifier = Modifier.width(56.dp))
        TextButton({ onValueChange((value + 1).coerceAtMost(maximum)) }, enabled = value < maximum) { Text("＋") }
    }
}

@Composable
private fun MediaSettingRow(label: String, settings: SettingsRepository, key: Preferences.Key<String>, onPick: () -> Unit) {
    val scope = rememberCoroutineScope()
    val value by settings.stringFlow(key).collectAsStateWithLifecycle(initialValue = null)
    Row(Modifier.fillMaxWidth().height(52.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) { Text(label, color = MainInk, fontSize = 15.sp); Text(if (value.isNullOrBlank()) "使用默认" else "已设置", color = SecondaryInk, fontSize = 11.sp) }
        if (!value.isNullOrBlank()) TextButton({ scope.launch { settings.setString(key, null) } }) { Text("清除") }
        Button(onPick) { Text("选择文件") }
    }
}

private fun callStateText(session: CallSessionSnapshot): String = when (session.phase) {
    CallPhase.DIALING -> "正在拨号。"
    CallPhase.CONNECTED -> formatDuration(session.callElapsedMs)
    CallPhase.SELF_HANGING_UP -> "正在挂断…"
    CallPhase.REMOTE_ENDED, CallPhase.ENDED -> "通话结束"
    CallPhase.UNREACHABLE -> "无法接通"
    CallPhase.BUSY -> "用户正忙"
    CallPhase.IDLE -> ""
}

private fun detectClipboardPhone(text: String): String? {
    if (text.length > 40 || text.any(Char::isLetter)) return null
    val normalized = normalizePhoneNumber(text)
    return normalized.takeIf { it.length in 7..15 && text.count(Char::isDigit) >= it.length }
}

private fun takeReadPermission(context: Context, uri: Uri) {
    runCatching { context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) }
}

private fun queryDisplayName(context: Context, uri: Uri): String? = runCatching {
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { if (it.moveToFirst()) it.getString(0) else null }
}.getOrNull()

private fun loadImage(context: Context, uri: Uri): androidx.compose.ui.graphics.ImageBitmap? = runCatching {
    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
    else context.contentResolver.openInputStream(uri)?.use(BitmapFactory::decodeStream)
    bitmap?.asImageBitmap()
}.getOrNull()

private fun displayHistoryTime(timeMillis: Long): String {
    if (timeMillis <= 0) return "示例"
    val elapsed = System.currentTimeMillis() - timeMillis
    return when { elapsed < 60_000 -> "刚刚"; elapsed < 3_600_000 -> "${elapsed / 60_000}分钟前"; elapsed < 86_400_000 -> "${elapsed / 3_600_000}小时前"; else -> "昨天" }
}
