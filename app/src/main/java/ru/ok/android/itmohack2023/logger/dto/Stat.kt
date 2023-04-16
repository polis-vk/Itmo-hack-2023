package ru.ok.android.itmohack2023.logger.dto

import com.google.gson.annotations.SerializedName

data class Stat(
    @SerializedName("url")
    val url: String,
    @SerializedName("method")
    val method: String,
    @SerializedName("statusCode")
    val statusCode: Int,
    @SerializedName("date")
    val date: String,
    @SerializedName("duration")
    val duration: Long,
    @SerializedName("size")
    val size: Int,
    @SerializedName("locationOfRequest")
    val locationOfRequest: String?,
)
