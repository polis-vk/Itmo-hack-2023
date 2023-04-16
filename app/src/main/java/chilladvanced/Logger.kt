package chilladvanced

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import java.net.HttpURLConnection
import java.net.Proxy
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

data class RequestLog(
    val url: String,
    val method: String,
    val userId: String,
    val unixtime: Long,
    val inputTraffic: Long,
    val outputTraffic: Long,
    val duration: Long,
)

typealias Host = String

typealias Timestamp = Long

object Logger {
    @JvmStatic
    val userId: UUID = UUID.randomUUID()
    @JvmStatic
    private val storage = mutableSetOf<RequestLog>()
    @JvmStatic
    val sendLogsLock = Mutex()
    @JvmStatic
    val gson = Gson()

    @JvmStatic
    val map: ConcurrentHashMap<Host, Pair<NETWORK_METHODS, Timestamp>> = ConcurrentHashMap()
    @JvmStatic
    var lastUpdate = AtomicLong();
    @JvmStatic
    fun log(
        host: String,
        method: NETWORK_METHODS,
        duration: Long,
        inputTraffic: Long,
        outputTraffic: Long
    ) {
        val methodWithFilter = map[host]?.first ?: method
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastUpdate.get() > 10000) {
            map.filterValues { (currentTimeMillis - it.second) > 10000 }.keys.forEach(map::remove)
            lastUpdate.set(currentTimeMillis);
        }
        val l = RequestLog(
            host, methodWithFilter.name, userId.toString(),
            currentTimeMillis, inputTraffic, outputTraffic, duration
        )
        synchronized(storage) {
            storage += l
        }
    }

    @JvmStatic
    fun register(host: String, method: NETWORK_METHODS) {
        map[host] = Pair(method, System.currentTimeMillis());
    }


    val SEND_LOG_TIME_SECOND = 1
    @JvmStatic
    fun runSending() {
        Thread {
            while (true) {
                Thread.sleep(SEND_LOG_TIME_SECOND.toLong() * 1000)
                sendLog()
            }
        }.start()
    }

    @JvmStatic
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
