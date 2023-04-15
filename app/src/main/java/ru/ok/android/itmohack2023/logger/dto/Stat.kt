package ru.ok.android.itmohack2023.logger.dto

import android.provider.Settings
import com.google.gson.annotations.SerializedName

data class Stat(
    @SerializedName("url")
    val url: String,
    @SerializedName("method")
    val method: String,
    @SerializedName("statusCode")
    val statusCode: Int,
    @SerializedName("date")
    val date: Int,
    @SerializedName("duration")
    val duration: Long,
    @SerializedName("size")
    val size: Int,
    @SerializedName("locationOfRequest")
    val locationOfRequest: String?,
    @SerializedName("deviceId")
    val deviceId: String = Settings.Secure.ANDROID_ID,
)
