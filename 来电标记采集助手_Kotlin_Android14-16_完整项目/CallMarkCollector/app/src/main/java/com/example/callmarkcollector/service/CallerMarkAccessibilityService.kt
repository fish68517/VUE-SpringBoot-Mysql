package com.example.callmarkcollector.service

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.content.ContextCompat
import com.example.callmarkcollector.data.CaptureRepository
import com.example.callmarkcollector.model.CallMarkRecord
import com.example.callmarkcollector.network.CallRecordUploader
import com.example.callmarkcollector.parser.CallScreenParser
import com.example.callmarkcollector.util.LogTags

class CallerMarkAccessibilityService : AccessibilityService() {
    private val handler = Handler(Looper.getMainLooper())
    private var phoneState = TelephonyManager.CALL_STATE_IDLE
    private var telephonyCallback: IncomingCallCallback? = null
    private var callbackRegistered = false

    private var pendingPackage = "unknown"
    private var pendingAttempt = 0
    private var sessionUploaded = false
    private var activeNumber: String? = null
    private var lastRelevantEventAt = 0L

    private val scanRunnable = Runnable { scanCurrentWindow() }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(LogTags.CAPTURE, "无障碍服务已连接")
        ensureTelephonyCallback()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        ensureTelephonyCallback()

        val packageName = event.packageName?.toString().orEmpty()
        if (packageName == this.packageName) return

        val now = System.currentTimeMillis()
        val likelyCallUi = isLikelyCallUi(event, packageName)
        val isRinging = phoneState == TelephonyManager.CALL_STATE_RINGING
        if (!isRinging && !likelyCallUi) return

        if (now - lastRelevantEventAt > SESSION_GAP_MS) resetCaptureSession("来电界面事件间隔超时")
        lastRelevantEventAt = now
        pendingPackage = packageName.ifBlank { "unknown" }

        Log.d(
            LogTags.CAPTURE,
            "收到候选事件 type=${event.eventType}, package=$pendingPackage, ringing=$isRinging, likelyCallUi=$likelyCallUi"
        )
        scheduleScan(FIRST_SCAN_DELAY_MS)
    }

    private fun scheduleScan(delayMs: Long) {
        handler.removeCallbacks(scanRunnable)
        handler.postDelayed(scanRunnable, delayMs)
    }

    private fun scanCurrentWindow() {
        val root = rootInActiveWindow
        if (root == null) {
            Log.w(LogTags.CAPTURE, "rootInActiveWindow 为空，等待下一次窗口事件")
            return
        }

        val lines = LinkedHashSet<String>()
        collectVisibleText(root, lines, 0)
        val textLines = lines.toList()
        Log.d(LogTags.CAPTURE, "窗口文本节点(${textLines.size})=$textLines")

        val parsed = CallScreenParser.parse(textLines)
        if (parsed == null) {
            Log.d(LogTags.PARSER, "本次扫描未提取到有效来电号码")
            if (pendingAttempt < MAX_RETRY_COUNT) {
                pendingAttempt++
                scheduleScan(RETRY_SCAN_DELAY_MS)
            }
            return
        }

        if (activeNumber != parsed.phoneNumber) {
            activeNumber = parsed.phoneNumber
            sessionUploaded = false
            pendingAttempt = 0
        }

        Log.i(
            LogTags.PARSER,
            "解析结果 number=${parsed.phoneNumber}, marked=${parsed.isMarked}, count=${parsed.markCount}, type=${parsed.markType}"
        )

        // 标记信息有时比号码晚出现。未标记结果会额外等待两轮，避免过早上传半成品。
        if (!parsed.isMarked && pendingAttempt < MAX_RETRY_COUNT) {
            pendingAttempt++
            scheduleScan(RETRY_SCAN_DELAY_MS)
            return
        }

        if (sessionUploaded) {
            Log.d(LogTags.CAPTURE, "当前来电会话已经采集，跳过重复窗口事件")
            return
        }

        val record = CallMarkRecord(
            installationId = CaptureRepository.getInstallationId(this),
            phoneNumber = parsed.phoneNumber,
            isMarked = parsed.isMarked,
            markCount = parsed.markCount,
            markType = parsed.markType,
            sourcePackage = pendingPackage,
            rawTextLines = parsed.rawTextLines
        )
        sessionUploaded = true
        CaptureRepository.saveRecord(this, record, "已采集，正在进入上传流程")
        Log.i(LogTags.CAPTURE, "采集完成 recordId=${record.recordId}")

        // 每形成一条采集记录，都调用一次上传入口。
        CallRecordUploader.upload(this, record)
    }

    private fun collectVisibleText(
        node: AccessibilityNodeInfo,
        output: LinkedHashSet<String>,
        depth: Int
    ) {
        if (depth > MAX_NODE_DEPTH || output.size >= MAX_TEXT_LINES) return

        node.text?.toString()?.trim()?.takeIf { it.isNotBlank() }?.let {
            output += it.take(MAX_LINE_LENGTH)
        }
        node.contentDescription?.toString()?.trim()?.takeIf { it.isNotBlank() }?.let {
            output += it.take(MAX_LINE_LENGTH)
        }
        for (index in 0 until node.childCount) {
            node.getChild(index)?.let { child -> collectVisibleText(child, output, depth + 1) }
            if (output.size >= MAX_TEXT_LINES) break
        }
    }

    private fun isLikelyCallUi(event: AccessibilityEvent, packageName: String): Boolean {
        val lowerPackage = packageName.lowercase()
        val lowerClass = event.className?.toString()?.lowercase().orEmpty()
        val packageHint = CALL_PACKAGE_HINTS.any { lowerPackage.contains(it) }
        val classHint = CALL_CLASS_HINTS.any { lowerClass.contains(it) }
        val eventText = buildString {
            event.text.forEach { append(it).append(' ') }
            event.contentDescription?.let { append(it) }
        }
        val textHint = CALL_TEXT_HINTS.any { eventText.contains(it, ignoreCase = true) }
        val systemCallSurface = lowerPackage.contains("systemui") || lowerClass.contains("notification")
        return textHint && (packageHint || classHint || systemCallSurface)
    }

    private fun ensureTelephonyCallback() {
        if (callbackRegistered) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(LogTags.CAPTURE, "READ_PHONE_STATE 未授权，使用来电界面特征作为降级判断")
            return
        }

        val manager = getSystemService(TelephonyManager::class.java)
        val callback = IncomingCallCallback()
        manager.registerTelephonyCallback(mainExecutor, callback)
        telephonyCallback = callback
        callbackRegistered = true
        Log.i(LogTags.CAPTURE, "电话状态监听已注册")
    }

    private inner class IncomingCallCallback : TelephonyCallback(), TelephonyCallback.CallStateListener {
        override fun onCallStateChanged(state: Int) {
            phoneState = state
            Log.i(LogTags.CAPTURE, "电话状态变化 state=$state")
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> resetCaptureSession("检测到新来电")
                TelephonyManager.CALL_STATE_IDLE -> resetCaptureSession("电话恢复空闲")
                TelephonyManager.CALL_STATE_OFFHOOK -> Unit
            }
        }
    }

    private fun resetCaptureSession(reason: String) {
        handler.removeCallbacks(scanRunnable)
        pendingAttempt = 0
        sessionUploaded = false
        activeNumber = null
        Log.d(LogTags.CAPTURE, "重置采集会话：$reason")
    }

    override fun onInterrupt() {
        Log.w(LogTags.CAPTURE, "无障碍服务被系统中断")
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        if (callbackRegistered) {
            telephonyCallback?.let {
                getSystemService(TelephonyManager::class.java).unregisterTelephonyCallback(it)
            }
        }
        callbackRegistered = false
        telephonyCallback = null
        Log.i(LogTags.CAPTURE, "无障碍服务已销毁")
        super.onDestroy()
    }

    companion object {
        private const val FIRST_SCAN_DELAY_MS = 650L
        private const val RETRY_SCAN_DELAY_MS = 900L
        private const val SESSION_GAP_MS = 20_000L
        private const val MAX_RETRY_COUNT = 2
        private const val MAX_NODE_DEPTH = 30
        private const val MAX_TEXT_LINES = 80
        private const val MAX_LINE_LENGTH = 200

        private val CALL_PACKAGE_HINTS = listOf("dialer", "incall", "telecom", "phone")
        private val CALL_CLASS_HINTS = listOf("incall", "incomingcall", "callactivity")
        private val CALL_TEXT_HINTS = listOf("来电", "接听", "拒绝", "incoming call", "answer", "decline")
    }
}
