package ru.ok.android.itmohack2023.pixels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings

class ApplicationId : Application(){
        lateinit var appContext: Context

        override fun onCreate() {
            super.onCreate()
            appContext = applicationContext
        }

//    @SuppressLint("HardwareIds")
    fun getAndroidId(): String? {
        return Settings.Secure.getString(appContext.contentResolver, Settings.Secure.ANDROID_ID)
    }
}