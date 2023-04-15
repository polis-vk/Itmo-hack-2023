package ru.ok.android.itmohack2023.okhttp

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ru.ok.android.itmohack2023.logger.ApiInterceptor
import java.util.concurrent.TimeUnit

sealed class BaseClient {
    companion object {
        fun getBaseOkHttpClient() : OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor())
                /// added part
                .addInterceptor { chain -> ApiInterceptor.intercept(chain) }
                /// end of added part
                .connectTimeout(5, TimeUnit.SECONDS)
                .build()
        }
    }
}
