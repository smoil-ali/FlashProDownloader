package com.reactive.flashprodownloader.Helper

import androidx.recyclerview.widget.DiffUtil
import com.reactive.flashprodownloader.model.FlashLightDownload


class SizeDiffUtil(val oldList: List<FlashLightDownload>,
                   val newList: List<FlashLightDownload>):DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        TODO("Not yet implemented")
    }
}