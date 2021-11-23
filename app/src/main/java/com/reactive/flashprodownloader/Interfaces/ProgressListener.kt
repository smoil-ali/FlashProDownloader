package com.reactive.flashprodownloader.Interfaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.reactive.flashprodownloader.model.FlashLightDownload


interface ProgressListener {
    fun onStart(lightDownload: MutableLiveData<FlashLightDownload>)
    fun onPause(lightDownload: MutableLiveData<FlashLightDownload>)
    fun onLong(lightDownload: MutableLiveData<FlashLightDownload>)
    fun onRemove(lightDownload: MutableLiveData<FlashLightDownload>)
}