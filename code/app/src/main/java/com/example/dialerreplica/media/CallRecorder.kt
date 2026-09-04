package com.example.dialerreplica.media

import android.content.Context
import android.media.MediaRecorder
import android.os.SystemClock
import java.io.File

data class RecordingResult(
    val file: File,
    val durationMs: Long,
)

class CallRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var startedAtElapsed: Long = 0

    val isRecording: Boolean get() = recorder != null

    @Suppress("DEPRECATION")
    fun start(sessionId: String): Result<File> = runCatching {
        check(recorder == null) { "Recording is already active" }
        val directory = File(context.filesDir, "recordings").apply { mkdirs() }
        val file = File(directory, "$sessionId-${System.currentTimeMillis()}.m4a")
        val mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128_000)
            setAudioSamplingRate(44_100)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        recorder = mediaRecorder
        outputFile = file
        startedAtElapsed = SystemClock.elapsedRealtime()
        file
    }

    fun stop(): RecordingResult? {
        val active = recorder ?: return null
        val file = outputFile
        val duration = (SystemClock.elapsedRealtime() - startedAtElapsed).coerceAtLeast(0)
        recorder = null
        outputFile = null
        startedAtElapsed = 0
        val stopped = runCatching { active.stop() }.isSuccess
        runCatching { active.reset() }
        runCatching { active.release() }
        if (!stopped || file == null || !file.exists() || file.length() == 0L) {
            file?.delete()
            return null
        }
        return RecordingResult(file, duration)
    }

    fun release() {
        stop()
    }
}
