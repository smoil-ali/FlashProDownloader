package com.reactive.flashprodownloader.Interfaces

import com.reactive.flashprodownloader.model.FlashHistory

interface HistoryListener {

    fun onSelect(history: FlashHistory)
    fun onDelete(history: FlashHistory)
}