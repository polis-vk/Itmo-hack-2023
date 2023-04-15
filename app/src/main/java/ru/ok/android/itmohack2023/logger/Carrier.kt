package ru.ok.android.itmohack2023.logger

import android.os.Build
import android.provider.Settings
import com.google.gson.Gson
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.ok.android.itmohack2023.BuildConfig
import ru.ok.android.itmohack2023.logger.dto.DeviceStat
import ru.ok.android.itmohack2023.logger.dto.Metrics
import ru.ok.android.itmohack2023.logger.dto.Stat

/// class for accumulate, carry and send data to logging service
class Carrier(private val stat: Stat, private val locationOfRequest: String?) {
    fun send() {
        val device = DeviceStat(
            osVersion = System.getProperty("os.version"),
            buildVersion = BuildConfig.VERSION_NAME,
            device = Build.DEVICE,
            model = Build.MODEL,
            deviceId = Settings.Secure.ANDROID_ID,
        )
        val full = Metrics(
            device = device,
            stat = stat.copy(locationOfRequest = locationOfRequest),
        )

        val objectToSend = Gson().toJson(full)
        println(objectToSend)
        try {
            Request.Builder()
                .url("https://")
                .post(objectToSend.toRequestBody())
                .build()
        } catch (_: Throwable) { }
    }
}