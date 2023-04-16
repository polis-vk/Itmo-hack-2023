package ru.ok.android.itmohack2023.pixels.picasso

import android.content.Context
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import ru.ok.android.itmohack2023.pixels.okHttp.OkHttpInterceptor

class PicassoClientProvider {
    fun getClient(context: Context): Picasso {
        val client = OkHttpClient.Builder()
            .addInterceptor(OkHttpInterceptor())
            .build()

        return Picasso.Builder(context)
            .downloader(OkHttp3Downloader(client))
            .build()
    }
}