package com.reactive.flashprodownloader.Interfaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.reactive.flashprodownloader.model.FlashLightDownload


interface CompleteListener {
    fun onLong(lightDownload: MutableLiveData<FlashLightDownload>)
    fun onRemove(lightDownload: MutableLiveData<FlashLightDownload>)
    fun onSelect(lightDownload: MutableLiveData<FlashLightDownload>)
    fun onShare(lightDownload: MutableLiveData<FlashLightDownload>)
}