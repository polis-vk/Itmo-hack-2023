package ru.ok.android.itmohack2023.logcat

import java.time.LocalDateTime

data class LogcatInfo(var time: LocalDateTime, var identification: String, var sender: String, var logBody: String)