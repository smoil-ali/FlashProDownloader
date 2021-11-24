package com.reactive.flashprodownloader.Activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.reactive.flashprodownloader.Adapter.HistoryAdapter
import com.reactive.flashprodownloader.Helper.Constants
import com.reactive.flashprodownloader.Interfaces.HistoryListener
import com.reactive.flashprodownloader.databinding.ActivityHistoryBinding
import com.reactive.flashprodownloader.model.FlashHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch
import java.io.Serializable

class HistoryActivity : BaseActivity(), HistoryListener {
    private val TAG = HistoryActivity::class.simpleName
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    var tempDate:String? = null
    lateinit var binding: ActivityHistoryBinding
    lateinit var adater: HistoryAdapter
    val list:MutableList<FlashHistory> = mutableListOf()
    val tempList:MutableList<FlashHistory> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adater = HistoryAdapter(this,list)
        adater.setHistoryListener(this)
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adater

        getData()
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: ")

    }


    private fun getData(){
        flashDao.getHistory().observe(this, Observer {
            tempList.clear()
            if (it.isNotEmpty()){
                Log.i(TAG, "getData: called")
                binding.alertTitle.visibility = View.GONE
                it.forEach { history ->
                    if (history.time != tempDate){
                        val history1 = FlashHistory(0,
                        Constants.DATE, Constants.DATE,history.time)
                        tempDate = history.time
                        tempList.add(history1)
                    }
                    tempList.add(history)
                }
                Log.i(TAG, "getData: $tempList")
                adater.setData(tempList)
            }else{
                adater.setData(it)
                binding.alertTitle.visibility = View.VISIBLE
            }
        })

    }

    override fun onSelect(history: FlashHistory) {
        val intent = Intent()
        intent.putExtra(Constants.PARAMS,history as Serializable)
        setResult(Constants.HISTORY,intent)
        finish()
    }

    override fun onDelete(history: FlashHistory) {
        coroutineScope.launch(Dispatchers.Default) {
            flashDao.deleteHistory(history)
        }
    }
}