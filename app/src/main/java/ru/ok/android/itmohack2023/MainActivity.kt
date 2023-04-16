package ru.ok.android.itmohack2023

import android.app.ActivityManager
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import ru.itmo.networkmonitor.NetworkMonitorTask

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NetworkMonitorTask(
            getSystemService(NETWORK_STATS_SERVICE) as NetworkStatsManager, getSystemService(
                Context.ACTIVITY_SERVICE
            ) as ActivityManager,
            listOf("ru.ok")
        ).start()
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