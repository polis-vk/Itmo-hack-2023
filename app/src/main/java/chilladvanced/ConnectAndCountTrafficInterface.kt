package chilladvanced

import java.net.Socket
import java.util.function.BiConsumer

public interface ConnectAndCountTrafficInterface {
    fun connectAndCountTraffic(
        first: Socket,
        second: Socket,
        consumer: BiConsumer<Long, Long>,
        runnable: Runnable
    )
}