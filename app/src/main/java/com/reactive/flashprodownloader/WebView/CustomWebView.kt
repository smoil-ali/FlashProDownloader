package com.reactive.flashprodownloader.WebView

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.webkit.WebView
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.model.FlashWindow
import com.reactive.flashprodownloader.Interfaces.WebViewCallbacks


@SuppressLint("SetJavaScriptEnabled")
class CustomWebView(private val listener: WebViewCallbacks,
                    private val window: FlashWindow,
                    private val mContext: Context) : WebView(mContext) {
    private val TAG = CustomWebView::class.simpleName
    private var scroll = true

    init {
        settings.javaScriptEnabled = true
        settings.useWideViewPort = true
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.domStorageEnabled = true
        isHorizontalScrollBarEnabled = true
        isVerticalScrollBarEnabled = true
        settings.allowUniversalAccessFromFileURLs = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.setSupportMultipleWindows(true)
        settings.loadWithOverviewMode = true
        webViewClient = MyWebViewClient(listener,window,mContext.resources
            .getStringArray(R.array.videourl_filters),this)
        webChromeClient = (object : MyChromeClient(mContext) {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                listener.OnProgressChange(newProgress)
            }
        })
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (scroll){
            scroll = false
            listener.OnScroll()
        }

        Log.i(TAG, "onScrollChanged: ${t}")
    }



}