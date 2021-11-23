package com.reactive.flashprodownloader.Interfaces

import com.reactive.flashprodownloader.model.FlashBookmark


interface BookmarkListener {

    fun onSelect(bookmark:FlashBookmark)
    fun onDelete(bookmark: FlashBookmark)
}