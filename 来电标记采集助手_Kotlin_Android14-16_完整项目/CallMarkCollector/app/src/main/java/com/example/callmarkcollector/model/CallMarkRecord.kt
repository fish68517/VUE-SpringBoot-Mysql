package com.example.callmarkcollector.model

import android.os.Build
import com.example.callmarkcollector.BuildConfig
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class CallMarkRecord(
    val recordId: String = UUID.randomUUID().toString(),
    val installationId: String,
    val phoneNumber: String,
    val isMarked: Boolean,
    val markCount: Int?,
    val markType: String?,
    val sourcePackage: String,
    val capturedAt: Long = System.currentTimeMillis(),
    val rawTextLines: List<String>,
    val androidVersion: String = Build.VERSION.RELEASE,
    val sdkInt: Int = Build.VERSION.SDK_INT,
    val deviceModel: String = "${Build.MANUFACTURER} ${Build.MODEL}".trim(),
    val appVersion: String = BuildConfig.VERSION_NAME
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("recordId", recordId)
        put("installationId", installationId)
        put("phoneNumber", phoneNumber)
        put("isMarked", isMarked)
        put("markCount", markCount ?: JSONObject.NULL)
        put("markType", markType ?: JSONObject.NULL)
        put("sourcePackage", sourcePackage)
        put("capturedAt", capturedAt)
        put("rawTextLines", JSONArray(rawTextLines))
        put("androidVersion", androidVersion)
        put("sdkInt", sdkInt)
        put("deviceModel", deviceModel)
        put("appVersion", appVersion)
    }
}
