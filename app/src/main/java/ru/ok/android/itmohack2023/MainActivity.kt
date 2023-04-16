package ru.ok.android.itmohack2023

import android.content.Intent
import android.net.Proxy
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import chilladvanced.Logger
import chilladvanced.ProxyServer
import chilladvanced.SocketConnect
import chilladvanced.SocketConnectHttpChecker

class MainActivity : AppCompatActivity() {


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

    override fun onCreate(savedInstanceState: Bundle?) {
        val proxyPortHttp = 3128;
        val proxyPortAnother = 3129;
        setProxyHostHttp("127.0.0.1")
        setProxyHostAnother("127.0.0.1")
        setProxyPortHttp("$proxyPortHttp")
        setProxyPortAnother("$proxyPortAnother")

        val logger = Logger()
        logger.runSending()

        ProxyServer(
            proxyPortHttp,
            logger,
            SocketConnectHttpChecker.Companion::connectAndCountTraffic
        ).startServer()
        ProxyServer(
            proxyPortAnother,
            logger,
            SocketConnect.Companion::connectAndCountTraffic
        ).startServer()


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.url_connection).setOnClickListener {
            startActivity(Intent(this, UrlConnectionActivity::class.java))
        }
        findViewById<View>(R.id.make_crash).setOnClickListener {
            0 / 0
        }
        findViewById<View>(R.id.web_view_button).setOnClickListener {
            startActivity(Intent(this, WebViewActivity::class.java))
        }
        findViewById<View>(R.id.jni_button).setOnClickListener {
            startActivity(Intent(this, JNIActivity::class.java))
        }
        findViewById<View>(R.id.curl).setOnClickListener {
            startActivity(Intent(this, CurlActivity::class.java))
        }
        findViewById<View>(R.id.ok_http).setOnClickListener {
            startActivity(Intent(this, OkHttpActivity::class.java))
        }
        findViewById<View>(R.id.exo_player).setOnClickListener {
            startActivity(Intent(this, ExoPlayerActivity::class.java))
        }
        findViewById<View>(R.id.fresco).setOnClickListener {
            startActivity(Intent(this, FrescoActivity::class.java))
        }
        findViewById<View>(R.id.retrofit).setOnClickListener {
            startActivity(Intent(this, RetrofitActivity::class.java))
        }
        findViewById<View>(R.id.glide).setOnClickListener {
            startActivity(Intent(this, GlideActivity::class.java))
        }
        findViewById<View>(R.id.picasso).setOnClickListener {
            startActivity(Intent(this, PicassoActivity::class.java))
        }
        findViewById<View>(R.id.downloadmanager).setOnClickListener {
            startActivity(Intent(this, DownloadManagerActivity::class.java))
        }
    }
}