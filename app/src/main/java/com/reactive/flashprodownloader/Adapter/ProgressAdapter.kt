package com.reactive.flashprodownloader.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.reactive.flashprodownloader.Fragments.ProgressFragment
import com.reactive.flashprodownloader.Helper.ProgressDiffUtil
import com.reactive.flashprodownloader.Interfaces.ProgressListener
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.databinding.ItemProgressDownloadsBinding
import com.reactive.flashprodownloader.model.FlashLightDownload
import kotlin.math.log


class ProgressAdapter(val context: Context,
                      val lifecycle: LifecycleOwner,
val list: MutableList<FlashLightDownload>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = ProgressAdapter::class.simpleName
    private lateinit var listener: ProgressListener

    fun setProgressListener(listener: ProgressListener){
        this.listener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemProgressDownloadsBinding.inflate(inflater,parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val lightDownload:MutableLiveData<FlashLightDownload> = MutableLiveData()
        Log.i(TAG, "onBindViewHolder: ${holder.adapterPosition}")
        lightDownload.value = list[holder.adapterPosition]
        if (holder is MyViewHolder){
            Log.i(TAG, "onBindViewHolder: bhr")
            holder.binding.playContainer.visibility = View.VISIBLE
            holder.binding.pauseContainer.visibility = View.GONE
            holder.binding.circularProgressBar.progress = 0
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

                Log.i(TAG, "onBindViewHolder: under")

                holder.binding.title.text = it.title
                holder.binding.size.text = it.size

                if (holder.binding.playContainer.visibility == View.GONE){
                    listener.onStart(lightDownload,holder)
                }
                holder.binding.playContainer.setOnClickListener {
                    holder.binding.playContainer.visibility = View.GONE
                    holder.binding.pauseContainer.visibility = View.VISIBLE
                    listener.onStart(lightDownload,holder)
                }

                holder.binding.pauseContainer.setOnClickListener {
                    holder.binding.playContainer.visibility = View.VISIBLE
                    holder.binding.pauseContainer.visibility = View.GONE
                    listener.onPause(lightDownload,holder)
                }

                holder.binding.container.setOnClickListener(object : View.OnClickListener{
                    override fun onClick(p0: View?) {
                        if (it.selected){
                            listener.onRemove(lightDownload)
                            it.selected = false
                            lightDownload.value =
                                FlashLightDownload(it.id,it.url,it.title,it.path,it.size,it.bytes,it.selected,it.status)
                        }else if (ProgressFragment.CHECK){
                            it.selected = true
                            lightDownload.value =
                                FlashLightDownload(it.id,it.url,it.title,it.path,it.size,it.bytes,it.selected,it.status)
                            listener.onLong(lightDownload,holder)
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

    class MyViewHolder(val binding: ItemProgressDownloadsBinding):RecyclerView.ViewHolder(binding.root)

    fun setData(newList: List<FlashLightDownload>){
        val diffUtil = ProgressDiffUtil(list,newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        diffResult.dispatchUpdatesTo(this)
        list.clear()
        list.addAll(newList)
        
    }
}