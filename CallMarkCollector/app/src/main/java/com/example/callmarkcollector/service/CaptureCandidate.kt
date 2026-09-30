package com.example.callmarkcollector.service

import com.example.callmarkcollector.model.ParsedCallInfo

data class CaptureCandidate(
    val info: ParsedCallInfo,
    val packageName: String,
    val windowId: Int,
    val confidence: Int
) {
    fun merge(next: CaptureCandidate): CaptureCandidate {
        if (info.phoneNumber != next.info.phoneNumber) {
            // Do not merge fields belonging to different numbers.
            return if (next.confidence > confidence) next else this
        }
        val primary = if (next.confidence >= confidence) next else this
        val marked = when {
            info.isMarked == true || next.info.isMarked == true -> true
            info.isMarked == false || next.info.isMarked == false -> false
            else -> null
        }
        return primary.copy(info = primary.info.copy(
            isMarked = marked,
            markCount = next.info.markCount ?: info.markCount,
            markType = next.info.markType ?: info.markType,
            rawTextLines = (info.rawTextLines + next.info.rawTextLines).distinct().take(120)
        ))
    }
}
