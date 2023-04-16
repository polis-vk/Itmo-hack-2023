package ru.ok.android.itmohack2023.logger.dto

import com.google.gson.annotations.SerializedName

data class Metrics(
    @SerializedName("device")
    val device: DeviceStat,
    @SerializedName("stat")
    val stat: Stat,
)
