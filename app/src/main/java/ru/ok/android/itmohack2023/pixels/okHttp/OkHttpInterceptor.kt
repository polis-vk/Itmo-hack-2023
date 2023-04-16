package ru.ok.android.itmohack2023.pixels.okHttp
//package com.example.okhttpinterceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import ru.ok.android.itmohack2023.db.DbManager
import ru.ok.android.itmohack2023.db.DbModel
import ru.ok.android.itmohack2023.db.DbSingletone
import ru.ok.android.itmohack2023.pixels.MeasurementService
import java.io.BufferedReader

class OkHttpInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBody = request.body

        val exception = Exception()
        val stackTrace = exception.stackTrace

        val id = MeasurementService.start()

        lateinit var logReader: BufferedReader

        val response = chain.proceed(request)
        val responseBody = response.body

        val time = MeasurementService.end(id)

        val modelInstance = DbModel(
            userID = DbSingletone.userId,
            time = time.toString(),
            client = "OkHttp",
            path = request.url.toString(),
            type = request.method,
            requestSize = requestBody.toString().length.toString(),
            responseSize = responseBody.toString().length.toString(),
            className = stackTrace[4].className,
            methodName = stackTrace[4].methodName,
            statusCode = response.code.toString(),
        )

        val tmpDb = DbManager (DbSingletone.context)
        tmpDb.openDb()
        tmpDb.insertToDb(modelInstance)

        return response
    }
}