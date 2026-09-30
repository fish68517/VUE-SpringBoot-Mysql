package com.example.callmarkcollector.service

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.telecom.TelecomManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.core.content.ContextCompat
import com.example.callmarkcollector.data.CaptureRepository
import com.example.callmarkcollector.model.CallMarkRecord
import com.example.callmarkcollector.model.ParsedCallInfo
import com.example.callmarkcollector.network.CallRecordUploader
import com.example.callmarkcollector.util.LogTags
import java.util.UUID

class CallerMarkAccessibilityService : AccessibilityService() {
    private val handler = Handler(Looper.getMainLooper())
    private data class Session(
        val id: String, val startedAt: Long, val deadline: Long,
        var finished: Boolean = false, var best: CaptureCandidate? = null
    )
    private var session: Session? = null
    private var gate = RingGate()
    private lateinit var reader: CallWindowReader
    private val callbacks = linkedMapOf<Int, Pair<TelephonyManager, IncomingCallCallback>>()
    private var registrationGeneration = 0
    private var connected = false
    private var scanScheduled = false
    private var eventWindowId: Int? = null
    private var subscriptionListener: SubscriptionManager.OnSubscriptionsChangedListener? = null
    private val scanRunnable = Runnable {
        scanScheduled = false
        scan()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        if (connected) return
        connected = true
        restoreSession()
        gate = RingGate(initialRinging = session != null)
        reader = CallWindowReader(this) {
            runCatching { getSystemService(TelecomManager::class.java).defaultDialerPackage }.getOrNull()
        }
        CallRecordUploader.initialize(this)
        val listener = object : SubscriptionManager.OnSubscriptionsChangedListener() {
            override fun onSubscriptionsChanged() { if (connected) ensureTelephonyCallbacks() }
        }
        subscriptionListener = listener
        runCatching {
            getSystemService(SubscriptionManager::class.java).addOnSubscriptionsChangedListener(mainExecutor, listener)
        }.onFailure { Log.w(LogTags.CAPTURE, "订阅变化监听不可用", it) }
        ensureTelephonyCallbacks()
        Log.i(LogTags.CAPTURE, "无障碍服务已连接；每次响铃建立一个采集会话")
    }

    private fun restoreSession() {
        val active = CaptureRepository.activeSession(this) ?: return
        val startedAt = active.getLong("startedAt")
        val remaining = (CAPTURE_WINDOW_MS - (System.currentTimeMillis() - startedAt)).coerceIn(0, CAPTURE_WINDOW_MS)
        val restored = Session(active.getString("recordId"), startedAt, SystemClock.elapsedRealtime() + remaining,
            active.optBoolean("finished"))
        val record = CaptureRepository.getRecord(this, restored.id)
        if (record != null && !record.isNull("phoneNumber")) {
            val raw = record.optJSONArray("rawTextLines")
            restored.best = CaptureCandidate(
                ParsedCallInfo(record.getString("phoneNumber"),
                    if (record.isNull("isMarked")) null else record.getBoolean("isMarked"),
                    if (record.isNull("markCount")) null else record.getInt("markCount"),
                    if (record.isNull("markType")) null else record.getString("markType"),
                    if (raw == null) emptyList() else (0 until raw.length()).map { raw.getString(it) }),
                record.optString("sourcePackage", "unknown"), record.optInt("sourceWindowId", -1), 100
            )
        }
        session = restored
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!connected || event == null) return
        ensureTelephonyCallbacks()
        if (event.packageName?.toString() == packageName || !gate.ready || !gate.ringing) return
        eventWindowId = event.windowId
        Log.d(LogTags.CAPTURE, "event type=" + event.eventType + ", package=" + event.packageName +
            ", class=" + event.className + ", window=" + event.windowId)
        // Do not remove an existing task: rapid UI events cannot keep postponing the scan.
        scheduleScan(100)
    }

    private fun ensureTelephonyCallbacks() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (callbacks.isNotEmpty()) {
                finishSession("电话状态权限被撤销")
                unregisterCallbacks()
            }
            return
        }
        val ids = runCatching {
            getSystemService(SubscriptionManager::class.java).activeSubscriptionInfoList
                ?.map { it.subscriptionId }?.toSet().orEmpty()
        }.getOrDefault(emptySet()).ifEmpty { setOf(SubscriptionManager.DEFAULT_SUBSCRIPTION_ID) }
        if (callbacks.keys == ids) return
        unregisterCallbacks()
        gate.watch(ids)
        val generation = registrationGeneration
        ids.forEach { id ->
            val manager = getSystemService(TelephonyManager::class.java).let {
                if (id == SubscriptionManager.DEFAULT_SUBSCRIPTION_ID) it else it.createForSubscriptionId(id)
            }
            val callback = IncomingCallCallback(id, generation)
            try {
                manager.registerTelephonyCallback(mainExecutor, callback)
                callbacks[id] = manager to callback
            } catch (error: SecurityException) {
                Log.e(LogTags.CAPTURE, "无法监听电话状态 subscription=" + id, error)
            } catch (error: IllegalStateException) {
                Log.e(LogTags.CAPTURE, "电话状态监听不可用 subscription=" + id, error)
            }
        }
        // Unregistered subscriptions must not leave the initial-callback barrier waiting forever.
        if (callbacks.keys != ids) gate.watch(callbacks.keys.toSet())
    }

    private inner class IncomingCallCallback(private val subscriptionId: Int, private val generation: Int) :
        TelephonyCallback(), TelephonyCallback.CallStateListener {
        override fun onCallStateChanged(state: Int) {
            if (!connected || generation != registrationGeneration) return
            Log.i(LogTags.CAPTURE, "电话状态 subscription=" + subscriptionId + ", state=" + state)
            when (gate.update(subscriptionId, state)) {
                RingGate.Change.START -> beginSession()
                RingGate.Change.END -> {
                    finishSession("响铃结束")
                    CaptureRepository.endSession(this@CallerMarkAccessibilityService)
                    session = null
                    eventWindowId = null
                }
                null -> Unit
            }
            // Resume a persisted session after initial state callbacks arrive.
            if (gate.ready && gate.ringing) scheduleScan(100)
        }
    }

    private fun beginSession() {
        cancelScan()
        eventWindowId = null
        val current = Session(UUID.randomUUID().toString(), System.currentTimeMillis(),
            SystemClock.elapsedRealtime() + CAPTURE_WINDOW_MS)
        session = current
        CaptureRepository.beginSession(this, toRecord(current, "SCANNING", "响铃已触发，等待来电窗口"))
        Log.i(LogTags.CAPTURE, "开始采集 recordId=" + current.id)
        scheduleScan(100)
    }

    private fun scheduleScan(delayMs: Long) {
        if (scanScheduled || session == null || session?.finished == true || !gate.ready || !gate.ringing) return
        scanScheduled = true
        handler.postDelayed(scanRunnable, delayMs)
    }

    private fun cancelScan() {
        handler.removeCallbacks(scanRunnable)
        scanScheduled = false
    }

    private fun scan() {
        val current = session ?: return
        if (current.finished || !gate.ready || !gate.ringing) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            finishSession("电话状态权限被撤销")
            return
        }
        runCatching { reader.scan(eventWindowId) }.onSuccess { candidates ->
            val candidate = candidates.maxByOrNull { it.confidence }
            if (candidate != null) {
                val combined = current.best?.merge(candidate) ?: candidate
                if (current.best != combined) {
                    current.best = combined
                    CaptureRepository.updateCapture(this, toRecord(current, "SCANNING", "等待字段补全"), false)
                }
            }
        }.onFailure { Log.w(LogTags.CAPTURE, "窗口暂不可读，将继续重试", it) }
        // Collect for a bounded window even if a type appears early, so late counts can be merged.
        if (SystemClock.elapsedRealtime() >= current.deadline) {
            finishSession("采集窗口结束")
        } else {
            scheduleScan(minOf(SCAN_INTERVAL_MS, current.deadline - SystemClock.elapsedRealtime()).coerceAtLeast(1))
        }
    }

    private fun finishSession(reason: String) {
        val current = session ?: return
        if (current.finished) return
        current.finished = true
        cancelScan()
        val status = if (current.best == null) "NO_NUMBER" else "CAPTURED"
        val detail = if (current.best == null) reason + "；未读取到可信来电号码，字段保持未知" else reason
        val saved = CaptureRepository.updateCapture(this, toRecord(current, status, detail), true)
        if (saved) CallRecordUploader.upload(this, current.id)
        Log.i(LogTags.CAPTURE, "采集完成 recordId=" + current.id + ", status=" + status + ", retained=" + saved)
    }

    private fun toRecord(current: Session, status: String, reason: String): CallMarkRecord {
        val candidate = current.best
        return CallMarkRecord(
            recordId = current.id,
            installationId = CaptureRepository.getInstallationId(this),
            phoneNumber = candidate?.info?.phoneNumber,
            isMarked = candidate?.info?.isMarked,
            markCount = candidate?.info?.markCount,
            markType = candidate?.info?.markType,
            sourcePackage = candidate?.packageName ?: "unknown",
            capturedAt = current.startedAt,
            rawTextLines = candidate?.info?.rawTextLines.orEmpty(),
            captureStatus = status,
            captureReason = reason,
            sourceWindowId = candidate?.windowId
        )
    }

    private fun unregisterCallbacks() {
        registrationGeneration++
        callbacks.values.forEach { (manager, callback) ->
            runCatching { manager.unregisterTelephonyCallback(callback) }
        }
        callbacks.clear()
    }

    override fun onInterrupt() {
        Log.w(LogTags.CAPTURE, "无障碍反馈被中断")
    }

    override fun onDestroy() {
        finishSession("采集服务停止")
        connected = false
        cancelScan()
        unregisterCallbacks()
        subscriptionListener?.let {
            runCatching { getSystemService(SubscriptionManager::class.java).removeOnSubscriptionsChangedListener(it) }
        }
        super.onDestroy()
    }

    companion object {
        private const val CAPTURE_WINDOW_MS = 8_000L
        private const val SCAN_INTERVAL_MS = 600L
    }
}
