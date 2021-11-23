package com.reactive.flashprodownloader.Interfaces

import com.reactive.flashprodownloader.model.FlashWindow



interface TabFragmentListener {
    fun OnSelect(window: FlashWindow, pos: Int)
    fun OnCancel(window: FlashWindow)
    fun OnSelectedCancel(delWin: FlashWindow,newWindow: FlashWindow,pos: Int)
    fun OnFinishList(delWin: FlashWindow,pos: Int)
}