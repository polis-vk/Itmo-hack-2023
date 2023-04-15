package ru.ok.android.itmohack2023.logger

import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import ru.ok.android.itmohack2023.logger.dto.Stat
import java.io.IOException
import java.util.concurrent.TimeUnit

/// this class is used to intercept the requests
class ApiInterceptor {
    companion object {
        @set:JvmName("level")
        @Volatile
        var level = HttpLoggingInterceptor.Level.BASIC

        private val logger: HttpLoggingInterceptor.Logger = HttpLoggingInterceptor.Logger.DEFAULT

        @Throws(IOException::class)
        fun intercept(chain: Interceptor.Chain): Response {
            var result = ""
            val level = this.level

            val request = chain.request()
            if (level == HttpLoggingInterceptor.Level.NONE) {
                return chain.proceed(request)
            }

            val logBody = level == HttpLoggingInterceptor.Level.BODY
            val logHeaders = logBody || level == HttpLoggingInterceptor.Level.HEADERS

            val requestBody = request.body

            val connection = chain.connection()
            var requestStartMessage =
                ("--> ${request.method} ${request.url} ${if (connection != null) " " + connection.protocol() else ""}")
            if (!logHeaders && requestBody != null) {
                requestStartMessage += " (${requestBody.contentLength()}-byte body)"
            }
            result += requestStartMessage

            val startNs = System.nanoTime()
            val response: Response
            try {
                response = chain.proceed(request)
            } catch (e: Exception) {
                result += "<-- HTTP FAILED: $e"
                throw e
            }
            val endNs = System.nanoTime()
            val tookMs = TimeUnit.NANOSECONDS.toMillis(endNs - startNs)

            val responseBody = response.body!!
            val contentLength = responseBody.contentLength()
            result += " <-- ${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} (${tookMs}ms${if (!logHeaders) ", $contentLength body" else ""})"
            val stat = Stat(
                url = request.url.toString(),
                size = contentLength.toInt(),
                method = request.method,
                statusCode = response.code,
                duration = tookMs,
                date = TimeUnit.NANOSECONDS.toMillis(startNs).toInt(),
                locationOfRequest = null
            )
            logger.log(stat.toString())
            Carrier(stat = stat, locationOfRequest = null).send()

            return response
        }

        fun glideListener() : RequestListener<Drawable> {
            return object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    println(resource.toString())
                    println(model.toString())
                    println(target.toString())
                    println(dataSource.toString())
                    println(isFirstResource.toString())

                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    println(e.toString())
                    println(model.toString())
                    println(target.toString())
                    println(isFirstResource.toString())

                    return false
                }
            }
        }
    }
}