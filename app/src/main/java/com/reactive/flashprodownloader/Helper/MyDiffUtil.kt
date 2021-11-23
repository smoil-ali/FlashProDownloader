package com.reactive.flashprodownloader.Helper

import androidx.recyclerview.widget.DiffUtil
import com.reactive.flashprodownloader.model.FlashWindow


class MyDiffUtil(private val oldList:List<FlashWindow>,
                 private val newList: List<FlashWindow>):DiffUtil.Callback() {
    private  val TAG = MyDiffUtil::class.simpleName
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