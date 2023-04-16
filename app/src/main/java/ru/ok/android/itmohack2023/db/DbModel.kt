package ru.ok.android.itmohack2023.db

import android.icu.number.IntegerWidth
import java.time.LocalDate
import java.util.Date

class DbModel(
    val userID: String = "",
    val time: String = "",
    val client: String = "",
    val path: String = "",
    val headers: String = "",
    val type: String = "",
    val requestTime: String = "",
    val requestSize: String = "",
    val responseSize: String = "",
    val statusCode: String = "",
    val methodName: String = "",
    val className: String = "",
    val other: String = ""
) {

}
