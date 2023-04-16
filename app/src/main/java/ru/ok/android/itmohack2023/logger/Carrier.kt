package ru.ok.android.itmohack2023.logger

import android.os.Build
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import ru.ok.android.itmohack2023.BuildConfig
import ru.ok.android.itmohack2023.logger.dto.DeviceStat
import ru.ok.android.itmohack2023.logger.dto.Metrics
import ru.ok.android.itmohack2023.logger.dto.Stat
import java.util.concurrent.TimeUnit

/// class for accumulate, carry and send data to logging service
class Carrier(private val stat: Stat, private val locationOfRequest: String?) {
    fun send(androidId: String) {
        val device = DeviceStat(
            osVersion = System.getProperty("os.version"),
            buildVersion = BuildConfig.VERSION_NAME,
            device = Build.DEVICE,
            name = Build.MODEL,
            id = androidId,
        )
        val full = Metrics(
            device = device,
            stat = stat.copy(locationOfRequest = locationOfRequest),
        )

        val objectToSend = Gson().toJson(full)
        println(objectToSend)
        try {
            val mediaType = "application/json; charset=utf-8".toMediaType()

            val req = Request.Builder()
                .url("http://192.168.137.1:3000/api/stats")
                .post(objectToSend.toRequestBody(mediaType))
                .build()
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor())
                .connectTimeout(5, TimeUnit.SECONDS)
                .build().newCall(req).execute()
        } catch (e: Throwable) {
            println(e)
        }
    }
}