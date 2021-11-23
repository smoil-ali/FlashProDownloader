package com.reactive.flashprodownloader.WebView

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import android.util.Patterns
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.reactive.flashprodownloader.Interfaces.WebViewCallbacks


import android.webkit.JavascriptInterface
import com.reactive.flashprodownloader.Helper.Utils
import com.reactive.flashprodownloader.model.FlashWindow


@SuppressLint("JavascriptInterface")
class MyWebViewClient(
    private val listener: WebViewCallbacks,
    val window: FlashWindow,
    val filters: Array<String>,
    val customWebView: CustomWebView
): WebViewClient() {
    private val TAG = MyWebViewClient::class.simpleName

    init {
        Log.i(TAG, "init: start ")
        customWebView.addJavascriptInterface(this, "browser");
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)

        var page: String? = null
        var title: String? = null
        if (view != null) {
            page = view.url
            title = view.title
        }


        val mLowerCase = url?.lowercase()
        var urlMightBeVideo = false
        for (filter in filters) {
            if (mLowerCase!!.contains(filter)) {
                Log.i(TAG, "onLoadResource: $mLowerCase")
                urlMightBeVideo = true
                break
            }
        }

        if (urlMightBeVideo){
            FsdEngine.startEngine(url!!, page!!,title,listener)
        }
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        listener.OnPageStart(window)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if(view?.isShown!!){
            listener.OnPageFinish(window,view)
        }


    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (Patterns.WEB_URL.matcher(request?.url.toString()).matches()){
            Log.i(TAG, "shouldOverrideUrlLoading: ${request?.url.toString()}")
        }
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (Patterns.WEB_URL.matcher(url).matches()){
            window.url = url
            listener.OnNewPage(window)
        }
        return super.shouldOverrideUrlLoading(view, url)
    }


    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
    }


    @JavascriptInterface
    fun getVideoData(link: String){
        Utils.showToast(customWebView.context,link)
        Log.i(TAG, "getVideoData: $link")
    }

}