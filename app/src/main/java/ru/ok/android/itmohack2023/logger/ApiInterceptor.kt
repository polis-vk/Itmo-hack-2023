package ru.ok.android.itmohack2023.logger

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import ru.ok.android.itmohack2023.logger.dto.Stat
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
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

            val startNs = System.currentTimeMillis()
            val response: Response
            try {
                response = chain.proceed(request)
            } catch (e: Exception) {
                result += "<-- HTTP FAILED: $e"
                throw e
            }
            val endNs = System.currentTimeMillis()
            val tookMs = endNs - startNs

            val responseBody = response.body!!
            val contentLength = responseBody.contentLength()
            result += " <-- ${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} (${tookMs}ms${if (!logHeaders) ", $contentLength body" else ""})"
            println(startNs)
            println(TimeUnit.NANOSECONDS.toMillis(startNs))
            val stat = Stat(
                url = request.url.toString(),
                size = contentLength.toInt(),
                method = request.method,
                statusCode = response.code,
                duration = tookMs,
                date = startNs.toString(),
                locationOfRequest = null
            )
            logger.log(stat.toString())
            Carrier(stat = stat, locationOfRequest = null).send(UUID.randomUUID().toString())

            return response
        }

        fun connectionWrapper(connection: URL) : HttpURLConnection {
            val startMs = System.currentTimeMillis()
            val result = connection.openConnection() as HttpURLConnection
            val endMs = System.currentTimeMillis()
            Carrier(
                Stat(
                    url = result.url.toString(),
                    size = result.contentLength,
                    method = result.requestMethod,
                    statusCode = result.responseCode,
                    duration = endMs - startMs,
                    date = result.date.toString(),
                    locationOfRequest = null
                ),
                null
            ).send(UUID.randomUUID().toString())
            return result
        }
    }
}