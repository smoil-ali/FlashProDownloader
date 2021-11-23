package com.reactive.flashprodownloader.Helper

import androidx.recyclerview.widget.DiffUtil
import com.reactive.flashprodownloader.model.FlashHistory


class HistoryDiffUtil(private val oldList: List<FlashHistory>,
private val newList: List<FlashHistory>):DiffUtil.Callback() {
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
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}