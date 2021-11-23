package com.reactive.flashprodownloader.Adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.reactive.flashprodownloader.Activities.TabActivity
import com.reactive.flashprodownloader.Helper.Constants
import com.reactive.flashprodownloader.Helper.MyDiffUtil
import com.reactive.flashprodownloader.Helper.Utils
import com.reactive.flashprodownloader.Interfaces.TabFragmentListener
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.databinding.NewTabItemBinding
import com.reactive.flashprodownloader.model.FlashWindow
import java.io.File


class TabAdapter(val context: Context,
                 val lifecycle: LifecycleOwner,
                 val list: MutableList<FlashWindow>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = TabAdapter::class.simpleName
    lateinit var listener: TabFragmentListener
    var change: Boolean = false

    fun setTabListener(listener: TabFragmentListener){
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = NewTabItemBinding.inflate(inflater,parent,false)
        return TabViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val win = list.get(holder.adapterPosition)
        Log.i(TAG, "onBindViewHolder: ${holder.adapterPosition}")
        if (holder is TabViewHolder){

            if (TabActivity.selected_tab.value == holder.adapterPosition){
                selected(holder)
            }else{
                normal(holder)
            }


            holder.binding.title.text = win.title

            if (win.path == Constants.BLANK_URL){
                Glide.with(context)
                    .load(File(Constants.getHomePageScreenShot(context,Constants.HOME_PAGE_TITLE)))
                    .placeholder(R.drawable.logo_icon)
                    .into(holder.binding.imageView)
            }else{
                if (Utils.isPathExists(win.path!!,context)){
                    Glide.with(context)
                        .load(Uri.parse(Constants.getHomePageScreenShot(context,win.path)))
                        .placeholder(R.drawable.logo_icon)
                        .into(holder.binding.imageView)
                }else{
                    Glide.with(context)
                        .load(Uri.parse(Constants.getHomePageScreenShot(context,Constants.HOME_PAGE_TITLE)))
                        .placeholder(R.drawable.logo_icon)
                        .into(holder.binding.imageView)
                }

            }

            holder.binding.cancelContainer.setOnClickListener {
                Log.i(TAG, "onBindViewHolder: selected tab ${TabActivity.selected_tab} " +
                        "${holder.adapterPosition}")
                if (TabActivity.selected_tab.value == holder.adapterPosition){
                    change = true
                    if (TabActivity.selected_tab.value == 0){
                        if (list.size > 1){
                            TabActivity.selected_tab.value = TabActivity.selected_tab.value!! + 1
                            listener.OnCancel(win)
                        }
                    }else if (TabActivity.selected_tab.value == list.size - 1){
                        TabActivity.selected_tab.value = TabActivity.selected_tab.value!! - 1
                        listener.OnCancel(win)
                    }else{
                        listener.OnCancel(win)
                    }
                }else{
                    if (TabActivity.selected_tab.value!! > holder.adapterPosition)
                        TabActivity.selected_tab.value = TabActivity.selected_tab.value!! + 1
                    listener.OnCancel(win)
                }

            }

            holder.binding.body.setOnClickListener{
                Log.i(TAG, "onBindViewHolder: body ${holder.adapterPosition}")
                if(holder.adapterPosition != TabActivity.selected_tab.value){
                    notifyItemChanged(TabActivity.selected_tab.value!!)
                    TabActivity.selected_tab.value = holder.adapterPosition
                    notifyItemChanged(TabActivity.selected_tab.value!!)
                }
                listener.OnSelect(win,holder.adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class TabViewHolder(val binding: NewTabItemBinding) : RecyclerView.ViewHolder(binding.root){

    }

    fun selected(holder: TabViewHolder) {
        holder.binding.container.setBackground(
            ContextCompat
                .getDrawable(context, R.drawable.ad_new_tab_selected_bg)
        )

        holder.binding.title.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.white
            )
        )
        holder.binding.cancel.setColorFilter(
            ContextCompat.getColor(
                context,
                R.color.white
            )
        )
    }

    fun normal(holder: TabViewHolder) {
        holder.binding.container.setBackground(
            ContextCompat
                .getDrawable(context, R.drawable.add_new_tab_bg)
        )
        holder.binding.title.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.black
            )
        )
        holder.binding.cancel.setColorFilter(
            ContextCompat.getColor(
                context,
                R.color.black
            )
        )
    }

    fun setData(newlist: List<FlashWindow>){
        val diffUtil = MyDiffUtil(list,newlist)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        diffResult.dispatchUpdatesTo(this)
        list.clear()
        Log.i(TAG, "setData: $${newlist.size}")
        list.addAll(newlist)
        if (change){
            change = false
            notifyItemChanged(TabActivity.selected_tab.value!!)
        }



    }
}