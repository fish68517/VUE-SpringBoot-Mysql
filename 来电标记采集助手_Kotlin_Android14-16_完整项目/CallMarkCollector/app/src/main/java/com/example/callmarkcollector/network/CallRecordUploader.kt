package com.example.callmarkcollector.network

import android.content.Context
import android.util.Log
import com.example.callmarkcollector.data.CaptureRepository
import com.example.callmarkcollector.model.CallMarkRecord
import com.example.callmarkcollector.util.LogTags
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

object CallRecordUploader {
    private const val CONNECT_TIMEOUT_MS = 10_000
    private const val READ_TIMEOUT_MS = 10_000
    private val executor = Executors.newSingleThreadExecutor()

    /**
     * 每生成一条采集记录都必须调用此方法。
     * 服务端尚未提供接口时，endpoint 为空，本方法记录完整 JSON 和明确状态但不联网。
     */
    fun upload(context: Context, record: CallMarkRecord) {
        val appContext = context.applicationContext
        val endpoint = CaptureRepository.getEndpoint(appContext)
        val payload = record.toJson().toString()

        Log.i(LogTags.UPLOAD, "upload() invoked, recordId=${record.recordId}, endpointConfigured=${endpoint.isNotBlank()}")
        Log.d(LogTags.UPLOAD, "payload=$payload")

        if (endpoint.isBlank()) {
            CaptureRepository.updateUploadStatus(appContext, "接口未配置：已调用上传模块，未发送网络请求")
            Log.w(LogTags.UPLOAD, "服务端接口未配置，本次记录保存在本地状态页")
            return
        }

        executor.execute {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(endpoint)
                require(url.protocol == "https" || url.protocol == "http") {
                    "接口地址只支持 http 或 https"
                }

                connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = CONNECT_TIMEOUT_MS
                    readTimeout = READ_TIMEOUT_MS
                    doOutput = true
                    useCaches = false
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("X-Install-Id", record.installationId)
                }

                connection.outputStream.use { stream ->
                    stream.write(payload.toByteArray(StandardCharsets.UTF_8))
                }

                val code = connection.responseCode
                val response = readResponse(connection, code).take(500)
                if (code in 200..299) {
                    CaptureRepository.updateUploadStatus(appContext, "上传成功", code)
                    Log.i(LogTags.UPLOAD, "上传成功 code=$code response=$response")
                } else {
                    CaptureRepository.updateUploadStatus(appContext, "上传失败：HTTP $code", code)
                    Log.e(LogTags.UPLOAD, "上传失败 code=$code response=$response")
                }
            } catch (error: Exception) {
                CaptureRepository.updateUploadStatus(
                    appContext,
                    "上传异常：${error.message ?: error.javaClass.simpleName}"
                )
                Log.e(LogTags.UPLOAD, "上传异常", error)
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun readResponse(connection: HttpURLConnection, code: Int): String {
        val stream = if (code in 200..299) connection.inputStream else connection.errorStream
        if (stream == null) return ""
        return stream.bufferedReader(StandardCharsets.UTF_8).use(BufferedReader::readText)
    }
}
