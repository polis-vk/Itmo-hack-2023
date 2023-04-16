package chilladvanced

import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.util.function.BiConsumer

/**
 * @hide
 */
class SocketConnect(from: Socket, to: Socket) : Thread() {
    private val from: InputStream
    private val to: OutputStream
    private var trafficCounter: Long = 0

    init {
        this.from = from.getInputStream()
        this.to = to.getOutputStream()
        start()
    }

    override fun run() {
        val buffer = ByteArray(512)
        try {
            while (true) {
                val r = from.read(buffer)
                if (r < 0) {
                    break
                }
                Log.d("Socket", String(buffer, 0, r))
                trafficCounter += r.toLong()
                to.write(buffer, 0, r)
            }
            from.close()
            to.close()
        } catch (io: IOException) {
        }
    }

    companion object {
        @JvmStatic
        fun connectAndCountTraffic(
            first: Socket,
            second: Socket,
            consumer: BiConsumer<Long, Long>,
            runnable: Runnable
        ) {
            try {
                val sc1 = SocketConnect(first, second)
                val sc2 = SocketConnect(second, first)
                while (sc1.isAlive || sc2.isAlive) {
                    try {
                        sleep(1000);
                    } catch (e: InterruptedException) {
                    }
                    consumer.accept(sc2.trafficCounter, sc1.trafficCounter)
                    sc2.trafficCounter = 0
                    sc1.trafficCounter = 0
                }
                runnable.run()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

