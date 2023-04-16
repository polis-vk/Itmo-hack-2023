package chilladvanced

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import java.net.HttpURLConnection
import java.net.Proxy
import java.util.UUID

data class RequestLog(
    val url: String,
    val method: String,
    val userId: String,
    val unixtime: Long,
    val inputTraffic: Long,
    val outputTraffic: Long,
    val duration: Long,
)

class Logger() {
    val userId: UUID = UUID.randomUUID()
    private val storage = mutableSetOf<RequestLog>()
    val sendLogsLock = Mutex()
    val gson = Gson()

    fun log(url: String, method: String, duration: Long, inputTraffic: Long, outputTraffic: Long) {
        val l = RequestLog(url, method, userId.toString(), System.currentTimeMillis(), inputTraffic, outputTraffic, duration)
        synchronized(storage) {
            storage += l
        }
    }


    val SEND_LOG_TIME_SECOND = 1
    fun runSending() {
        Thread {
            while (true) {
                Thread.sleep(SEND_LOG_TIME_SECOND.toLong() * 1000)
                sendLog()
            }
        }.start()
    }

    private fun sendLog() {
        val lgs = synchronized(storage) {
            storage.toList()
        }

        if (lgs.isEmpty()) {
            return
        }

        try {
            Log.i("Logger", "log object: $lgs")
            val connection = java.net.URL("https://d.kbats.ru/util/user-add")
                .openConnection(Proxy.NO_PROXY) as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")

            val info = gson.toJson(lgs)
            Log.i("Logger", "sending log info: $info")
            connection.outputStream.write(info.toByteArray())

            val text = connection.inputStream.bufferedReader().readText()

            synchronized(storage) {
                storage -= lgs.toSet()
            }
        } catch (e: IOException) {
            Log.e("Logger", "failed to log network request, caused error $e")
        }
    }
}