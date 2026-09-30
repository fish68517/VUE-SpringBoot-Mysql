package com.example.callmarkcollector.data

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.callmarkcollector.model.CallMarkRecord
import org.json.JSONObject
import java.util.UUID

/** Serialized mutations; upload callbacks update by ID and never recreate deleted rows. */
object CaptureRepository {
    const val ACTION_CAPTURE_UPDATED = "com.example.callmarkcollector.ACTION_CAPTURE_UPDATED"
    private var helper: Store? = null
    private fun preferences(context: Context) = context.getSharedPreferences("call_mark_collector", Context.MODE_PRIVATE)
    private fun database(context: Context): SQLiteDatabase {
        if (helper == null) helper = Store(context.applicationContext)
        return helper!!.writableDatabase
    }

    private class Store(private val context: Context) : SQLiteOpenHelper(context, "call_history.db", null, 1) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE records (id TEXT PRIMARY KEY, captured_at INTEGER NOT NULL, payload TEXT NOT NULL)")
            db.execSQL("CREATE INDEX records_time ON records(captured_at DESC)")
            db.execSQL("CREATE TABLE metadata (key TEXT PRIMARY KEY, value TEXT NOT NULL)")
            val legacy = context.getSharedPreferences("call_mark_collector", Context.MODE_PRIVATE).getString("last_record", null)
            val json = legacy?.let { runCatching { JSONObject(it) }.getOrNull() }
            if (json != null) {
                val id = json.optString("recordId").ifBlank { UUID.randomUUID().toString() }
                json.put("recordId", id).put("captureStatus", "LEGACY").put("uploadState", "LEGACY")
                if (!json.has("capturedAt") || json.isNull("capturedAt")) json.put("capturedAt", System.currentTimeMillis())
                db.insertOrThrow("records", null, values(json))
            }
        }
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
        override fun onOpen(db: SQLiteDatabase) {
            super.onOpen(db)
            // Migration is committed before onOpen. Remove the duplicate legacy copy so deletion is real.
            context.getSharedPreferences("call_mark_collector", Context.MODE_PRIVATE)
                .edit().remove("last_record").apply()
        }
    }

    private fun values(json: JSONObject) = ContentValues().apply {
        put("id", json.getString("recordId"))
        put("captured_at", json.getLong("capturedAt"))
        put("payload", json.toString())
    }

    @Synchronized fun getEndpoint(context: Context): String =
        preferences(context).getString("upload_endpoint", "").orEmpty()

    @Synchronized fun setEndpoint(context: Context, endpoint: String) {
        preferences(context).edit().putString("upload_endpoint", endpoint.trim()).apply()
    }

    @Synchronized fun getInstallationId(context: Context): String {
        val prefs = preferences(context)
        return prefs.getString("installation_id", null) ?: UUID.randomUUID().toString().also {
            check(prefs.edit().putString("installation_id", it).commit()) { "无法保存安装标识" }
        }
    }

    @Synchronized fun getRecord(context: Context, id: String): JSONObject? =
        database(context).query("records", arrayOf("payload"), "id=?", arrayOf(id), null, null, null).use {
            if (it.moveToFirst()) JSONObject(it.getString(0)) else null
        }

    @Synchronized fun getHistory(context: Context, limit: Int = 50, offset: Int = 0): List<JSONObject> =
        database(context).query("records", arrayOf("payload"), null, null, null, null,
            "captured_at DESC, rowid DESC", offset.coerceAtLeast(0).toString() + "," + limit.coerceIn(1, 100)).use {
            buildList { while (it.moveToNext()) add(JSONObject(it.getString(0))) }
        }

    @Synchronized fun getLastRecord(context: Context): JSONObject? = getHistory(context, 1).firstOrNull()

    @Synchronized fun count(context: Context): Int =
        database(context).rawQuery("SELECT COUNT(*) FROM records", null).use { it.moveToFirst(); it.getInt(0) }

    @Synchronized fun activeSession(context: Context): JSONObject? =
        database(context).query("metadata", arrayOf("value"), "key='active_session'", null, null, null, null).use {
            if (it.moveToFirst()) JSONObject(it.getString(0)) else null
        }

    private fun putSession(db: SQLiteDatabase, json: JSONObject) {
        db.insertWithOnConflict("metadata", null, ContentValues().apply {
            put("key", "active_session")
            put("value", json.toString())
        }, SQLiteDatabase.CONFLICT_REPLACE)
    }

    @Synchronized fun beginSession(context: Context, record: CallMarkRecord) {
        val db = database(context)
        val json = record.toJson().put("uploadState", "WAITING_CAPTURE").put("uploadStatus", "采集中，等待字段补全")
        db.beginTransaction()
        try {
            db.insertOrThrow("records", null, values(json))
            putSession(db, JSONObject().put("recordId", record.recordId).put("startedAt", record.capturedAt).put("finished", false))
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
        notifyChanged(context)
    }

    /** Updates only; deletion during capture prevents reinsertion. */
    @Synchronized fun updateCapture(context: Context, record: CallMarkRecord, finished: Boolean): Boolean {
        val db = database(context)
        val json = record.toJson()
            .put("uploadState", if (finished) "PENDING" else "WAITING_CAPTURE")
            .put("uploadStatus", if (finished) "等待上传" else "采集中，等待字段补全")
        var changed: Boolean
        db.beginTransaction()
        try {
            changed = db.update("records", values(json), "id=?", arrayOf(record.recordId)) > 0
            val active = activeSession(context)
            if (finished && active?.optString("recordId") == record.recordId) {
                putSession(db, active.put("finished", true))
            }
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
        if (changed) notifyChanged(context)
        return changed
    }

    @Synchronized fun endSession(context: Context) {
        database(context).delete("metadata", "key='active_session'", null)
    }

    @Synchronized fun deleteRecord(context: Context, id: String) {
        database(context).delete("records", "id=?", arrayOf(id))
        notifyChanged(context)
    }

    @Synchronized fun deleteAll(context: Context) {
        database(context).delete("records", null, null)
        // Keep the current ring token, so deletion cannot start a second capture for this call.
        notifyChanged(context)
    }

    private fun updateJson(context: Context, json: JSONObject): Boolean {
        val changed = database(context).update("records", values(json), "id=?", arrayOf(json.getString("recordId"))) > 0
        if (changed) notifyChanged(context)
        return changed
    }

    @Synchronized fun updateUploadStatus(context: Context, id: String, state: String, status: String, httpCode: Int? = null) {
        val json = getRecord(context, id) ?: return
        json.put("uploadState", state).put("uploadStatus", status)
            .put("uploadHttpCode", httpCode ?: JSONObject.NULL).put("uploadUpdatedAt", System.currentTimeMillis())
        updateJson(context, json)
    }

    @Synchronized fun claimUpload(context: Context, id: String): JSONObject? {
        val json = getRecord(context, id) ?: return null
        if (json.optString("uploadState") != "PENDING") return null
        json.put("uploadState", "UPLOADING").put("uploadStatus", "正在上传")
        json.put("uploadAttempts", json.optInt("uploadAttempts") + 1)
        updateJson(context, json)
        return json
    }

    @Synchronized fun prepareRetry(context: Context, id: String): Boolean {
        val json = getRecord(context, id) ?: return false
        if (json.optString("uploadState") !in setOf("FAILED", "UNCONFIGURED", "INTERRUPTED")) return false
        return updateJson(context, json.put("uploadState", "PENDING").put("uploadStatus", "等待重新上传"))
    }

    /** Once per process, before workers start. Do not silently retry uncertain HTTP outcomes. */
    @Synchronized fun recoverUploads(context: Context): List<String> {
        val db = database(context)
        val rows = db.query("records", arrayOf("payload"), null, null, null, null, null).use {
            buildList { while (it.moveToNext()) add(JSONObject(it.getString(0))) }
        }
        rows.filter { it.optString("uploadState") == "UPLOADING" }.forEach {
            updateUploadStatus(context, it.getString("recordId"), "INTERRUPTED",
                "上次上传被中断，服务端是否收到未知；可手动重试")
        }
        return rows.filter { it.optString("uploadState") == "PENDING" }.map { it.getString("recordId") }
    }

    private fun notifyChanged(context: Context) {
        context.sendBroadcast(Intent(ACTION_CAPTURE_UPDATED).setPackage(context.packageName))
    }
}
