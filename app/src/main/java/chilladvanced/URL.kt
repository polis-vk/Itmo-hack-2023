package chilladvanced

import java.io.IOException
import java.io.InputStream
import java.io.Serializable
import java.net.Proxy
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.net.URLConnection

class URL(private val url: URL) : Serializable {
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
        val start = System.currentTimeMillis();
        val res = url.openConnection()
        val duration = System.currentTimeMillis() - start;
//        Logger.log(toString(), "URL.OPEN_CONNECTIONS", duration);
        return res;
    }

    @Throws(IOException::class)
    fun openConnection(proxy: Proxy?): URLConnection {
        val start = System.currentTimeMillis();
        val res = url.openConnection(proxy)
        val duration = System.currentTimeMillis() - start;
//        Logger.log(toString(), "URL.OPEN_CONNECTIONS_PROXY", duration);
        return res;
    }

    @Throws(IOException::class)
    fun openStream(): InputStream? = url.openStream()

    @get:Throws(IOException::class)
    val content: Any? = url.content

    @Throws(IOException::class)
    fun getContent(classes: Array<Class<*>?>?): Any? = url.getContent(classes)
}