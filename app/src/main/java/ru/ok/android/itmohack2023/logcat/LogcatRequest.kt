package ru.ok.android.itmohack2023.logcat

import java.time.LocalDateTime

data class LogcatRequest(
    var request: String,
    var response: String,
    var type: String,
    var startTimestamp: Long,
    var finishTimestamp: Long,
    var bytesCount: Long
)