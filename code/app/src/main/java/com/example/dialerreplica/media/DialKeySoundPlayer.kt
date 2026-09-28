package com.example.dialerreplica.media

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

/** 独立短音效播放器，不占用彩铃/状态语音播放器。 */
class DialKeySoundPlayer(context: Context) {
    private val pool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build())
        .build()
    private var soundId = 0
    private var streamId = 0
    private var ready = false
    private var pending = false
    private var released = false

    init {
        pool.setOnLoadCompleteListener { _, _, status ->
            if (!released) {
                ready = status == 0
                if (ready && pending) play()
                pending = false
            }
        }
        runCatching {
            context.applicationContext.assets.openFd("detail_button.mp3").use {
                soundId = pool.load(it, 1)
            }
        }.onFailure {
            android.util.Log.w("DialKeySoundPlayer", "Key sound unavailable", it)
        }
    }

    fun play() {
        if (released || soundId == 0) return
        if (!ready) { pending = true; return }
        // 连续按键从头播放，避免多段音效重叠。
        if (streamId != 0) pool.stop(streamId)
        streamId = pool.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    fun stop() {
        pending = false
        if (!released && streamId != 0) pool.stop(streamId)
        streamId = 0
    }

    fun release() {
        if (released) return
        stop()
        released = true
        pool.setOnLoadCompleteListener(null)
        pool.release()
    }
}
