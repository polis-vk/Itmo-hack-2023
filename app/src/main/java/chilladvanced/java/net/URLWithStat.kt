package chilladvanced.java.net

import chilladvanced.Logger
import chilladvanced.NETWORK_METHODS
import java.io.IOException
import java.io.InputStream
import java.io.Serializable
import java.net.Proxy
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.net.URLConnection

class URLWithStat(private val url: URL) : Serializable {
    val port: Int = url.port
    val defaultPort: Int = url.defaultPort
    val protocol: String? = url.protocol
    val host: String? = url.host
    val file: String? = url.file
    val ref: String? = url.ref
    override fun equals(other: Any?): Boolean = url == other
    override fun hashCode(): Int = url.hashCode()
    fun sameFile(other: URL?): Boolean = url.sameFile(other)
    override fun toString(): String = url.toString()
    fun toExternalForm(): String? = url.toExternalForm()

    @Throws(URISyntaxException::class)
    fun toURI(): URI? = url.toURI()

    //TODO
    @Throws(IOException::class)
    fun openConnection(): URLConnection {
        Logger.register(url.host, NETWORK_METHODS.URL_CONNECTION)
        return url.openConnection()
    }

    @Throws(IOException::class)
    fun openConnection(proxy: Proxy?): URLConnection {
        Logger.register(url.host, NETWORK_METHODS.URL_CONNECTION)
        return url.openConnection(proxy);
    }

    @Throws(IOException::class)
    fun openStream(): InputStream? = url.openStream()

    @get:Throws(IOException::class)
    val content: Any? = url.content

    @Throws(IOException::class)
    fun getContent(classes: Array<Class<*>?>?): Any? = url.getContent(classes)
}