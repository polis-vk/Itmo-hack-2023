package ru.ok.android.itmohack2023.retrofit

import com.google.gson.GsonBuilder
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.ok.android.itmohack2023.okhttp.BaseClient

object RetrofitProvider {

    val retrofit: Retrofit by lazy {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = BaseClient.getBaseOkHttpClient()
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()
        val gsonConverterFactory = GsonConverterFactory.create(gson)
        val rxJavaCallAdapterFactory = RxJava3CallAdapterFactory.create()

        Retrofit.Builder()
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(rxJavaCallAdapterFactory)
            .baseUrl("https://catfact.ninja/")
            .client(okHttpClient)
            .build()
    }
}