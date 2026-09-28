package com.example.dialerreplica.media

import androidx.datastore.preferences.core.Preferences
import com.example.dialerreplica.data.SettingsRepository

/** assets 根目录下的八种模拟状态提示音。文件名区分大小写。 */
object SimulationVoiceResources {
    val files: Map<Preferences.Key<String>, String> = mapOf(
        // Preferences.Key.to() 返回 DataStore Pair，不能用于 mapOf；显式构造 Kotlin Pair。
        kotlin.Pair(SettingsRepository.PROMPT_CONNECTED_URI, "simulation_connected.mp3"),
        kotlin.Pair(SettingsRepository.PROMPT_BUSY_URI, "simulation_busy.mp3"),
        kotlin.Pair(SettingsRepository.PROMPT_NO_ANSWER_URI, "simulation_no_answer.mp3"),
        kotlin.Pair(SettingsRepository.PROMPT_SUSPENDED_URI, "simulation_suspended.mp3"),
        kotlin.Pair(SettingsRepository.PROMPT_UNREACHABLE_URI, "simulation_unreachable.mp3"),
        kotlin.Pair(SettingsRepository.PROMPT_VOICEMAIL_URI, "simulation_voicemail.mp3"),
        kotlin.Pair(SettingsRepository.PROMPT_POWERED_OFF_URI, "simulation_powered_off.mp3"),
        kotlin.Pair(SettingsRepository.PROMPT_REJECTED_URI, "simulation_rejected.mp3"),
    )

    fun uri(key: Preferences.Key<String>): String? {
        val fileName: String = files[key] ?: return null
        return "asset:///$fileName"
    }
}
