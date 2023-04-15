package ru.ok.android.itmohack2023.logger.dto

import com.google.gson.annotations.SerializedName

data class DeviceStat(
    @SerializedName("device")
    val model: String,
    @SerializedName("buildVersion")
    val buildVersion: String,
    @SerializedName("os")
    val device: String,
    @SerializedName("osVersion")
    val osVersion: String?,
    @SerializedName("deviceId")
    val deviceId: String,
)
