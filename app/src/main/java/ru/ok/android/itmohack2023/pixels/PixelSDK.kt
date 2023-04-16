package ru.ok.android.itmohack2023.pixels

import android.content.Context
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.acsbendi.requestinspectorwebview.RequestInspectorOptions
import com.acsbendi.requestinspectorwebview.RequestInspectorWebViewClient
import com.acsbendi.requestinspectorwebview.WebViewRequest
import com.google.android.exoplayer2.ExoPlayer
import com.squareup.picasso.Picasso
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import ru.ok.android.itmohack2023.pixels.JNI.JNIClient
import ru.ok.android.itmohack2023.pixels.exoPlayer.PixelExoPlayerProvider
import ru.ok.android.itmohack2023.pixels.okHttp.OkHttpInterceptor
import ru.ok.android.itmohack2023.pixels.picasso.PicassoClientProvider
import ru.ok.android.itmohack2023.pixels.webview.PixelsWebViewClient

class PixelSDK {

    fun configure(path: String) : Request.Builder{
        return Request.Builder()
            .url("http://$path/upload")
    }
    fun getExoPlayerClient(context: Context): ExoPlayer {
        return PixelExoPlayerProvider().getClient(context)
    }

    fun getJNIClient(command: Runnable?) {
        JNIClient().execute(command)
    }

    fun getOkHttpClient(chain: Interceptor.Chain): Response {
        return OkHttpInterceptor().intercept(chain)
    }

    fun getPicassoClient(context: Context): Picasso {
        return PicassoClientProvider().getClient(context)
    }

    fun getWebViewClient(
        webView: WebView,
        options: RequestInspectorOptions = RequestInspectorOptions(),
        view: WebView,
        webViewRequest: WebViewRequest

    ): WebResourceResponse? {
        return PixelsWebViewClient(webView, options).shouldInterceptRequest(view, webViewRequest)
    }

}