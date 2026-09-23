package com.example.callmarkcollector.model

data class ParsedCallInfo(
    val phoneNumber: String,
    val isMarked: Boolean,
    val markCount: Int?,
    val markType: String?,
    val rawTextLines: List<String>
)
