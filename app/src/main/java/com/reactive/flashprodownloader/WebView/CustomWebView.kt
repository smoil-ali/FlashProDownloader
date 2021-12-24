package com.reactive.flashprodownloader.WebView

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.webkit.WebView
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.model.FlashWindow
import com.reactive.flashprodownloader.Interfaces.WebViewCallbacks


@SuppressLint("SetJavaScriptEnabled")
class CustomWebView(private val listener: WebViewCallbacks,
                    private val window: FlashWindow,
                    private val mContext: Context)  {
    private val TAG = CustomWebView::class.simpleName
    private val mWebClient:MyWebViewClient
    private var scroll = true
    private val webView:WebView

    init {
        webView = WebView(mContext)
        webView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scroll) {
                scroll = false
                listener.OnScroll()
            }
        }
        webView.settings.javaScriptEnabled = true
        webView.settings.useWideViewPort = true
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.settings.domStorageEnabled = true
        webView.isHorizontalScrollBarEnabled = true
        webView.isVerticalScrollBarEnabled = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.setSupportMultipleWindows(true)
        webView.settings.loadWithOverviewMode = true
        FsdEngine.initializeEngine()
        mWebClient  = MyWebViewClient(listener,window,mContext.resources
            .getStringArray(R.array.videourl_filters),this)
        webView.webViewClient = mWebClient
        webView.webChromeClient = (object : MyChromeClient(mContext) {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                listener.OnProgressChange(newProgress)
            }
        })

    }

    fun getWebView():WebView{
        return webView
    }

    fun stopEngine(){
        mWebClient.stopEngine()
    }
}