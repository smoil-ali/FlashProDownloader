package com.reactive.flashprodownloader.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.reactive.flashprodownloader.Adapter.ProgressAdapter
import com.reactive.flashprodownloader.Helper.PR
import com.reactive.flashprodownloader.Helper.Utils
import com.reactive.flashprodownloader.Interfaces.MainActivityListener
import com.reactive.flashprodownloader.Interfaces.OnBackPressedListener
import com.reactive.flashprodownloader.Interfaces.ProgressListener
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.databinding.FragmentProgressBinding
import com.reactive.flashprodownloader.model.FlashLightDownload
import com.reactive.flashprodownloader.model.FlashLightDownloadsIds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProgressFragment : BaseFragment(), MainActivityListener, OnBackPressedListener,
    ProgressListener {
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
        flashDao.getProgressDownloads(false).observe(requireActivity(), Observer {
            Log.i(TAG, "getData: ${it}")
            adapter.setData(it)
            if(it.isNotEmpty()){
                binding.alertTitle.visibility = View.GONE
            }else{
                binding.alertTitle.visibility = View.VISIBLE
            }
        })
    }


    override fun onStart(lightDownload: MutableLiveData<FlashLightDownload>,
                         holder: ProgressAdapter.MyViewHolder) {
        itemLightDownload = lightDownload.value!!
        val exist:FlashLightDownloadsIds? = PR.existId(lightDownload.value!!.id)
        Log.i(TAG, "onStart: $exist")
        if (exist == null){
            ids.value = PRDownloader.download(lightDownload.value!!.url, lightDownload.value!!.path,
                lightDownload.value!!.title)
                .build()
                .setOnStartOrResumeListener {
                    Log.i(TAG, "onStart resume: $ids")
                }
                .setOnPauseListener {
                    Log.i(TAG, "on pause: ")
                }
                .setOnCancelListener {
                    Log.i(TAG, "on cancel: ")
                }
                .setOnProgressListener {
                    val result = (it.currentBytes * 100) / it.totalBytes
                    Log.i(TAG, "onStart: $result")
                    holder.binding.circularProgressBar.progress = result.toInt()
                }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        Log.i(TAG, "onDownloadComplete: ")
                        if (isAdded){
                            PR.deleteId(lightDownload.value!!.id)
                            update(lightDownload)
                        }

                    }
                    override fun onError(error: com.downloader.Error?) {
                        Log.i(TAG, "onError: ${error.toString()}")
                    }
                })
        }else{
            PRDownloader.resume(exist.downloadId)
        }


    }


    override fun onPause(lightDownload: MutableLiveData<FlashLightDownload>,holder: ProgressAdapter.MyViewHolder) {
        coroutineScope.launch(Dispatchers.Main) {
            val downloadIds =
                PR.getDownloadId(lightDownload.value!!.id)
            Log.i(TAG, "onPause: $downloadIds")
            PRDownloader.pause(downloadIds!!.downloadId)
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



    private fun update(lightDownload: LiveData<FlashLightDownload>){
        coroutineScope.launch(Dispatchers.Default) {
            flashDao.updateDownload(true,lightDownload.value!!.id,
                Utils.getDownloadVideoPath(requireContext()))
        }
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