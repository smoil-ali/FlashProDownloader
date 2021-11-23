package com.reactive.flashprodownloader.Fragments

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.reactive.flashprodownloader.Activities.MainActivity
import com.reactive.flashprodownloader.Interfaces.*
import com.reactive.flashprodownloader.databinding.ActivityMainBinding
import com.reactive.flashprodownloader.model.FlashHome

import com.reactive.flashprodownloader.Room.FlashDao



open class BaseFragment:Fragment() {
    private val TAG = BaseFragment::class.simpleName
    private lateinit var mainActivity: MainActivity
    lateinit var flashDao: FlashDao
    lateinit var listBrowsers:List<FlashHome>
    lateinit var mainBinding: ActivityMainBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated: ")
        mainActivity = requireActivity() as MainActivity
        flashDao = mainActivity.flashDao
        listBrowsers = mainActivity.listBrowsers
        mainBinding = mainActivity.binding
    }

    fun setOnBackPressedListener(onBackPressedListener: OnBackPressedListener?){
        mainActivity.setOnBackPressedListener(onBackPressedListener)
    }

    fun setMainActivityListener(listener: MainActivityListener){
        mainActivity.setMainActivityListener(listener)
    }

    fun setBookmarkMain(listener: MainActivityBookmarkListener){
        mainActivity.setMainActivityBookmarkListener(listener)
    }

    fun setHistoryMain(listener: MainActivityHistoryListener){
        mainActivity.setMainActivityHistoryListener(listener)
    }

    fun showHomeScreen(){
        mainActivity.showHomeScreen()
    }

    fun  setTabListener(listener: MainActivityTabListener){
        mainActivity.setMainTabListener(listener)
    }

    fun registerReceiver(broadcastReceiver: BroadcastReceiver){
        mainActivity.registerReceiver(broadcastReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }
}