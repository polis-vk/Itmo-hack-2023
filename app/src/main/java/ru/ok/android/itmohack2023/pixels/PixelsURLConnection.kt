package ru.ok.android.itmohack2023.pixels

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import android.util.Log

class PixelsURLConnection(url: URL) : URLConnection(url) {
    private var connection: HttpURLConnection? = null
    private var startTime: Long = 0
    private var endTime: Long = 0
    private var requestSize: Long = 0
    private var responseSize: Long = 0

    override fun connect() {
//        try {
//            connection = url.openConnection() as HttpURLConnection
//            startTime = System.currentTimeMillis()
//            connection?.connect()
//            endTime = System.currentTimeMillis()
//            requestSize = connection?.requestMethod?.length?.toLong() as Long
//            responseSize = connection?.contentLength?.toLong() as Long
//
////            Log.d("OkHttp Request", "Url: ${connection?.url?.path}")
////            Log.d("OkHttp Request", "Method: ${connection?.requestMethod}")
////            Log.d("OkHttp Request", "Body: ${requestSize}")
//        } catch (e: Exception) {
////            Log.e("PixelsURLConnection", "Error connecting: ${e.message}")
//        }
    }

    override fun getInputStream(): InputStream {
        val inputStream = super.getInputStream()
//        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
//        var line: String? = bufferedReader.readLine()
//        while (line != null) {
//            // Process the traffic data here
//            line = bufferedReader.readLine()
//        }
        return inputStream
    }
}