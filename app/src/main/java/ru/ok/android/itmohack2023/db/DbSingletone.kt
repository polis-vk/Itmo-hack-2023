package ru.ok.android.itmohack2023.db

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import ru.ok.android.itmohack2023.pixels.MeasurementService

@SuppressLint("StaticFieldLeak")
object DbSingletone {
    lateinit var context: Context
    lateinit var userId: String
    @SuppressLint("HardwareIds")
    fun initValue(context_ : Context) {
        context = context_

        userId = MeasurementService.getDeviceId(context_)
    }

}