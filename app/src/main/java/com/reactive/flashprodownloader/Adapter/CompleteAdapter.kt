package com.reactive.flashprodownloader.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.reactive.flashprodownloader.Fragments.CompleteFragment
import com.reactive.flashprodownloader.Helper.CompleteDiffUtil
import com.reactive.flashprodownloader.Helper.Constants
import com.reactive.flashprodownloader.Interfaces.CompleteListener
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.databinding.ItemCompleteDownloadsBinding
import com.reactive.flashprodownloader.model.FlashLightDownload
import java.io.File


class CompleteAdapter(val context: Context,
                      val lifecycle: LifecycleOwner,
                      val list: MutableList<FlashLightDownload>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = CompleteAdapter::class.simpleName
    private lateinit var listener: CompleteListener

    fun setCompleteListener(listener: CompleteListener){
        this.listener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemCompleteDownloadsBinding.inflate(inflater,parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val lightDownload:MutableLiveData<FlashLightDownload> = MutableLiveData()
        Log.i(TAG, "onBindViewHolder: data load")
        lightDownload.value = list[holder.adapterPosition]
        if (holder is MyViewHolder){
            holder.binding.container.background  =
                ContextCompat.getDrawable(context,R.drawable.shadow_background)
            lightDownload.observe(lifecycle, {
                if (!it.selected){
                    holder.binding.container.background  =
                        ContextCompat.getDrawable(context,R.drawable.shadow_background)
                }else{
                    holder.binding.container.setBackgroundColor(ContextCompat.
                    getColor(context,R.color.selected_color_progress))
                }

                holder.binding.title.text = it.title
                holder.binding.size.text = it.size
                val path = it.path+"/"+it.title
                val uriForFile = FileProvider.getUriForFile(
                    context,
                    Constants.AUTHORITY,
                    File(path)
                )
                Glide.with(context)
                    .load(uriForFile)
                    .placeholder(R.drawable.logo_icon)
                    .into(holder.binding.logo)




                holder.binding.container.setOnClickListener(object : View.OnClickListener{
                    override fun onClick(p0: View?) {
                        if (it.selected){
                            listener.onRemove(lightDownload)
                            it.selected = false
                            lightDownload.value =
                                FlashLightDownload(it.id,it.url,it.title,it.path,it.size,it.bytes,it.selected,it.status)
                        }else if (CompleteFragment.CHECK){
                            it.selected = true
                            lightDownload.value =
                                FlashLightDownload(it.id,it.url,it.title,it.path,it.size,it.bytes,it.selected,it.status)
                            listener.onLong(lightDownload,holder)
                        }else{
                            listener.onSelect(lightDownload)
                        }
                    }
                })

                holder.binding.container.setOnLongClickListener(object : View.OnLongClickListener{
                    override fun onLongClick(p0: View?): Boolean {
                        if (!it.selected){
                            it.selected = true
                            lightDownload.value =
                                FlashLightDownload(it.id,it.url,it.title,it.path,it.size,it.bytes,it.selected,it.status)
                            listener.onLong(lightDownload,holder)
                        }

                        return true
                    }
                })

            })


        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(val binding: ItemCompleteDownloadsBinding):RecyclerView.ViewHolder(binding.root)

    fun setData(newList: List<FlashLightDownload>){
        val diffUtil = CompleteDiffUtil(list,newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        diffResult.dispatchUpdatesTo(this)
        list.clear()
        list.addAll(newList)
        
    }
}