package chilladvanced

import android.util.Log
import java.io.IOException
import java.util.UUID

class Logger() {
    val userId: UUID = UUID.randomUUID()

    fun log(url: String, method: String, duration: Long, inputTraffic: Long, outputTraffic: Long) {

        val params = "[{\"url\": \"$url\", " +
                "\"method\": \"$method\", " +
                "\"userId\": $userId, " +
                "\"inputTraffic\": $inputTraffic, " +
                "\"outputTraffic\": $outputTraffic, " +
                "\"duration\": $duration, " +
                "\"unixtime\": ${System.currentTimeMillis()}}]"

        try {
            Log.i("Logger", "log object: $params")
//            val connection =
//                URL("https://d.kbats.ru/user-add").openConnection(Proxy.NO_PROXY) as HttpURLConnection
//            connection.requestMethod = "POST"
//            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
//
//            connection.outputStream.write(params.toByteArray())
//
//            val text = connection.inputStream.bufferedReader().readText()
//            println(text)
        } catch (e: IOException) {
            println("failed to log network request, caused error $e")
        }
    }
}