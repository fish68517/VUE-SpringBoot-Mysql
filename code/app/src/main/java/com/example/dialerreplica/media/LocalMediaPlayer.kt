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
            player = MediaPlayer().apply {
                setDataSource(context, uri)
                isLooping = looping
                setOnCompletionListener { if (!looping) activeUri = null }
                prepare()
                start()
            }
            activeUri = uriValue
            true
        }.getOrDefault(false)
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
            retriever.setDataSource(context, Uri.parse(uriValue))
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
        } finally {
            retriever.release()
        }
    }.getOrDefault(0L)
}
