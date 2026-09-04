package com.example.dialerreplica.model

enum class CallPhase {
    IDLE,
    DIALING,
    CONNECTED,
    SELF_HANGING_UP,
    REMOTE_ENDED,
    UNREACHABLE,
    BUSY,
    ENDED,
}

enum class CallEndReason {
    LOCAL_HANGUP,
    REMOTE_HANGUP,
    UNREACHABLE,
    BUSY,
    INTERRUPTED,
}

data class CallSessionSnapshot(
    val sessionId: String = "",
    val rawNumber: String = "",
    val formattedNumber: String = "",
    val location: String = "",
    val phase: CallPhase = CallPhase.IDLE,
    val callElapsedMs: Long = 0,
    val recording: Boolean = false,
    val recordingElapsedMs: Long = 0,
    val recordingPath: String? = null,
    val activeAction: String? = null,
)

data class NumberAttribution(
    val province: String = "",
    val city: String = "",
    val operator: String = "",
    val numberType: String = "unknown",
) {
    val displayText: String
        get() = listOf(province + city, operator.removePrefix("中国"))
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { "未知地区" }
}
