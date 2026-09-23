package com.example.callmarkcollector.data

import android.content.Context
import android.content.Intent
import androidx.core.content.edit
import com.example.callmarkcollector.model.CallMarkRecord
import org.json.JSONObject
import java.util.UUID

object CaptureRepository {
    const val ACTION_CAPTURE_UPDATED = "com.example.callmarkcollector.ACTION_CAPTURE_UPDATED"

    private const val PREFS = "call_mark_collector"
    private const val KEY_ENDPOINT = "upload_endpoint"
    private const val KEY_INSTALLATION_ID = "installation_id"
    private const val KEY_LAST_RECORD = "last_record"

    private fun preferences(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun getEndpoint(context: Context): String =
        preferences(context).getString(KEY_ENDPOINT, "").orEmpty()

    fun setEndpoint(context: Context, endpoint: String) {
        preferences(context).edit { putString(KEY_ENDPOINT, endpoint.trim()) }
    }

    fun getInstallationId(context: Context): String {
        val prefs = preferences(context)
        prefs.getString(KEY_INSTALLATION_ID, null)?.let { return it }
        val value = UUID.randomUUID().toString()
        prefs.edit { putString(KEY_INSTALLATION_ID, value) }
        return value
    }

    fun saveRecord(context: Context, record: CallMarkRecord, uploadStatus: String) {
        val json = record.toJson().apply {
            put("uploadStatus", uploadStatus)
        }
        preferences(context).edit { putString(KEY_LAST_RECORD, json.toString()) }
        notifyChanged(context)
    }

    fun updateUploadStatus(context: Context, status: String, httpCode: Int? = null) {
        val current = getLastRecord(context) ?: return
        current.put("uploadStatus", status)
        current.put("uploadHttpCode", httpCode ?: JSONObject.NULL)
        preferences(context).edit { putString(KEY_LAST_RECORD, current.toString()) }
        notifyChanged(context)
    }

    fun getLastRecord(context: Context): JSONObject? {
        val raw = preferences(context).getString(KEY_LAST_RECORD, null) ?: return null
        return runCatching { JSONObject(raw) }.getOrNull()
    }

    private fun notifyChanged(context: Context) {
        context.sendBroadcast(
            Intent(ACTION_CAPTURE_UPDATED).setPackage(context.packageName)
        )
    }
}
