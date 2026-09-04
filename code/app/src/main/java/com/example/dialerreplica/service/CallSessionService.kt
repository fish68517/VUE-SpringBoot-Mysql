package com.example.dialerreplica.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.example.dialerreplica.MainActivity
import com.example.dialerreplica.R
import com.example.dialerreplica.data.AppDatabase
import com.example.dialerreplica.data.CallRecordEntity
import com.example.dialerreplica.data.NumberAttributionRepository
import com.example.dialerreplica.data.SettingsRepository
import com.example.dialerreplica.data.formatPhoneNumber
import com.example.dialerreplica.data.normalizePhoneNumber
import com.example.dialerreplica.media.CallRecorder
import com.example.dialerreplica.media.LocalMediaPlayer
import com.example.dialerreplica.model.CallEndReason
import com.example.dialerreplica.model.CallPhase
import com.example.dialerreplica.model.CallSessionSnapshot
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object CallSessionBus {
    private val mutableSnapshot = MutableStateFlow(CallSessionSnapshot())
    val snapshot = mutableSnapshot.asStateFlow()
    internal fun update(value: CallSessionSnapshot) { mutableSnapshot.value = value }
}

class CallSessionService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var recorder: CallRecorder
    private lateinit var player: LocalMediaPlayer
    private lateinit var settings: SettingsRepository
    private lateinit var attribution: NumberAttributionRepository
    private var tickerJob: Job? = null
    private var transitionJob: Job? = null
    private var dialStartedElapsed = 0L
    private var connectedElapsed = 0L
    private var recordingStartedElapsed = 0L
    private var dialStartedWall = 0L
    private var connectedWall: Long? = null
    private var lastRecordingUri: String? = null
    private var lastRecordingName: String? = null
    private var lastRecordingDuration = 0L

    override fun onCreate() {
        super.onCreate()
        recorder = CallRecorder(this)
        player = LocalMediaPlayer(this)
        settings = SettingsRepository(this)
        attribution = NumberAttributionRepository(this)
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startSession(intent.getStringExtra(EXTRA_NUMBER).orEmpty())
            ACTION_TOGGLE_RECORD -> toggleRecording()
            ACTION_TOGGLE_ACTION -> toggleAction(intent.getStringExtra(EXTRA_ACTION_NAME).orEmpty())
            ACTION_LOCAL_HANGUP -> requestEnd(CallEndReason.LOCAL_HANGUP)
            ACTION_REMOTE_HANGUP -> requestEnd(CallEndReason.REMOTE_HANGUP)
            ACTION_BUSY -> requestEnd(CallEndReason.BUSY)
            ACTION_UNREACHABLE -> requestEnd(CallEndReason.UNREACHABLE)
        }
        return START_NOT_STICKY
    }

    private fun startSession(raw: String) {
        val number = normalizePhoneNumber(raw)
        if (number.isBlank()) return
        transitionJob?.cancel()
        tickerJob?.cancel()
        player.stop()
        recorder.release()
        dialStartedElapsed = SystemClock.elapsedRealtime()
        dialStartedWall = System.currentTimeMillis()
        connectedElapsed = 0
        connectedWall = null
        recordingStartedElapsed = 0
        lastRecordingUri = null
        lastRecordingName = null
        lastRecordingDuration = 0
        val location = attribution.lookup(number).displayText
        CallSessionBus.update(
            CallSessionSnapshot(
                sessionId = UUID.randomUUID().toString(),
                rawNumber = number,
                formattedNumber = formatPhoneNumber(number),
                location = location,
                phase = CallPhase.DIALING,
            ),
        )
        promote(recording = false)
        startTicker()
        scope.launch {
            settings.getString(SettingsRepository.PROMPT_CALLING_URI)?.let { player.play(it) }
            if (settings.getString(SettingsRepository.RINGBACK_VIDEO_URI).isNullOrBlank()) {
                settings.getString(SettingsRepository.RINGBACK_AUDIO_URI)?.let { player.play(it, looping = true) }
            }
        }
        transitionJob = scope.launch {
            delay(3_000)
            when (settings.getString(SettingsRepository.NEXT_OUTCOME) ?: "CONNECTED") {
                "BUSY" -> requestEnd(CallEndReason.BUSY)
                "UNREACHABLE" -> requestEnd(CallEndReason.UNREACHABLE)
                "REMOTE_HANGUP" -> {
                    connect()
                    delay(6_000)
                    requestEnd(CallEndReason.REMOTE_HANGUP)
                }
                else -> connect()
            }
        }
    }

    private fun connect() {
        val current = CallSessionBus.snapshot.value
        if (current.phase != CallPhase.DIALING) return
        player.stop()
        connectedElapsed = SystemClock.elapsedRealtime()
        connectedWall = System.currentTimeMillis()
        CallSessionBus.update(current.copy(phase = CallPhase.CONNECTED, callElapsedMs = 0))
        scope.launch { settings.getString(SettingsRepository.PROMPT_CONNECTED_URI)?.let { player.play(it) } }
        promote(recording = false)
    }

    private fun toggleAction(actionName: String) {
        val current = CallSessionBus.snapshot.value
        if (current.phase != CallPhase.CONNECTED) return
        CallSessionBus.update(current.copy(activeAction = if (current.activeAction == actionName) null else actionName))
        updateNotification()
    }

    private fun toggleRecording() {
        val current = CallSessionBus.snapshot.value
        if (current.phase != CallPhase.CONNECTED) return
        if (recorder.isRecording) {
            val result = recorder.stop()
            if (result != null) {
                lastRecordingUri = Uri.fromFile(result.file).toString()
                lastRecordingName = result.file.name
                lastRecordingDuration = result.durationMs
            }
            CallSessionBus.update(current.copy(recording = false, recordingElapsedMs = lastRecordingDuration, recordingPath = lastRecordingUri))
            promote(recording = false)
            return
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) return
        recorder.start(current.sessionId).onSuccess { file ->
            recordingStartedElapsed = SystemClock.elapsedRealtime()
            lastRecordingUri = Uri.fromFile(file).toString()
            lastRecordingName = file.name
            CallSessionBus.update(current.copy(recording = true, recordingElapsedMs = 0, recordingPath = lastRecordingUri))
            promote(recording = true)
        }
    }

    private fun requestEnd(reason: CallEndReason) {
        val current = CallSessionBus.snapshot.value
        if (current.phase == CallPhase.IDLE || current.phase == CallPhase.ENDED ||
            current.phase == CallPhase.SELF_HANGING_UP || current.phase == CallPhase.REMOTE_ENDED
        ) return
        transitionJob?.cancel()
        player.stop()
        recorder.stop()?.let { result ->
            lastRecordingUri = Uri.fromFile(result.file).toString()
            lastRecordingName = result.file.name
            lastRecordingDuration = result.durationMs
        }
        val phase = when (reason) {
            CallEndReason.LOCAL_HANGUP -> CallPhase.SELF_HANGING_UP
            CallEndReason.REMOTE_HANGUP -> CallPhase.REMOTE_ENDED
            CallEndReason.BUSY -> CallPhase.BUSY
            CallEndReason.UNREACHABLE -> CallPhase.UNREACHABLE
            CallEndReason.INTERRUPTED -> CallPhase.ENDED
        }
        CallSessionBus.update(current.copy(phase = phase, recording = false, recordingPath = lastRecordingUri, recordingElapsedMs = lastRecordingDuration))
        promote(recording = false)
        scope.launch {
            val promptKey = when (reason) {
                CallEndReason.BUSY -> SettingsRepository.PROMPT_BUSY_URI
                CallEndReason.UNREACHABLE -> SettingsRepository.PROMPT_UNREACHABLE_URI
                else -> SettingsRepository.PROMPT_ENDED_URI
            }
            settings.getString(promptKey)?.let { player.play(it) }
        }
        transitionJob = scope.launch {
            delay(if (reason == CallEndReason.LOCAL_HANGUP) 750 else 1_300)
            finishSession(reason)
        }
    }

    private suspend fun finishSession(reason: CallEndReason) {
        val current = CallSessionBus.snapshot.value
        val endedAt = System.currentTimeMillis()
        val duration = if (connectedElapsed > 0) SystemClock.elapsedRealtime() - connectedElapsed else 0
        AppDatabase.get(this).callRecordDao().insert(
            CallRecordEntity(
                rawNumber = current.rawNumber,
                formattedNumber = current.formattedNumber,
                location = current.location,
                result = reason.name,
                dialStartedAt = dialStartedWall,
                connectedAt = connectedWall,
                endedAt = endedAt,
                durationMs = duration,
                recordingUri = lastRecordingUri,
                recordingName = lastRecordingName,
                recordingDurationMs = lastRecordingDuration,
                recordingSource = if (lastRecordingUri == null) null else "APP",
            ),
        )
        tickerJob?.cancel()
        player.stop()
        CallSessionBus.update(current.copy(phase = CallPhase.ENDED, callElapsedMs = duration, recording = false))
        updateNotification()
        delay(250)
        CallSessionBus.update(CallSessionSnapshot())
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startTicker() {
        tickerJob = scope.launch {
            while (isActive) {
                val current = CallSessionBus.snapshot.value
                val now = SystemClock.elapsedRealtime()
                val callElapsed = if (connectedElapsed > 0) now - connectedElapsed else 0
                val recordingElapsed = if (current.recording && recordingStartedElapsed > 0) now - recordingStartedElapsed else current.recordingElapsedMs
                CallSessionBus.update(current.copy(callElapsedMs = callElapsed, recordingElapsedMs = recordingElapsed))
                updateNotification()
                delay(500)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "模拟通话", NotificationManager.IMPORTANCE_LOW).apply {
                description = "显示模拟通话状态、计时和录音状态"
                setSound(null, null)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun promote(recording: Boolean) {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var value = if (recording) ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE else 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                value = value or ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            }
            value
        } else 0
        ServiceCompat.startForeground(this, NOTIFICATION_ID, buildNotification(), type)
    }

    private fun updateNotification() {
        getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): Notification {
        val current = CallSessionBus.snapshot.value
        val openIntent = Intent(this, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 1, openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val hangupIntent = Intent(this, CallSessionService::class.java).setAction(ACTION_LOCAL_HANGUP)
        val hangupPendingIntent = PendingIntent.getService(this, 2, hangupIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val stateText = when (current.phase) {
            CallPhase.DIALING -> "正在拨号"
            CallPhase.CONNECTED -> "通话中 ${formatDuration(current.callElapsedMs)}" + if (current.recording) " · 正在录音" else ""
            CallPhase.SELF_HANGING_UP -> "正在挂断"
            CallPhase.REMOTE_ENDED, CallPhase.ENDED -> "通话结束"
            CallPhase.UNREACHABLE -> "无法接通"
            CallPhase.BUSY -> "用户正忙"
            CallPhase.IDLE -> "通话已结束"
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_phone)
            .setContentTitle(current.formattedNumber.ifBlank { "模拟通话" })
            .setContentText(stateText)
            .setContentIntent(contentIntent)
            .setOngoing(current.phase != CallPhase.IDLE && current.phase != CallPhase.ENDED)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .addAction(R.drawable.ic_notification_phone, "挂断", hangupPendingIntent)
            .build()
    }

    override fun onDestroy() {
        transitionJob?.cancel()
        tickerJob?.cancel()
        recorder.release()
        player.stop()
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val CHANNEL_ID = "simulated_call"
        private const val NOTIFICATION_ID = 4107
        private const val EXTRA_NUMBER = "number"
        private const val EXTRA_ACTION_NAME = "action_name"
        const val ACTION_START = "dialer.START"
        const val ACTION_TOGGLE_RECORD = "dialer.TOGGLE_RECORD"
        const val ACTION_TOGGLE_ACTION = "dialer.TOGGLE_ACTION"
        const val ACTION_LOCAL_HANGUP = "dialer.LOCAL_HANGUP"
        const val ACTION_REMOTE_HANGUP = "dialer.REMOTE_HANGUP"
        const val ACTION_BUSY = "dialer.BUSY"
        const val ACTION_UNREACHABLE = "dialer.UNREACHABLE"

        fun start(context: Context, number: String) = send(context, ACTION_START) { putExtra(EXTRA_NUMBER, number) }
        fun toggleRecording(context: Context) = send(context, ACTION_TOGGLE_RECORD)
        fun toggleAction(context: Context, name: String) = send(context, ACTION_TOGGLE_ACTION) { putExtra(EXTRA_ACTION_NAME, name) }
        fun localHangup(context: Context) = send(context, ACTION_LOCAL_HANGUP)
        fun remoteHangup(context: Context) = send(context, ACTION_REMOTE_HANGUP)
        fun markBusy(context: Context) = send(context, ACTION_BUSY)
        fun markUnreachable(context: Context) = send(context, ACTION_UNREACHABLE)

        private fun send(context: Context, action: String, configure: Intent.() -> Unit = {}) {
            val intent = Intent(context, CallSessionService::class.java).setAction(action).apply(configure)
            if (action == ACTION_START && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, intent)
            } else {
                context.startService(intent)
            }
        }
    }
}

fun formatDuration(milliseconds: Long): String {
    val totalSeconds = (milliseconds / 1000).coerceAtLeast(0)
    return "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}
