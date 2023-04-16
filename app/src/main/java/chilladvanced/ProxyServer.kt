package chilladvanced

import android.util.Log
import chilladvanced.SocketConnect.Companion.connectAndCountTraffic
import com.google.common.collect.Lists
import java.io.IOException
import java.io.InputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.ProxySelector
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.net.URI
import java.net.URISyntaxException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @hide
 */
class ProxyServer(port: Int, logger: Logger, connector: ConnectAndCountTrafficInterface) : Thread() {
    private val logger: Logger
    private val threadExecutor: ExecutorService = Executors.newCachedThreadPool()
    var mIsRunning = false
    private var serverSocket: ServerSocket? = null
    val port: Int

    private inner class ProxyConnection(private val connection: Socket) : Runnable {
        override fun run() {
            val urlStringReq: String
            try {
                val requestLine = getLine(connection.getInputStream())
                Log.i(TAG, "REQUEST ON PROXY: $requestLine")
                val splitLine =
                    requestLine.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (splitLine.size < 3) {
                    connection.close()
                    return
                }
                Log.v(TAG, " -> REQUEST: $requestLine")
                val requestType = splitLine[0]
                var urlString = splitLine[1]
                urlStringReq = urlString
                val httpVersion = splitLine[2]
                var url: URI? = null
                val host: String
                var port: Int
                if (requestType == CONNECT) {
                    val hostPortSplit =
                        urlString.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    host = hostPortSplit[0]
                    // Use default SSL port if not specified. Parse it otherwise
                    port = if (hostPortSplit.size < 2) {
                        443
                    } else {
                        try {
                            hostPortSplit[1].toInt()
                        } catch (nfe: NumberFormatException) {
                            connection.close()
                            return
                        }
                    }
                    urlString = "Https://$host:$port"
                } else {
                    try {
                        url = URI(urlString)
                        host = url.host
                        port = url.port
                        if (port < 0) {
                            port = 80
                        }
                    } catch (e: URISyntaxException) {
                        connection.close()
                        return
                    }
                }
                var list: List<Proxy> = Lists.newArrayList()
                try {
                    list = ProxySelector.getDefault().select(URI(urlString))
                } catch (e: URISyntaxException) {
                    e.printStackTrace()
                }
                var server: Socket? = null
                for (proxy in list) {
                    try {
                        var forward = false
                        if (proxy != Proxy.NO_PROXY) {
                            // Only Inets created by PacProxySelector.
                            val inetSocketAddress = proxy.address() as InetSocketAddress
                            server = Socket(
                                inetSocketAddress.hostName,
                                inetSocketAddress.port
                            )
                            if ("127.0.0.1" == InetAddress.getByName(
                                    inetSocketAddress.hostName
                                ).hostAddress != true
                            ) {
                                server = Socket(
                                    inetSocketAddress.hostName,
                                    inetSocketAddress.port
                                )
                                sendLine(server, requestLine)
                                forward = true
                            }
                        }
                        if (forward != true) {
                            server = Socket(host, port)
                            if (requestType == CONNECT) {
                                Log.v(TAG, " -> CONNECT: $host:$port")
                                skipToRequestBody(connection)
                                // No proxy to respond so we must.
                                sendLine(connection, HTTP_OK)
                            } else {
                                // Proxying the request directly to the origin server.
                                Log.v(TAG, " -> DIRECT: $host:$port")
                                sendAugmentedRequestToHost(
                                    connection, server,
                                    requestType, url, httpVersion
                                )
                            }
                        }
                    } catch (ioe: IOException) {
                        if (Log.isLoggable(TAG, Log.VERBOSE)) {
                            Log.v(TAG, "Unable to connect to proxy $proxy", ioe)
                        }
                    }
                    if (server != null) {
                        break
                    }
                }
                if (list.isEmpty()) {
                    server = Socket(host, port)
                    if (requestType == CONNECT) {
                        skipToRequestBody(connection)
                        // No proxy to respond so we must.
                        sendLine(connection, HTTP_OK)
                    } else {
                        // Proxying the request directly to the origin server.
                        sendAugmentedRequestToHost(
                            connection, server,
                            requestType, url, httpVersion
                        )
                    }
                }
                val start = LongArray(1)
                start[0] = System.currentTimeMillis()
                // Pass data back and forth until complete.
                if (server != null) {
                    SocketConnectHttpChecker.connectAndCountTraffic(
                        connection,
                        server, { inputTraffic, outputTraffic ->
                            logger.log(
                                host, "UNKNOWN", 0,
                                inputTraffic, outputTraffic
                            );
                        }) {
                        logger.log(
                            host, "UNKNOWN", System.currentTimeMillis() - start[0],
                            0, 0
                        )
                        start[0] = System.currentTimeMillis()
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Problem Proxying", e)
            }
            try {
                connection.close()
            } catch (ioe: IOException) {
                // Do nothing
            }
        }

        /**
         * Sends HTTP request-line (i.e. the first line in the request)
         * that contains absolute path of a given absolute URI.
         *
         * @param server server to send the request to.
         * @param requestType type of the request, a.k.a. HTTP method.
         * @param absoluteUri absolute URI which absolute path should be extracted.
         * @param httpVersion version of HTTP, e.g. HTTP/1.1.
         * @throws IOException if the request-line cannot be sent.
         */
        @Throws(IOException::class)
        private fun sendRequestLineWithPath(
            server: Socket, requestType: String,
            absoluteUri: URI?, httpVersion: String
        ) {
            val absolutePath = getAbsolutePathFromAbsoluteURI(absoluteUri)
            val outgoingRequestLine = String.format(
                "%s %s %s",
                requestType, absolutePath, httpVersion
            )
            sendLine(server, outgoingRequestLine)
        }

        /**
         * Extracts absolute path form a given URI. E.g., passing
         * `http://google.com:80/execute?query=cat#top`
         * will result in `/execute?query=cat#top`.
         *
         * @param uri URI which absolute path has to be extracted,
         * @return the absolute path of the URI,
         */
        private fun getAbsolutePathFromAbsoluteURI(uri: URI?): String {
            val rawPath = uri!!.rawPath
            val rawQuery = uri.rawQuery
            val rawFragment = uri.rawFragment
            val absolutePath = StringBuilder()
            if (rawPath != null) {
                absolutePath.append(rawPath)
            } else {
                absolutePath.append("/")
            }
            if (rawQuery != null) {
                absolutePath.append("?").append(rawQuery)
            }
            if (rawFragment != null) {
                absolutePath.append("#").append(rawFragment)
            }
            return absolutePath.toString()
        }

        @Throws(IOException::class)
        private fun getLine(inputStream: InputStream): String {
            val buffer = StringBuilder()
            var byteBuffer = inputStream.read()
            if (byteBuffer < 0) return ""
            do {
                if (byteBuffer != '\r'.code) {
                    buffer.append(byteBuffer.toChar())
                }
                byteBuffer = inputStream.read()
            } while (byteBuffer != '\n'.code && byteBuffer >= 0)
            return buffer.toString()
        }

        @Throws(IOException::class)
        private fun sendLine(socket: Socket, line: String) {
            val os = socket.getOutputStream()
            os.write(line.toByteArray())
            os.write('\n'.code)
            os.flush()
        }

        /**
         * Reads from socket until an empty line is read which indicates the end of HTTP headers.
         *
         * @param socket socket to read from.
         * @throws IOException if an exception took place during the socket read.
         */
        @Throws(IOException::class)
        private fun skipToRequestBody(socket: Socket) {
            while (getLine(socket.getInputStream()).length != 0);
        }

        /**
         * Sends an augmented request to the final host (DIRECT connection).
         *
         * @param src socket to read HTTP headers from.The socket current position should point
         * to the beginning of the HTTP header section.
         * @param dst socket to write the augmented request to.
         * @param httpMethod original request http method.
         * @param uri original request absolute URI.
         * @param httpVersion original request http version.
         * @throws IOException if an exception took place during socket reads or writes.
         */
        @Throws(IOException::class)
        private fun sendAugmentedRequestToHost(
            src: Socket, dst: Socket,
            httpMethod: String, uri: URI?, httpVersion: String
        ) {
            sendRequestLineWithPath(dst, httpMethod, uri, httpVersion)
            filterAndForwardRequestHeaders(src, dst)

            // Currently the proxy does not support keep-alive connections; therefore,
            // the proxy has to request the destination server to close the connection
            // after the destination server sent the response.
            sendLine(dst, "Connection: close")

            // Sends and empty line that indicates termination of the header section.
            sendLine(dst, "")
        }

        /**
         * Forwards original request headers filtering out the ones that have to be removed.
         *
         * @param src source socket that contains original request headers.
         * @param dst destination socket to send the filtered headers to.
         * @throws IOException if the data cannot be read from or written to the sockets.
         */
        @Throws(IOException::class)
        private fun filterAndForwardRequestHeaders(src: Socket, dst: Socket) {
            var line: String
            do {
                line = getLine(src.getInputStream())
                if (line.length > 0 && !shouldRemoveHeaderLine(line)) {
                    sendLine(dst, line)
                }
            } while (line.length > 0)
        }

        /**
         * Returns true if a given header line has to be removed from the original request.
         *
         * @param line header line that should be analysed.
         * @return true if the header line should be removed and not forwarded to the destination.
         */
        private fun shouldRemoveHeaderLine(line: String): Boolean {
            val colIndex = line.indexOf(":")
            if (colIndex != -1) {
                val headerName = line.substring(0, colIndex).trim { it <= ' ' }
                if (headerName.regionMatches(
                        0, HEADER_CONNECTION, 0,
                        HEADER_CONNECTION.length, ignoreCase = true
                    )
                    || headerName.regionMatches(
                        0, HEADER_PROXY_CONNECTION,
                        0, HEADER_PROXY_CONNECTION.length, ignoreCase = true
                    )
                ) {
                    return true
                }
            }
            return false
        }
    }

    init {
        this.port = port
        this.logger = logger
    }

    override fun run() {
        try {
            serverSocket = ServerSocket(port)
            while (mIsRunning) {
                try {
                    val socket = serverSocket!!.accept()
                    // Only receive local connections.
                    if (socket.inetAddress.isLoopbackAddress) {
                        val parser = ProxyConnection(socket)
                        threadExecutor.execute(parser)
                    } else {
                        socket.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } catch (e: SocketException) {
            Log.e(TAG, "Failed to start proxy server", e)
        } catch (e1: IOException) {
            Log.e(TAG, "Failed to start proxy server", e1)
        }
        mIsRunning = false
    }

    @Synchronized
    fun startServer() {
        mIsRunning = true
        start()
    }

    @Synchronized
    fun stopServer() {
        mIsRunning = false
        if (serverSocket != null) {
            try {
                serverSocket!!.close()
                serverSocket = null
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    val isBound: Boolean
        get() = port != -1

    companion object {
        private const val CONNECT = "CONNECT"
        private const val HTTP_OK = "HTTP/1.1 200 OK\n"
        private const val TAG = "ProxyServer"

        // HTTP Headers
        private const val HEADER_CONNECTION = "connection"
        private const val HEADER_PROXY_CONNECTION = "proxy-connection"
    }
}