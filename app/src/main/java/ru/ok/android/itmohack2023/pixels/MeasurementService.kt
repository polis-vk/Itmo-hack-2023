package ru.ok.android.itmohack2023.pixels

import android.content.Context
import java.util.UUID
import android.provider.Settings



//val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
object MeasurementService {
    val measurements = mutableMapOf<String, Long>()

    fun start(id: String) {
        // start time measurement
        measurements[id] = System.currentTimeMillis()
    }

    fun start(): String {
        // start time measurement
        val id = generateRandomId()

        measurements[id] = System.currentTimeMillis()

        return id
    }

    fun generateRandomId(): String {
        return UUID.randomUUID().toString()
    }

    fun end(id: String): Int {
        // calculate time difference in milliseconds
        val startTime = measurements.remove(id) ?: return -1
        val endTime = System.currentTimeMillis()
        val elapsedTime = (endTime - startTime).toInt()
        return elapsedTime
    }

    fun getDeviceId(context: Context): String {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        return androidId
    }


}