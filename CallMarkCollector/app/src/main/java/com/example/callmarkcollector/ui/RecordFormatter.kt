package com.example.callmarkcollector.ui

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object RecordFormatter {
    fun value(json: JSONObject, key: String): String =
        if (!json.has(key) || json.isNull(key)) "未知（未读取到）" else json.get(key).toString()

    fun time(json: JSONObject): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date(json.optLong("capturedAt")))

    fun captureStatus(json: JSONObject): String = when (json.optString("captureStatus")) {
        "SCANNING" -> "正在采集"
        "CAPTURED" -> "已采集"
        "NO_NUMBER" -> "未读取到号码"
        "LEGACY" -> "旧版历史记录"
        else -> "未知"
    }

    private fun markedStatus(json: JSONObject): String = when {
        !json.has("isMarked") || json.isNull("isMarked") -> "未知（页面未显示或未读取到）"
        json.optBoolean("isMarked") -> "是"
        else -> "否"
    }

    fun summary(json: JSONObject): String =
        time(json) + "  " + value(json, "phoneNumber") + "\n" +
            captureStatus(json) + " · " + value(json, "markType") + "\n" +
            "是否标记：" + markedStatus(json) + "\n" +
            "标记人数：" + value(json, "markCount") + "\n" +
            json.optString("uploadStatus", "未知")

    fun details(json: JSONObject, includeRaw: Boolean = true): String = buildString {
        appendLine("号码：" + value(json, "phoneNumber"))
        appendLine("是否标记：" + markedStatus(json))
        appendLine("标记人数：" + value(json, "markCount"))
        appendLine("标记类型：" + value(json, "markType"))
        appendLine("来源应用：" + value(json, "sourcePackage"))
        appendLine("响铃时间：" + time(json))
        appendLine("采集状态：" + captureStatus(json))
        appendLine("说明：" + json.optString("captureReason", ""))
        appendLine("上传状态：" + json.optString("uploadStatus", "未知"))
        if (includeRaw) {
            appendLine("HTTP 状态：" + value(json, "uploadHttpCode"))
            appendLine("上传尝试：" + json.optInt("uploadAttempts"))
            appendLine("来源窗口：" + value(json, "sourceWindowId"))
            appendLine("记录 ID：" + value(json, "recordId"))
            appendLine("\n采集到的原始文字：")
            val raw = json.optJSONArray("rawTextLines")
            if (raw == null || raw.length() == 0) append("未读取到")
            else (0 until raw.length()).forEach { appendLine(raw.optString(it)) }
        }
    }.trim()
}
