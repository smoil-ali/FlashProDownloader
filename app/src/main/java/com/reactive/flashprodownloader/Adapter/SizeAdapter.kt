package com.reactive.flashprodownloader.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reactive.flashprodownloader.Interfaces.SizeFragmentListener
import com.reactive.flashprodownloader.databinding.ItemSizeFragmentBinding
import com.reactive.flashprodownloader.model.FlashLightDownload


class SizeAdapter(val context:Context,
val list: MutableList<FlashLightDownload>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var listener: SizeFragmentListener

    fun setMyListener(listener: SizeFragmentListener){
        this.listener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemSizeFragmentBinding.inflate(inflater,parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val lightDownload = list[holder.adapterPosition]
        if (holder is MyViewHolder){
            holder.binding.name.text = lightDownload.title
            holder.binding.size.text = lightDownload.size

            holder.binding.container.setOnClickListener {
                listener.onSelect(lightDownload)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(val binding: ItemSizeFragmentBinding):RecyclerView.ViewHolder(binding.root)
}