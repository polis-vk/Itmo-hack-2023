package ru.ok.android.itmohack2023

import android.app.Application
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import ru.ok.android.itmohack2023.okhttp.BaseClient
import ru.ok.android.itmohack2023.okhttp.MyAppGlideModule

class ItmohackApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val config = OkHttpImagePipelineConfigFactory
            .newBuilder(this, BaseClient.getBaseOkHttpClient())
            .build()

        Fresco.initialize(this, config)

        MyAppGlideModule().registerComponents(this, Glide.get(this), Registry())
    }
}