package ru.ok.android.itmohack2023.pixels.webview

import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.acsbendi.requestinspectorwebview.RequestInspectorOptions
import com.acsbendi.requestinspectorwebview.RequestInspectorWebViewClient
import com.acsbendi.requestinspectorwebview.WebViewRequest
import ru.ok.android.itmohack2023.db.DbManager
import ru.ok.android.itmohack2023.db.DbModel
import ru.ok.android.itmohack2023.db.DbSingletone
import ru.ok.android.itmohack2023.pixels.MeasurementService

class PixelsWebViewClient(private val webView: WebView,
                          options: RequestInspectorOptions = RequestInspectorOptions()
): RequestInspectorWebViewClient(webView, options) {
    override fun shouldInterceptRequest(
        view: WebView,
        webViewRequest: WebViewRequest
    ): WebResourceResponse? {
        val exception = Exception()
        val stackTrace = exception.stackTrace

        val modelInstance = DbModel(
            userID = DbSingletone.userId,
            client = "WebView",
            path = webViewRequest.url,
            type = webViewRequest.method,
            requestSize = webViewRequest.body.length.toString(),
            className = stackTrace[4].className,
            methodName = stackTrace[4].methodName
        )

        val tmpDb = DbManager (DbSingletone.context)
        tmpDb.openDb()
        tmpDb.insertToDb(modelInstance)

        return super.shouldInterceptRequest(view, webViewRequest)
    }
}