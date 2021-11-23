package com.reactive.flashprodownloader.Interfaces

import com.reactive.flashprodownloader.model.FlashHistory


interface MainActivityHistoryListener {
    fun onHistory(history: FlashHistory)
}