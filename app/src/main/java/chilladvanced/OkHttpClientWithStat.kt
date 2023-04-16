package chilladvanced

import android.util.Log
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request

class OkHttpClientWithStat(val okHttpClient: OkHttpClient) {
    fun newCall(request: Request): Call {
        Logger.register(request.url.host, NETWORK_METHODS.OK_HTTP_CLIENT)
        return okHttpClient.newCall(request)
    }
}