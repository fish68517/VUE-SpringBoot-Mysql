package com.example.dialerreplica.media

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri

class LocalMediaPlayer(private val context: Context) {
    private var player: MediaPlayer? = null
    private var activeUri: String? = null

    fun toggle(uriValue: String, looping: Boolean = false): Boolean {
        if (activeUri == uriValue && player?.isPlaying == true) {
            player?.pause()
            return false
        }
        if (activeUri == uriValue && player != null) {
            player?.start()
            return true
        }
        stop()
        return runCatching {
            val uri = Uri.parse(uriValue)
            // 先保存实例，确保资源缺失/解码失败时也能释放播放器。
            val mediaPlayer = MediaPlayer()
            player = mediaPlayer
            mediaPlayer.apply {
                if (uri.scheme == "asset") {
                    context.assets.openFd(uri.path.orEmpty().removePrefix("/")).use {
                        setDataSource(it.fileDescriptor, it.startOffset, it.length)
                    }
                } else setDataSource(context, uri)
                isLooping = looping
                setOnCompletionListener { if (!looping) activeUri = null }
                prepare()
                start()
            }
            activeUri = uriValue
            true
        }.getOrElse {
            android.util.Log.w("LocalMediaPlayer", "Audio unavailable; skipping playback", it)
            stop()
            false
        }
    }

    fun play(uriValue: String, looping: Boolean = false): Boolean {
        stop()
        return toggle(uriValue, looping)
    }

    fun stop() {
        player?.let {
            runCatching { it.stop() }
            runCatching { it.reset() }
            runCatching { it.release() }
        }
        player = null
        activeUri = null
    }

    fun duration(uriValue: String): Long = runCatching {
        val retriever = MediaMetadataRetriever()
        try {
            val uri = Uri.parse(uriValue)
            if (uri.scheme == "asset") {
                context.assets.openFd(uri.path.orEmpty().removePrefix("/")).use {
                    retriever.setDataSource(it.fileDescriptor, it.startOffset, it.length)
                }
            } else retriever.setDataSource(context, uri)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
        } finally {
            retriever.release()
        }
    }.getOrDefault(0L)
}
