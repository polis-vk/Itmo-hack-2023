package ru.ok.android.itmohack2023.logcat

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.google.gson.Gson
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit


class LogcatHelper(private val timeout: Long, mainContext: Context) {
    companion object {
        private const val LOG_TAG = "LogcatHandler"
        val okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor {
                Log.d(LOG_TAG, it)
            }
                .setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(5, TimeUnit.SECONDS)
            .build()

        fun createGlide(context: Context) {
            Glide.get(context).registry
                .replace(
                    GlideUrl::class.java,
                    InputStream::class.java, OkHttpUrlLoader.Factory(okHttpClient)
                )
        }
    }

    val parser = LogcatParser()

    init {
        Picasso.setSingletonInstance(
            Picasso.Builder(mainContext)
                .downloader(OkHttp3Downloader(okHttpClient))
                .build()
        )

    }

    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun publish() {
        while (true) {
            val process = withContext(Dispatchers.IO) {
                Runtime.getRuntime().exec("logcat -d")
            }
            val lines = BufferedReader(InputStreamReader(process.inputStream))
                .readLines()
                .filter { it.contains(LOG_TAG) }
            val logs = lines
                .map { parser.parse(it) }
                .filterNotNull()
                .filter { it.sender == LOG_TAG }
                .groupBy { it.identification }

            val finalLogs = mutableListOf<LogcatRequest>()

            for (log in logs) {
                val request = parser.processAnswer(log.value)
                if (request != null) {
                    finalLogs.add(request)
                }
            }
            val client = HttpClient(CIO) {
                expectSuccess = true
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                    })
                }
            }
            runBlocking {

                client.post("http://10.0.2.2:8080/addNetworkEvent") {
                    contentType(ContentType.Application.Json)
                    setBody(Gson().toJson(finalLogs))
                }
            }
            withContext(Dispatchers.IO) {
                Runtime.getRuntime().exec("logcat -c")
            }
            withContext(Dispatchers.IO) {
                Thread.sleep(timeout)
            }
        }
    }
}
