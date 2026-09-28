package com.example.dialerreplica

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dialerreplica.data.SettingsRepository
import kotlinx.coroutines.launch

private val simulationOptions = listOf(
    "CONNECTED" to "正常接通",
    "BUSY" to "正在通话中（自动播放提示音）",
    "NO_ANSWER" to "无人接听（超时播放提示音）",
    "SUSPENDED" to "暂停服务（自动播放提示音）",
    "UNREACHABLE" to "无法接通",
    "VOICEMAIL" to "语音留言",
    "POWERED_OFF" to "关机",
    "REJECTED" to "对方拒接",
)

@Composable
internal fun SimulationSettingsCard(settings: SettingsRepository) {
    val scope = rememberCoroutineScope()
    val stored by settings.stringFlow(SettingsRepository.NEXT_OUTCOME).collectAsStateWithLifecycle("CONNECTED")
    val outcome = if (stored == "REMOTE_HANGUP") "CONNECTED" else stored ?: "CONNECTED"
    val connect by settings.intFlow(SettingsRepository.CONNECT_DELAY_SECONDS, SettingsRepository.DEFAULT_CONNECT_DELAY_SECONDS)
        .collectAsStateWithLifecycle(SettingsRepository.DEFAULT_CONNECT_DELAY_SECONDS)
    val wait by settings.intFlow(SettingsRepository.REMOTE_HANGUP_SECONDS, SettingsRepository.DEFAULT_REMOTE_HANGUP_SECONDS)
        .collectAsStateWithLifecycle(SettingsRepository.DEFAULT_REMOTE_HANGUP_SECONDS)
    var expanded by rememberSaveable { mutableStateOf(true) }
    var menu by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).border(1.dp, Color(0xFFEAEAEA), RoundedCornerShape(16.dp))) {
        Row(Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("接听状态模拟", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(if (expanded) "⌃" else "⌄", color = Color.Gray, fontSize = 24.sp)
        }
        if (expanded) {
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().background(Color(0xFFF8F9FA), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFE1E1E1), RoundedCornerShape(8.dp)).clickable { menu = true }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(simulationOptions.firstOrNull { it.first == outcome }?.second ?: "正常接通", modifier = Modifier.weight(1f), fontSize = 16.sp)
                        Text("⌄")
                    }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }, modifier = Modifier.heightIn(max = 420.dp)) {
                        simulationOptions.forEach { (value, label) ->
                            DropdownMenuItem(text = { Text(label, modifier = Modifier.widthIn(max = 240.dp)) },
                                trailingIcon = { RadioButton(selected = outcome == value, onClick = null) },
                                onClick = { menu = false; scope.launch { settings.setString(SettingsRepository.NEXT_OUTCOME, value) } })
                        }
                    }
                }
                SimulationSecondsInput("响铃几秒后接通", connect, 2..30) {
                    scope.launch { settings.setInt(SettingsRepository.CONNECT_DELAY_SECONDS, it) }
                }
                SimulationSecondsInput("自动挂断/等待时长", wait, 1..120) {
                    scope.launch { settings.setInt(SettingsRepository.REMOTE_HANGUP_SECONDS, it) }
                }
                Text("单位：秒。正常接通后按第二项自动挂断；无人接听按第二项等待超时；其他状态按第一项触发。修改用于后续拨号。", color = Color.Gray, fontSize = 12.sp)
                Text("状态语音默认读取 assets 内置 MP3；下方选择的音频优先，失效则回退内置资源。两者均不可用时仅显示状态。彩铃仍在拨出 2 秒后开始。", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SimulationSecondsInput(label: String, value: Int, range: IntRange, onChange: (Int) -> Unit) {
    var input by remember(value) { mutableStateOf(value.toString()) }
    val valid = input.toIntOrNull()?.let { it in range } == true
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 15.sp, modifier = Modifier.weight(1f))
        OutlinedTextField(value = input, onValueChange = { text ->
            if (text.length <= 3 && text.all(Char::isDigit)) {
                input = text
                text.toIntOrNull()?.takeIf { it in range }?.let(onChange)
            }
        }, singleLine = true, modifier = Modifier.width(90.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = !valid, supportingText = if (!valid) ({ Text("${range.first}–${range.last}") }) else null)
    }
}
