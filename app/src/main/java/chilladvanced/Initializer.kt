package chilladvanced

object Initializer {
    private fun setProxyHostHttp(host: String) {
        System.setProperty("http.proxyHost", host);
    }

    private fun setProxyPortHttp(host: String) {
        System.setProperty("http.proxyPort", host);
    }

    private fun setProxyHostAnother(host: String) {
        System.setProperty("https.proxyHost", host);
        System.setProperty("ftp.proxyHost", host);
        System.setProperty("socksProxyHost", host);
    }

    private fun setProxyPortAnother(port: String) {
        System.setProperty("https.proxyPort", port);
        System.setProperty("ftp.proxyPort", port);
        System.setProperty("socksProxyPort", port);
    }

    fun initPropertiesAndStartProxies(proxyPortHttp: Int = 3128, proxyPortAnother: Int = 3129) {
        setProxyHostHttp("127.0.0.1")
        setProxyHostAnother("127.0.0.1")
        setProxyPortHttp("$proxyPortHttp")
        setProxyPortAnother("$proxyPortAnother")

        ProxyServer(
            proxyPortHttp,
            SocketConnectHttpChecker.Companion::connectAndCountTraffic
        ).startServer()
        ProxyServer(
            proxyPortAnother,
            SocketConnect.Companion::connectAndCountTraffic
        ).startServer()

        Logger.runSending()
    }
    private fun delayShutdownEvent(millis: Long) {
        Thread.setDefaultUncaughtExceptionHandler { t, e -> // perform any necessary cleanup or shutdown tasks here
            println("Uncaught exception occurred: " + e.message)
            try {
                // delay the shutdown for 2 seconds
                Thread.sleep(millis)
            } catch (ex: InterruptedException) {
                // handle the InterruptedException if necessary
                ex.printStackTrace()
            }
            // shut down the program
            System.exit(1)
        }
    }
}