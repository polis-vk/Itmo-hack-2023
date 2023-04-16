package ru.ok.android.itmohack2023

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.net.TrafficStats
import androidx.appcompat.app.AppCompatActivity
import ru.ok.android.itmohack2023.db.DbManager
import ru.ok.android.itmohack2023.db.DbModel
import ru.ok.android.itmohack2023.db.DbSheduleSaver
import ru.ok.android.itmohack2023.db.DbSingletone
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {

    var initialTxBytes = TrafficStats.getTotalTxBytes()
    var initialRxBytes = TrafficStats.getTotalRxBytes()


    lateinit var logReader: BufferedReader
    override fun onCreate(savedInstanceState: Bundle?) {
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

        val dbSheduleSaver = DbSheduleSaver (this)
        dbSheduleSaver.postOkRequest()


    }

    fun setupNetworkTrack() {
        while(true) {
            // Wait for a specific time interval
            Thread.sleep(5000)

            val currentTxBytes = TrafficStats.getTotalTxBytes()
            val currentRxBytes = TrafficStats.getTotalRxBytes()

            val txBytes = currentTxBytes - initialTxBytes
            val rxBytes = currentRxBytes - initialRxBytes

            if (txBytes > 1000000 || rxBytes > 1000000) {
                initialTxBytes = TrafficStats.getTotalTxBytes()
                initialRxBytes = TrafficStats.getTotalRxBytes()

                getLogs()
            }
        }
    }

    fun getLogs() {
        var result = ""

        val logcatThread = Thread(Runnable {
            try {
                val process = Runtime.getRuntime().exec("logcat -v time") // "-v time" adds timestamp to log messages
                logReader = BufferedReader(InputStreamReader(process.inputStream))

                var line: String?
                while (logReader.readLine().also { line = it } != null) {
                    result += line

                }
            } catch (e: IOException) {
                Log.e("ERROR", "Error reading logcat")
            }

            val modelInstance = DbModel(
                userID = DbSingletone.userId,
                other = result
            )

            val tmpDb = DbManager (DbSingletone.context)
            tmpDb.openDb()
            tmpDb.insertToDb(modelInstance)
        })

        logcatThread.start()

        // Stop reading logcat after 5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            logReader.close()
            logcatThread.interrupt()
        }, 5000)
    }

}