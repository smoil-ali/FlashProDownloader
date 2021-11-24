package com.reactive.flashprodownloader.Adapter

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.reactive.flashprodownloader.Helper.BookmarkDiffUtil
import com.reactive.flashprodownloader.Interfaces.BookmarkListener
import com.reactive.flashprodownloader.databinding.HistoryRecyclerViewListItemBinding
import com.reactive.flashprodownloader.model.FlashBookmark


class BookmarkAdapter(val context: Context,
                      val list: MutableList<FlashBookmark>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var listener: BookmarkListener

    fun setBookmarkListener(listener: BookmarkListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = HistoryRecyclerViewListItemBinding.inflate(inflater,parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val bookmark = list[holder.adapterPosition]
        if (holder is MyViewHolder){
            holder.binding.tvHistoryTitle.text = bookmark.title
            holder.binding.tvHistoryUrl.text = bookmark.url

            holder.binding.delete.setOnClickListener {
                AlertDialog.Builder(context).apply {
                    setTitle("Delete Bookmark")
                    setMessage("You want to remove bookmark?")
                    setPositiveButton("Yes") { _, _ ->
                        listener.onDelete(bookmark)
                    }
                    setNegativeButton("No") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    show()
                }
            }

            holder.binding.llHistoryListItemContainer.setOnClickListener {
                listener.onSelect(bookmark)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(val binding: HistoryRecyclerViewListItemBinding): RecyclerView.ViewHolder(binding.root)

    fun setData(newList: List<FlashBookmark>){
        val diffUtil = BookmarkDiffUtil(list,newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        diffResult.dispatchUpdatesTo(this)
        list.clear()
        list.addAll(newList)
    }
}