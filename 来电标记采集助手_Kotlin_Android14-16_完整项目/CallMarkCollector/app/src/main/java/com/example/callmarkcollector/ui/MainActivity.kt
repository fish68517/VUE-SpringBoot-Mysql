package com.example.callmarkcollector.ui

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.callmarkcollector.R
import com.example.callmarkcollector.data.CaptureRepository
import com.example.callmarkcollector.parser.CallScreenParser
import com.example.callmarkcollector.service.CallerMarkAccessibilityService
import com.example.callmarkcollector.util.LogTags
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var phoneStatus: TextView
    private lateinit var accessibilityStatus: TextView
    private lateinit var endpointEdit: EditText
    private lateinit var lastCaptureText: TextView

    private var receiverRegistered = false

    private val phonePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Log.i(LogTags.UI, "READ_PHONE_STATE permission result=$granted")
            if (!granted) {
                Toast.makeText(this, "未授权时仍会尝试通过来电界面特征识别，但准确性会下降", Toast.LENGTH_LONG).show()
            }
            refreshStatus()
        }

    private val captureReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            showLastCapture()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        phoneStatus = findViewById(R.id.text_phone_permission_status)
        accessibilityStatus = findViewById(R.id.text_accessibility_status)
        endpointEdit = findViewById(R.id.edit_endpoint)
        lastCaptureText = findViewById(R.id.text_last_capture)

        endpointEdit.setText(CaptureRepository.getEndpoint(this))

        findViewById<android.view.View>(R.id.button_phone_permission).setOnClickListener {
            if (hasPhonePermission()) {
                Toast.makeText(this, "电话状态权限已经授权", Toast.LENGTH_SHORT).show()
            } else {
                phonePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
            }
        }

        findViewById<android.view.View>(R.id.button_accessibility_settings).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        findViewById<android.view.View>(R.id.button_save_endpoint).setOnClickListener {
            saveEndpoint()
        }

        findViewById<android.view.View>(R.id.button_test_parser).setOnClickListener {
            runParserDemo()
        }

        refreshStatus()
        showLastCapture()
    }

    override fun onStart() {
        super.onStart()
        if (!receiverRegistered) {
            registerReceiver(
                captureReceiver,
                IntentFilter(CaptureRepository.ACTION_CAPTURE_UPDATED),
                RECEIVER_NOT_EXPORTED
            )
            receiverRegistered = true
        }
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
        showLastCapture()
    }

    override fun onStop() {
        if (receiverRegistered) {
            unregisterReceiver(captureReceiver)
            receiverRegistered = false
        }
        super.onStop()
    }

    private fun refreshStatus() {
        renderStatus(
            phoneStatus,
            hasPhonePermission(),
            getString(R.string.permission_granted),
            getString(R.string.permission_denied)
        )
        renderStatus(
            accessibilityStatus,
            isAccessibilityServiceEnabled(),
            getString(R.string.service_enabled),
            getString(R.string.service_disabled)
        )
    }

    private fun hasPhonePermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) ==
            PackageManager.PERMISSION_GRANTED

    private fun isAccessibilityServiceEnabled(): Boolean {
        val manager = getSystemService(AccessibilityManager::class.java)
        val expected = ComponentName(this, CallerMarkAccessibilityService::class.java)
        return manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            .any { info ->
                val serviceInfo = info.resolveInfo?.serviceInfo ?: return@any false
                ComponentName(serviceInfo.packageName, serviceInfo.name) == expected
            }
    }

    private fun renderStatus(view: TextView, enabled: Boolean, yesText: String, noText: String) {
        view.text = if (enabled) yesText else noText
        val color = if (enabled) R.color.accent else R.color.warning
        view.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, color))
        view.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun saveEndpoint() {
        val endpoint = endpointEdit.text?.toString()?.trim().orEmpty()
        if (endpoint.isNotEmpty()) {
            val uri = runCatching { endpoint.toUri() }.getOrNull()
            val valid = uri != null &&
                (uri.scheme.equals("https", true) || uri.scheme.equals("http", true)) &&
                !uri.host.isNullOrBlank()
            if (!valid) {
                endpointEdit.error = "请输入以 http:// 或 https:// 开头的完整接口地址"
                endpointEdit.requestFocus()
                return
            }
        }

        CaptureRepository.setEndpoint(this, endpoint)
        Toast.makeText(
            this,
            if (endpoint.isBlank()) "已清空接口：采集时只记录日志" else "接口地址已保存",
            Toast.LENGTH_SHORT
        ).show()
        Log.i(LogTags.UI, "上传接口配置已更新，configured=${endpoint.isNotBlank()}")
    }

    private fun runParserDemo() {
        val sample = listOf("来电", "138 0013 8000", "被23人标记", "疑似诈骗", "接听", "拒绝")
        val result = CallScreenParser.parse(sample)
        val message = if (result == null) {
            "解析失败"
        } else {
            "号码：${result.phoneNumber}\n" +
                "是否标记：${if (result.isMarked) "是" else "否"}\n" +
                "标记人数：${result.markCount ?: "未知"}\n" +
                "标记类型：${result.markType ?: "无"}"
        }
        AlertDialog.Builder(this)
            .setTitle("内置解析器测试")
            .setMessage(message)
            .setPositiveButton("确定", null)
            .show()
        Log.i(LogTags.PARSER, "UI parser demo result=$result")
    }

    private fun showLastCapture() {
        val json = CaptureRepository.getLastRecord(this)
        if (json == null) {
            lastCaptureText.setText(R.string.no_capture)
            return
        }

        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
            .format(Date(json.optLong("capturedAt")))
        val count = nullableValue(json, "markCount") ?: "无"
        val type = nullableValue(json, "markType") ?: "无"
        val output = buildString {
            appendLine("号码：${json.optString("phoneNumber")}")
            appendLine("是否标记：${if (json.optBoolean("isMarked")) "是" else "否"}")
            appendLine("标记人数：$count")
            appendLine("标记类型：$type")
            appendLine("来源应用：${json.optString("sourcePackage")}")
            appendLine("采集时间：$time")
            append("上传状态：${json.optString("uploadStatus", "未知")}")
        }
        lastCaptureText.text = output
    }

    private fun nullableValue(json: JSONObject, key: String): String? {
        if (!json.has(key) || json.isNull(key)) return null
        return json.opt(key)?.toString()
    }
}
