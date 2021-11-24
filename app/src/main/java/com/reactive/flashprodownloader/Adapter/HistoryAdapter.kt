package com.reactive.flashprodownloader.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.reactive.flashprodownloader.Helper.Constants
import com.reactive.flashprodownloader.Helper.HistoryDiffUtil
import com.reactive.flashprodownloader.Interfaces.HistoryListener
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.databinding.HistoryRecyclerViewListItemBinding
import com.reactive.flashprodownloader.databinding.HistoryRecyclerViewListItemDateHeaderBinding
import com.reactive.flashprodownloader.model.FlashHistory


class HistoryAdapter(val context: Context,
val list: MutableList<FlashHistory>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var listener: HistoryListener

    fun setHistoryListener(listener: HistoryListener){
        this.listener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding1 = HistoryRecyclerViewListItemBinding.inflate(inflater,parent,false)
        val binding2 = HistoryRecyclerViewListItemDateHeaderBinding.inflate(inflater,parent,false)
        if (viewType == R.layout.history_recycler_view_list_item_date_header)
            return MyViewHolder2(binding2)
        else
            return MyViewHolder1(binding1)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val history = list[holder.adapterPosition]
        if (holder is MyViewHolder1){
            holder.binding1.tvHistoryTitle.text = history.title
            holder.binding1.tvHistoryUrl.text = history.url
            holder.binding1.llHistoryListItemContainer.setOnClickListener {
                listener.onSelect(history)
            }

            holder.binding1.delete.setOnClickListener {
                AlertDialog.Builder(context).apply {
                    setTitle("Delete Bookmark")
                    setMessage("You want to remove bookmark?")
                    setPositiveButton("Yes") { _, _ ->
                        listener.onDelete(history)
                    }
                    setNegativeButton("No") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    show()
                }
            }
        }
        if (holder is MyViewHolder2){
            holder.binding2.tvDateHeader.text = history.time
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].title == Constants.DATE){
            R.layout.history_recycler_view_list_item_date_header
        }else
            R.layout.history_recycler_view_list_item

    }

    class MyViewHolder1(val binding1:HistoryRecyclerViewListItemBinding):
        RecyclerView.ViewHolder(binding1.root){}

    class MyViewHolder2(val binding2:HistoryRecyclerViewListItemDateHeaderBinding):
            RecyclerView.ViewHolder(binding2.root){}

    fun setData(newList: List<FlashHistory>){
        val diffUtil = HistoryDiffUtil(list,newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        diffResult.dispatchUpdatesTo(this)
        list.clear()
        list.addAll(newList)
    }
}