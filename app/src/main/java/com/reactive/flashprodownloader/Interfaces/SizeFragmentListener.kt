package com.reactive.flashprodownloader.Interfaces

import com.reactive.flashprodownloader.model.FlashLightDownload


interface SizeFragmentListener {
    fun onSelect(lightDownload: FlashLightDownload)
}