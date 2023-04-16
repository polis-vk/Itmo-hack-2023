package chilladvanced

import android.util.Log
import okhttp3.internal.wait
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Integer.min
import java.lang.StringBuilder
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.function.BiConsumer

/**
 * @hide
 */
class SocketConnectHttpChecker(from: Socket, to: Socket, val addJson: Runnable) : Thread() {
    private val from: InputStream
    private val to: OutputStream
    private val trafficCounter: AtomicLong = AtomicLong(0)

    init {
        this.from = from.getInputStream()
        this.to = to.getOutputStream()
        start()
    }

    override fun run() {
        val buffer = ByteArray(512)
        try {
            var scanner = HttpScanner()
            while (true) {
                val r = from.read(
                    buffer, 0, min(
                        buffer.size,
                        if (scanner.bodySize > 0) scanner.bodySize - scanner.bodyIndex else buffer.size
                    )
                )
                if (r < 0) {
                    break
                }
                Log.d("Socket", String(buffer, 0, r))
                trafficCounter.addAndGet(r.toLong())
                to.write(buffer, 0, r)
                scanner.parse(buffer, 0, r)
                if (scanner.bodyIndex == scanner.bodySize) {
                    scanner = HttpScanner()
                }
            }
            from.close()
            to.close()
        } catch (io: IOException) {
        }
    }

    class HttpScanner() {
        var method: String? = null
            get() = field
        var request: String? = null
            get() = field
        var bodySize = 0
            get() = field
        var bodyIndex = -1
            get() = field
        private val contentLengthKeyString = "Content-Length: ".toByteArray()
        private var contentLengthKeyIndex = 0
        private val contentLengthValue = StringBuilder()
        private var readContentLengthValue = false;
        private var bodySeparatorIndex = 0
        private var bodySeparator = "\r\n\r\n".toByteArray()

        fun parse(bytes: ByteArray, offset: Int, size: Int) {
            for (i in offset until offset + size) {
                if (contentLengthKeyIndex >= 0 &&
                    contentLengthKeyIndex < contentLengthKeyString.size - 1
                ) {
                    if (bytes[i] != contentLengthKeyString[contentLengthKeyIndex++]) {
                        contentLengthKeyIndex = 0
                    }
                } else if (contentLengthKeyIndex == contentLengthKeyString.size - 1) {
                    if (bytes[i] != contentLengthKeyString[contentLengthKeyIndex++]) {
                        contentLengthKeyIndex = 0
                    } else {
                        readContentLengthValue = true;
                    }
                } else if (readContentLengthValue && '0' <= bytes[i].toInt().toChar()
                    && bytes[i].toInt().toChar() <= '9'
                ) {
                    contentLengthValue.append(bytes[i].toInt().toChar())
                } else if (readContentLengthValue) {
                    readContentLengthValue = false
                    bodySize = Integer.parseInt(contentLengthValue.toString())
                }
                if (bodySeparatorIndex < bodySeparator.size - 1 &&
                    bytes[i] != bodySeparator[bodySeparatorIndex++]
                ) {
                    bodySeparatorIndex = 0
                } else if (bodySeparatorIndex == bodySeparator.size - 1) {
                    if (bytes[i] != bodySeparator[bodySeparatorIndex++]) {
                        bodySeparatorIndex = 0
                    } else {
                        bodyIndex = 0;
                    }
                }
                if (bodyIndex >= 0 && bodyIndex < bodySize) {
                    bodyIndex++
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun connectAndCountTraffic(
            first: Socket,
            second: Socket,
            consumerTraffic: BiConsumer<Long, Long>,
            consumerTime: Runnable
        ) {
            try {
                val jsons1 = AtomicInteger(0)
                val jsons2 = AtomicInteger(0)
                val sc1 = SocketConnectHttpChecker(first, second)
                { if (jsons1.incrementAndGet() <= jsons2.get()) consumerTime.run() }
                val sc2 = SocketConnectHttpChecker(second, first)
                { if (jsons2.incrementAndGet() <= jsons1.get()) consumerTime.run() }
                while (sc1.isAlive || sc2.isAlive) {
                    try {
                        sleep(1000);
                    } catch (e: InterruptedException) {
                    }
                    consumerTraffic.accept(
                        sc1.trafficCounter.getAndSet(0),
                        sc1.trafficCounter.getAndSet(0)
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}