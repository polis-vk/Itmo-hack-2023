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

/**
 * Defining variable types.
 *
 * @param url the type of a urls in logs.
 * @param method the type of a metods in logs.
 * @param userId the type of a user_id in logs.
 * @param unixtime the type of a time logging.
 * @param inputTraffic the type of a incoming traffic in logs.
 * @param outputTraffic the type of a outgoing traffic in logs.
 * @param duration the type of a url in logs.
 */

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

/**
 *  Object containing the main functions used
 */

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
    var lastUpdate = AtomicLong()


    /**
     * Parameters accepted by the logger.
     *
     * @param host the name of request host
     * @param method the name method that usnig for network request
     * @param duration the time in millisecond that network connection spend to
     * @param inputTraffic the traffic in bytest that connection sends to server
     * @param outputTraffic the traffic in bytest that connection received from server
     */
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

    /**
     * Registers an expected call to the host from a supplied method.
     *
     * @param host the name of host that connection will be sent
     * @param method the name of network method that will be invocated
     */
    @JvmStatic
    fun register(host: String, method: NETWORK_METHODS) {
        map[host] = Pair(method, System.currentTimeMillis());
    }

    /**
     * frequency of sending statistic to server
     */
    val SEND_LOG_TIME_SECOND = 5

    /**
     * Run logger.
     */
    @JvmStatic
    fun runSending() {
        Thread {
            while (true) {
                Thread.sleep(SEND_LOG_TIME_SECOND.toLong() * 1000)
                sendLog()
            }
        }.start()
    }

    /**
     * Getting logs to the server.
     *
     */
    @JvmStatic
    private fun sendLog() {
        val lgs = synchronized(storage) {
            storage.toList()
        }

        if (lgs.isEmpty()) {
            return
        }

        /**
         * Connecting to the server and getting logs.
         * @throws IOException if failed to log network request.
         */

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
