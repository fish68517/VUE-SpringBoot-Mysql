package com.example.callmarkcollector.network

import android.content.Context
import android.util.Log
import com.example.callmarkcollector.data.CaptureRepository
import com.example.callmarkcollector.util.LogTags
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

object CallRecordUploader {
    private val executor = Executors.newSingleThreadExecutor()
    private var initialized = false

    @Synchronized fun initialize(context: Context) {
        if (initialized) return
        initialized = true
        val app = context.applicationContext
        CaptureRepository.recoverUploads(app).forEach { id -> executor.execute { perform(app, id) } }
    }

    fun upload(context: Context, recordId: String) {
        initialize(context)
        val app = context.applicationContext
        executor.execute { perform(app, recordId) }
    }

    fun retry(context: Context, recordId: String): Boolean {
        initialize(context)
        if (!CaptureRepository.prepareRetry(context, recordId)) return false
        upload(context, recordId)
        return true
    }

    private fun perform(context: Context, id: String) {
        // Atomic claim means repeated events/enqueue requests do not issue duplicate POSTs.
        val saved = CaptureRepository.claimUpload(context, id) ?: return
        val endpoint = CaptureRepository.getEndpoint(context)
        if (endpoint.isBlank()) {
            CaptureRepository.updateUploadStatus(context, id, "UNCONFIGURED", "接口未配置，记录已保存；配置后可在详情中重试")
            return
        }
        val payload = JSONObject(saved.toString()).apply {
            listOf("uploadState", "uploadStatus", "uploadHttpCode", "uploadUpdatedAt", "uploadAttempts").forEach { remove(it) }
        }
        var connection: HttpURLConnection? = null
        try {
            if (CaptureRepository.getRecord(context, id) == null) return
            val url = URL(endpoint)
            require(url.protocol == "https" || url.protocol == "http") { "接口只支持 http 或 https" }
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                instanceFollowRedirects = false
                connectTimeout = 10_000
                readTimeout = 10_000
                doOutput = true
                useCaches = false
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("X-Install-Id", payload.getString("installationId"))
                setRequestProperty("Idempotency-Key", id)
            }
            val bytes = payload.toString().toByteArray(StandardCharsets.UTF_8)
            connection.setFixedLengthStreamingMode(bytes.size)
            connection.outputStream.use { it.write(bytes) }
            val code = connection.responseCode
            if (code in 200..299) {
                CaptureRepository.updateUploadStatus(context, id, "SUCCESS", "上传成功", code)
            } else {
                CaptureRepository.updateUploadStatus(context, id, "FAILED", "上传失败：HTTP " + code + "，可手动重试", code)
            }
            Log.i(LogTags.UPLOAD, "recordId=" + id + ", HTTP=" + code)
        } catch (error: Exception) {
            CaptureRepository.updateUploadStatus(context, id, "FAILED",
                "上传异常（是否送达需服务端确认）：" + (error.message ?: error.javaClass.simpleName))
            Log.e(LogTags.UPLOAD, "上传异常 recordId=" + id, error)
        } finally {
            connection?.disconnect()
        }
    }
}
