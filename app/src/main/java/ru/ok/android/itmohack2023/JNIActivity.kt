package ru.ok.android.itmohack2023

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import chilladvanced.NativeLibraryTracker
import org.json.JSONArray
import org.json.JSONObject

class JNIActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jniactivity)
        Threads.ioPool.execute {
            val nativeTracker = NativeLibraryTracker();
            nativeTracker.start(this)
            var result = nativeFunction() ?: return@execute
            nativeTracker.stop(this)
            result = result.dropWhile { it != '{' }

            val textJson = JSONObject(result)
            val act =
                textJson.getString("activity")
            runOnUiThread {
                findViewById<TextView>(R.id.result).text = act
            }
        }
    }

    external fun nativeFunction(): String?

    companion object {
        init {
            System.loadLibrary("jnisocket");
        }
    }
}