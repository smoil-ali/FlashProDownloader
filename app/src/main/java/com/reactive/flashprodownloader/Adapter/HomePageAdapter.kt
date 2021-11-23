package com.reactive.flashprodownloader.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.reactive.flashprodownloader.databinding.ItemHomePageIconBinding
import com.reactive.flashprodownloader.model.FlashHome
import com.reactive.flashprodownloader.Interfaces.HomePageAdapterCallbacks

class HomePageAdapter(
    private val context: Context,
    private val list: List<FlashHome>,
    private val listener: HomePageAdapterCallbacks
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = HomePageAdapter::class.simpleName



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.i(TAG, "onCreateViewHolder: ")
        val inflater:LayoutInflater = LayoutInflater.from(context)
        val binding: ItemHomePageIconBinding = ItemHomePageIconBinding.inflate(inflater,
            parent,
            false)
        return MyHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val home:FlashHome = list.get(position)
        if (holder is MyHolder){
            Log.i(TAG, "onBindViewHolder: $home")
            holder.binding.icon.setImageDrawable(ContextCompat.getDrawable(context,
            home.icon))

            holder.binding.container.setOnClickListener {
                listener.OnSelect(home)
            }
        }else{
            Log.i(TAG, "onBindViewHolder: else $home")
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyHolder(public val binding: ItemHomePageIconBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}