package com.reactive.flashprodownloader.Interfaces

import android.webkit.WebView
import com.reactive.flashprodownloader.model.FlashLightDownloadPro
import com.reactive.flashprodownloader.model.FlashWindow


interface WebViewCallbacks {
    fun OnPageStart(window: FlashWindow)
    fun OnPageFinish(window: FlashWindow,webView: WebView)
    fun OnLoadingResource(window: FlashWindow)
    fun OnNewPage(window: FlashWindow)
    fun OnProgressChange(progress: Int)
    fun OnScroll()
    fun milGaiVideo(lightDownloadPro: FlashLightDownloadPro)
    fun NotFound()

}