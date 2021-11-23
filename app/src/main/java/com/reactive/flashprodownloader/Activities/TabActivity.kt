package com.reactive.flashprodownloader.Activities

import android.icu.util.UniversalTimeScale
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.reactive.flashprodownloader.Adapter.TabAdapter
import com.reactive.flashprodownloader.Helper.Constants
import com.reactive.flashprodownloader.Helper.Utils
import com.reactive.flashprodownloader.Interfaces.TabFragmentListener
import com.reactive.flashprodownloader.databinding.ActivityTabBinding
import com.reactive.flashprodownloader.model.FlashWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

class TabActivity : BaseActivity(), TabFragmentListener {
    private val TAG = TabActivity::class.simpleName
    private lateinit var binding: ActivityTabBinding
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    lateinit var adapter: TabAdapter
    var list: MutableList<FlashWindow> = mutableListOf()
    var scroll:Boolean = true
    companion object{
        var selected_tab: MutableLiveData<Int> = MutableLiveData(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTabBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addNewTabContainer.setOnClickListener {
            createNewTab()
        }

        selected_tab.value = Utils.getSelectedTab(this)

        selected_tab.observe(this, Observer {
            Log.i(TAG, "onCreate: $it")
            Utils.saveSelectedTab(this,it)
        })

        adapter = TabAdapter(this,this,list)
        adapter.setTabListener(this)
        binding.recycler.layoutManager = GridLayoutManager(this,2)
        binding.recycler.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: ${selected_tab}")
        getAllWindows()
    }

    private fun createNewTab(){
        scroll = false
        coroutineScope.launch(Dispatchers.Default) {
            flashDao.createNewFlashWindow(FlashWindow(0, Constants.BLANK_URL,
                Constants.BLANK_URL, Constants.BLANK_URL,false))
        }
    }


    private fun getAllWindows(){
        flashDao.getAllFlashWindows().observe(this,
            Observer {
                Log.i(TAG, "getAllWindows: ${it}")
                adapter.setData(it)
                if (scroll){
                    binding.recycler.smoothScrollToPosition(selected_tab.value!!)
                }else{
                    scroll = true
                }
                if (it.isEmpty()){
                    finish()
                }
            })
    }

    override fun OnSelect(window: FlashWindow,pos: Int) {
        Utils.saveSelectedId(this, window.id)
        Utils.saveSelectedTab(this,pos)
        finish()
    }

    override fun OnCancel(window: FlashWindow) {
        scroll = false
        coroutineScope.launch(Dispatchers.Default) {
            flashDao.deleteFlashWindow(window)
        }
    }

    override fun OnSelectedCancel(delWin: FlashWindow, newWindow: FlashWindow, pos: Int) {
        Utils.saveSelectedTab(this,pos)
        Utils.saveSelectedId(this, newWindow.id)
        coroutineScope.launch(Dispatchers.Default) {
            flashDao.deleteFlashWindow(delWin)
        }
    }

    override fun OnFinishList(delWin: FlashWindow, pos: Int) {
        Utils.saveSelectedTab(this,pos)
        coroutineScope.launch(Dispatchers.Default) {
            flashDao.deleteFlashWindow(delWin)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.coroutineContext.cancelChildren()
    }
}