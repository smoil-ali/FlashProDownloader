package com.reactive.flashprodownloader.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.reactive.flashprodownloader.Activities.MyService
import com.reactive.flashprodownloader.Activities.MyService2
import com.reactive.flashprodownloader.Adapter.ProgressAdapter
import com.reactive.flashprodownloader.Helper.Constants
import com.reactive.flashprodownloader.Helper.PR
import com.reactive.flashprodownloader.Helper.Utils
import com.reactive.flashprodownloader.Interfaces.MainActivityListener
import com.reactive.flashprodownloader.Interfaces.OnBackPressedListener
import com.reactive.flashprodownloader.Interfaces.ProgressListener
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.databinding.FragmentProgressBinding
import com.reactive.flashprodownloader.model.FlashLightDownload
import com.reactive.flashprodownloader.model.FlashLightDownloadsIds
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.Func
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.tonyodev.fetch2.Download

import org.jetbrains.annotations.NotNull

import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock


class ProgressFragment : BaseFragment(), MainActivityListener, OnBackPressedListener,
    ProgressListener{
    companion object{
        var CHECK = false
    }

    private val TAG = ProgressFragment::class.simpleName
    private lateinit var binding:FragmentProgressBinding
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val list:MutableList<FlashLightDownload> = mutableListOf()
    private lateinit var adapter: ProgressAdapter
    private lateinit var itemLightDownload: FlashLightDownload
    private var ids: MutableLiveData<Int> = MutableLiveData()
    private val idsList:MutableList<MutableLiveData<FlashLightDownload>> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProgressBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ProgressAdapter(requireContext(),requireActivity(),list)
        adapter.setProgressListener(this)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter

        val config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .build()
        PRDownloader.initialize(requireContext(), config)





        ids.observe(requireActivity(), Observer {
            Log.i(TAG, "onViewCreated: $ids")
            if(it != 0){
                Log.i(TAG, "onStart: $ids")
                PR.addDownloadId(FlashLightDownloadsIds(0,it,itemLightDownload.id))
            }
        })


    }

    override fun onHomeClick() {
        showHomeScreen()
    }

    override fun onBackPressed() {

    }

    override fun onResume() {
        super.onResume()
        DownloadSelection()
        getData()

        setMainActivityListener(this)
        setOnBackPressedListener(null)
        binding.deleteContainer.setOnClickListener {
            CHECK = false
            binding.deleteContainer.visibility = View.GONE
            binding.backContainer.visibility = View.GONE
            binding.title.visibility = View.VISIBLE
            cancelDownloading()
        }

        binding.backContainer.setOnClickListener {
            binding.deleteContainer.visibility = View.GONE
            binding.backContainer.visibility = View.GONE
            binding.title.visibility = View.VISIBLE

            val model = idsList.iterator()
            while (model.hasNext()){
                val its = model.next()
                its.value!!.selected = false
                its.value =
                    FlashLightDownload(its.value!!.id,its.value!!.url,its.value!!.title,
                        its.value!!.path,its.value!!.size,its.value!!.bytes,its.value!!.selected,
                        its.value!!.status)
                model.remove()

            }
            CHECK = false
        }
    }


    private fun getData(){
        flashDao.getProgressDownloads(Constants.PROGRESS).observe(requireActivity(), Observer {
            Log.i(TAG, "getData: ${it}")
            adapter.setData(it)
            if(it.isNotEmpty() ){
                binding.alertTitle.visibility = View.GONE
            }else{
                binding.alertTitle.visibility = View.VISIBLE
            }
        })
    }


    override fun onStart(lightDownload: MutableLiveData<FlashLightDownload>,
                         holder: ProgressAdapter.MyViewHolder) {
        itemLightDownload = lightDownload.value!!
        val serviceIntent = Intent(requireContext(), MyService2::class.java)
        serviceIntent.putExtra(Constants.PARAMS, lightDownload.value)
        startForegroundService(requireContext(), serviceIntent)
        Log.i(TAG, "onStart: ")

        flashDao.getProgressById(itemLightDownload.id).observe(requireActivity(),
            Observer {
                    if (it != null){
                        Log.i(TAG, "onStart: $it")
                        holder.binding.circularProgressBar.progress = it.progress
                    }
            })

    }


    override fun onPause(lightDownload: MutableLiveData<FlashLightDownload>,holder: ProgressAdapter.MyViewHolder) {
        coroutineScope.launch(Dispatchers.Main) {
            val downloadIds =
                PR.getDownloadId(lightDownload.value!!.id)
            Log.i(TAG, "onPause: $downloadIds")
            try {
                PRDownloader.pause(downloadIds!!.downloadId)
            }catch (exception:NullPointerException){
                Log.i(TAG, "onPause: null h re")
            }

        }
    }

    override fun onLong(lightDownload: MutableLiveData<FlashLightDownload>, holder: ProgressAdapter.MyViewHolder) {
        binding.deleteContainer.visibility = View.VISIBLE
        binding.backContainer.visibility = View.VISIBLE
        binding.title.visibility = View.GONE
        idsList.add(lightDownload)
        CHECK = true
        Log.i(TAG, "onLong: ${idsList.size}")
    }

    override fun onRemove(lightDownload: MutableLiveData<FlashLightDownload>) {
        if(idsList.remove(lightDownload))
            if (idsList.isEmpty()){
                binding.deleteContainer.visibility = View.GONE
                binding.backContainer.visibility = View.GONE
                binding.title.visibility = View.VISIBLE
                CHECK = false
            }
        Log.i(TAG, "onRemove: true ${idsList.size}")

    }


    private fun cancelDownloading(){
        coroutineScope.launch {
            val its = idsList.iterator()
            Log.i(TAG, "cancelDownloading: ${its.hasNext()}")
            while (its.hasNext()){
                val model = its.next()
                val exist = PR.existId(model.value!!.id)
                if (exist != null){
                    PRDownloader.cancel(exist.downloadId)
                }
                Log.i(TAG, "cancelDownloading: $model")
                val msg = withContext(Dispatchers.Default){
                    flashDao.deleteLightDownload(model.value!!.id)
                    "Deleted"
                }
                its.remove()
            }
        }

    }


    override fun onPause() {
        super.onPause()
        idsList.forEach {
            it.value!!.selected = false
            it.value =
                FlashLightDownload(it.value!!.id,it.value!!.url,it.value!!.title,
                    it.value!!.path,it.value!!.size,it.value!!.bytes,it.value!!.selected,
                    it.value!!.status)
        }
        binding.deleteContainer.visibility = View.GONE
        binding.backContainer.visibility = View.GONE
        binding.title.visibility = View.VISIBLE
        idsList.clear()
        CHECK = false
    }
}