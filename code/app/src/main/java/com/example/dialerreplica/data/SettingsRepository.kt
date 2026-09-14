package com.example.dialerreplica.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dialerSettings by preferencesDataStore(name = "dialer_settings")

class SettingsRepository(private val context: Context) {
    fun stringFlow(key: Preferences.Key<String>): Flow<String?> =
        context.dialerSettings.data.map { it[key] }

    fun booleanFlow(key: Preferences.Key<Boolean>, defaultValue: Boolean): Flow<Boolean> =
        context.dialerSettings.data.map { it[key] ?: defaultValue }

    fun intFlow(key: Preferences.Key<Int>, defaultValue: Int): Flow<Int> =
        context.dialerSettings.data.map { it[key] ?: defaultValue }

    fun ringbackVideoUrisFlow(): Flow<Set<String>> =
        context.dialerSettings.data.map { preferences ->
            preferences[RINGBACK_VIDEO_URIS].orEmpty().ifEmpty {
                preferences[RINGBACK_VIDEO_URI]?.let(::setOf).orEmpty()
            }
        }

    suspend fun getString(key: Preferences.Key<String>): String? =
        context.dialerSettings.data.first()[key]

    suspend fun getInt(key: Preferences.Key<Int>, defaultValue: Int): Int =
        context.dialerSettings.data.first()[key] ?: defaultValue

    suspend fun getRingbackVideoUris(): Set<String> {
        val preferences = context.dialerSettings.data.first()
        return preferences[RINGBACK_VIDEO_URIS].orEmpty().ifEmpty {
            preferences[RINGBACK_VIDEO_URI]?.let(::setOf).orEmpty()
        }
    }

    suspend fun setString(key: Preferences.Key<String>, value: String?) {
        context.dialerSettings.edit { preferences ->
            if (value == null) preferences.remove(key) else preferences[key] = value
        }
    }

    suspend fun setBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        context.dialerSettings.edit { it[key] = value }
    }

    suspend fun setInt(key: Preferences.Key<Int>, value: Int) {
        context.dialerSettings.edit { it[key] = value }
    }

    suspend fun setRingbackVideoUris(values: Collection<String>) {
        val uris = values.filter(String::isNotBlank).toSet()
        context.dialerSettings.edit { preferences ->
            if (uris.isEmpty()) {
                preferences.remove(RINGBACK_VIDEO_URIS)
                preferences.remove(RINGBACK_VIDEO_URI)
            } else {
                preferences[RINGBACK_VIDEO_URIS] = uris
                // 同步保留第一项，关闭随机功能常量后可直接回退到单首模式。
                preferences[RINGBACK_VIDEO_URI] = values.first(String::isNotBlank)
            }
        }
    }

    suspend fun setSingleRingbackVideoUri(value: String?) {
        context.dialerSettings.edit { preferences ->
            preferences.remove(RINGBACK_VIDEO_URIS)
            if (value.isNullOrBlank()) preferences.remove(RINGBACK_VIDEO_URI)
            else preferences[RINGBACK_VIDEO_URI] = value
        }
    }

    suspend fun clearMedia() {
        context.dialerSettings.edit { preferences ->
            MEDIA_KEYS.forEach { preferences.remove(it) }
            preferences.remove(RINGBACK_VIDEO_URIS)
        }
    }

    companion object {
        val CLIPBOARD_AUTO_FILL = booleanPreferencesKey("clipboard_auto_fill")
        val BACKGROUND_URI = stringPreferencesKey("default_background_uri")
        val RINGTONE_URI = stringPreferencesKey("default_ringtone_uri")
        val RINGBACK_AUDIO_URI = stringPreferencesKey("default_ringback_audio_uri")
        val RINGBACK_VIDEO_URI = stringPreferencesKey("default_ringback_video_uri")
        val RINGBACK_VIDEO_URIS = stringSetPreferencesKey("default_ringback_video_uris")
        val PROMPT_CALLING_URI = stringPreferencesKey("voice_prompt_calling_uri")
        val PROMPT_CONNECTED_URI = stringPreferencesKey("voice_prompt_connected_uri")
        val PROMPT_UNREACHABLE_URI = stringPreferencesKey("voice_prompt_unreachable_uri")
        val PROMPT_BUSY_URI = stringPreferencesKey("voice_prompt_busy_uri")
        val PROMPT_ENDED_URI = stringPreferencesKey("voice_prompt_ended_uri")
        val NEXT_OUTCOME = stringPreferencesKey("next_outcome")
        val CONNECT_DELAY_SECONDS = intPreferencesKey("connect_delay_seconds")
        val REMOTE_HANGUP_SECONDS = intPreferencesKey("remote_hangup_seconds")

        const val DEFAULT_CONNECT_DELAY_SECONDS = 3
        const val DEFAULT_REMOTE_HANGUP_SECONDS = 6

        // true：彩铃视频多选并在每次通话随机播放；false：沿用原有单首彩铃视频。
        const val ENABLE_RANDOM_RINGBACK_VIDEO = true

        val MEDIA_KEYS = listOf(
            BACKGROUND_URI,
            RINGTONE_URI,
            RINGBACK_AUDIO_URI,
            RINGBACK_VIDEO_URI,
            PROMPT_CALLING_URI,
            PROMPT_CONNECTED_URI,
            PROMPT_UNREACHABLE_URI,
            PROMPT_BUSY_URI,
            PROMPT_ENDED_URI,
        )
    }
}
