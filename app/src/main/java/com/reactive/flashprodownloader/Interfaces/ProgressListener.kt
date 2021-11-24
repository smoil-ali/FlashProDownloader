package com.reactive.flashprodownloader.Interfaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.reactive.flashprodownloader.Adapter.ProgressAdapter
import com.reactive.flashprodownloader.model.FlashLightDownload


interface ProgressListener {
    fun onStart(lightDownload: MutableLiveData<FlashLightDownload>,holder: ProgressAdapter.MyViewHolder)
    fun onPause(lightDownload: MutableLiveData<FlashLightDownload>,holder: ProgressAdapter.MyViewHolder)
    fun onLong(lightDownload: MutableLiveData<FlashLightDownload>,holder: ProgressAdapter.MyViewHolder)
    fun onRemove(lightDownload: MutableLiveData<FlashLightDownload>)
}