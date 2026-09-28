package com.example.dialerreplica

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dialerreplica.data.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
internal fun RingbackVideosScreen(settings: SettingsRepository, onBack: () -> Unit, onPick: () -> Unit) {
    BackHandler(onBack = onBack)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val enabled by settings.booleanFlow(SettingsRepository.RINGBACK_ENABLED, true).collectAsStateWithLifecycle(true)
    val values by settings.ringbackVideoUrisFlow().collectAsStateWithLifecycle(emptySet())
    var preview by remember { mutableStateOf<String?>(null) }
    var confirmClear by remember { mutableStateOf(false) }
    val green = Color(0xFF20B653)
    Column(Modifier.fillMaxSize().background(Color.White).statusBarsPadding().navigationBarsPadding().padding(16.dp)) {
        Row(Modifier.fillMaxWidth().height(52.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onBack) { Text("返回") }
            Text("彩铃视频", fontSize = 20.sp)
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("彩铃开关", fontSize = 18.sp, modifier = Modifier.weight(1f))
            Switch(enabled, { scope.launch { settings.setBoolean(SettingsRepository.RINGBACK_ENABLED, it) } },
                colors = SwitchDefaults.colors(checkedTrackColor = green))
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            val mode = if (SettingsRepository.ENABLE_RANDOM_RINGBACK_VIDEO) "随机播放" else "单首播放"
            val limit = SettingsRepository.MAX_RINGBACK_VIDEOS
            Text("当前：${values.size}/$limit 个 · $mode", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.weight(1f))
            TextButton({ confirmClear = true }, enabled = values.isNotEmpty()) { Text("清除彩铃", color = Color(0xFFCF6565)) }
        }
        Button(onPick, colors = ButtonDefaults.buttonColors(containerColor = green)) { Text("选择文件") }
        Text(
            "关闭后保留已保存文件，开关用于后续拨号的彩铃播放。" +
                if (SettingsRepository.ENABLE_RANDOM_RINGBACK_VIDEO) "最多保存 500 个，重复文件自动去重，超出上限的文件不再加入。" else "当前为单首模式，选择新文件会替换原有列表。",
            color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(vertical = 10.dp),
        )
        LazyColumn(Modifier.weight(1f).fillMaxWidth().background(Color(0xFFF0F6FF), RoundedCornerShape(8.dp))) {
            if (values.isEmpty()) item { Text("暂无彩铃，请选择视频文件", color = Color.Gray, modifier = Modifier.padding(20.dp)) }
            items(values.toList(), key = { it }) { uri ->
                val name by produceState("视频彩铃", uri) {
                    value = withContext(Dispatchers.IO) {
                        runCatching {
                            context.contentResolver.query(Uri.parse(uri), arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
                                if (it.moveToFirst()) it.getString(0) else null
                            }
                        }.getOrNull() ?: "视频文件（不可访问）"
                    }
                }
                Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextButton({ preview = uri }) { Text("▶ 试听", color = Color(0xFF2698D0)) }
                    Column(Modifier.weight(1f).padding(horizontal = 4.dp)) {
                        Text(name, color = Color(0xFF2A62A3), fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("已保存", color = Color(0xFF7CA8C8), fontSize = 12.sp)
                    }
                    TextButton({ scope.launch { settings.removeRingbackVideo(uri) } }) { Text("删除", color = Color(0xFFCF6565)) }
                }
                HorizontalDivider(color = Color(0xFFDDE7F2))
            }
        }
    }
    if (confirmClear) AlertDialog(
        onDismissRequest = { confirmClear = false },
        title = { Text("清除所有彩铃？") },
        text = { Text("仅移除应用中的列表，不删除手机里的原文件。") },
        confirmButton = { TextButton({ scope.launch { settings.setRingbackVideoUris(emptyList()) }; confirmClear = false }) { Text("清除") } },
        dismissButton = { TextButton({ confirmClear = false }) { Text("取消") } },
    )
    preview?.let { uri -> key(uri) { RingbackPreview(uri) { preview = null } } }
}

@Composable
private fun RingbackPreview(uri: String, onClose: () -> Unit) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var failed by remember { mutableStateOf(false) }
    val video = remember { VideoView(context) }
    DisposableEffect(video, lifecycle) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_PAUSE) video.pause() }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer); video.stopPlayback() }
    }
    Dialog(onDismissRequest = onClose) {
        Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).padding(12.dp)) {
            if (failed) Text("视频无法播放，请重新选择文件。", color = Color.Red)
            else AndroidView(factory = {
                video.apply {
                    setMediaController(MediaController(context).also { it.setAnchorView(this) })
                    setOnPreparedListener { start() }
                    setOnErrorListener { _, _, _ -> failed = true; true }
                    runCatching { setVideoURI(Uri.parse(uri)) }.onFailure { failed = true }
                }
            }, modifier = Modifier.fillMaxWidth().height(300.dp))
            TextButton(onClose, Modifier.align(Alignment.End)) { Text("关闭") }
        }
    }
}
