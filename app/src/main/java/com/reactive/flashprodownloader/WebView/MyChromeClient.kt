package com.reactive.flashprodownloader.WebView

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import com.reactive.flashprodownloader.Activities.MainActivity


open class MyChromeClient(val context: Context): WebChromeClient() {
    private val TAG = MyChromeClient::class.simpleName
    private var mCustomView: View? = null
    private var mCustomViewCallback: CustomViewCallback? = null
    protected var mFullscreenContainer: FrameLayout? = null
    private var mOriginalOrientation = 0
    private var mOriginalSystemUiVisibility = 0

    override fun getDefaultVideoPoster(): Bitmap? {
        return if (mCustomView == null) {
            null
        } else BitmapFactory.decodeResource(
            context.applicationContext.resources,
            2130837573
        )
    }

    override fun onHideCustomView() {
        ((context as MainActivity).window
            .decorView as FrameLayout).removeView(mCustomView)
        mCustomView = null
        context.window.decorView.systemUiVisibility = mOriginalSystemUiVisibility
        context.requestedOrientation = mOriginalOrientation
        mCustomViewCallback!!.onCustomViewHidden()
        mCustomViewCallback = null
    }

    override fun onShowCustomView(paramView: View?, paramCustomViewCallback: CustomViewCallback?) {
        if (mCustomView != null) {
            onHideCustomView()
            return
        }
        mCustomView = paramView
        mOriginalSystemUiVisibility =
            (context as MainActivity).window.decorView.systemUiVisibility
        mOriginalOrientation = context.requestedOrientation
        mCustomViewCallback = paramCustomViewCallback
        (context.window.decorView as FrameLayout).addView(
            mCustomView,
            FrameLayout.LayoutParams(-1, -1)
        )
        context.window.decorView.systemUiVisibility = 3846 or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

}