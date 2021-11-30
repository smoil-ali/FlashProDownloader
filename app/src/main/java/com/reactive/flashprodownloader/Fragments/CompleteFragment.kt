package com.reactive.flashprodownloader.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.reactive.flashprodownloader.Adapter.CompleteAdapter
import com.reactive.flashprodownloader.Helper.Constants
import com.reactive.flashprodownloader.Interfaces.CompleteListener
import com.reactive.flashprodownloader.Interfaces.MainActivityListener
import com.reactive.flashprodownloader.Interfaces.OnBackPressedListener
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.databinding.FragmentCompleteBinding
import com.reactive.flashprodownloader.model.FlashLightDownload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class CompleteFragment : BaseFragment(),MainActivityListener,OnBackPressedListener,
    CompleteListener {
    companion object{
        var CHECK = false
    }

    private val TAG = CompleteFragment::class.simpleName
    private lateinit var binding: FragmentCompleteBinding
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val list:MutableList<FlashLightDownload> = mutableListOf()
    private lateinit var adapter: CompleteAdapter
    private lateinit var itemLightDownload: FlashLightDownload
    private var ids: MutableLiveData<Int> = MutableLiveData()
    private val idsList:MutableList<MutableLiveData<FlashLightDownload>> = mutableListOf()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompleteBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CompleteAdapter(requireContext(),requireActivity(),list)
        adapter.setCompleteListener(this)
        val layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
        layoutManager.stackFromEnd = false
        binding.recycler.layoutManager = layoutManager
        binding.recycler.adapter = adapter


    }


    override fun onHomeClick() {
        showHomeScreen()
    }

    override fun onBackPressed() {

    }


    override fun onResume() {
        super.onResume()
        CompleteSelection()
        getData()

        setMainActivityListener(this)
        setOnBackPressedListener(null)

        binding.deleteContainer.setOnClickListener {
            CHECK = false
            showNormal()
            cancelDownloading()
        }

        binding.shareContainer.setOnClickListener{
            showNormal()
            shareVideos()
        }

        binding.backContainer.setOnClickListener {
            removeView()
        }
    }

    override fun onPause() {
        super.onPause()
        removeView()
    }

    private fun getData(){
        flashDao.getCompleteDownloads(Constants.COMPLETE).observe(requireActivity(), Observer {
            Log.i(TAG, "getData: ${it}")
            adapter.setData(it.reversed())
            if(it.isNotEmpty()){
                binding.alertTitle.visibility = View.GONE
            }else{
                binding.alertTitle.visibility = View.VISIBLE
            }
        })
    }

    private fun cancelDownloading(){

        coroutineScope.launch {
            val it = idsList.iterator()
            while (it.hasNext()){
                Log.i(TAG, "cancelDownloading: start loop")
                val model = it.next()
                val path = model.value!!.path +"/"+model.value!!.title
                val file = File(path)
                Log.i(TAG, "cancelDownloading: $path")
                if (file.exists()){
                    if (file.delete()){
                        Log.i(TAG, "cancelDownloading: deleted successfully")
                        val msg = withContext(Dispatchers.Default){
                            flashDao.deleteLightDownload(model.value!!.id)
                            Log.i(TAG, "cancelDownloading: delete query")
                            "Deleted"
                        }
                        it.remove()
                    }
                    else{
                        Log.i(TAG, "cancelDownloading: file not deleted")
                    }
                }else{
                    Log.i(TAG, "cancelDownloading: file dont exist")
                    val msg = withContext(Dispatchers.Default){
                        flashDao.deleteLightDownload(model.value!!.id)
                        Log.i(TAG, "cancelDownloading: delete query")
                        "Deleted"
                    }
                    it.remove()
                }
                
            }
            Log.i(TAG, "cancelDownloading: clear")
        }
    }


    override fun onLong(lightDownload: MutableLiveData<FlashLightDownload>,
                        holder: CompleteAdapter.MyViewHolder) {
        showSelectedOptions()
        CHECK = true
        idsList.add(lightDownload)
        Log.i(TAG, "onLong: ${idsList.size}")
    }

    override fun onSelect(lightDownload: MutableLiveData<FlashLightDownload>) {

        val path = lightDownload.value!!.path +"/"+lightDownload.value!!.title

        val videoFile = File(path)
        val fileUri =
            FileProvider.getUriForFile(requireContext(),
                Constants.AUTHORITY, videoFile)
        try{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(fileUri, "video/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
            Log.i(TAG, "onSelect: $path")
        }catch (e:Exception){
            Log.i(TAG, "onSelect: ${e.message}")
        }

    }

    override fun onRemove(lightDownload: MutableLiveData<FlashLightDownload>) {
        if(idsList.remove(lightDownload))
            if (idsList.isEmpty()){
                showNormal()
                CHECK = false
            }
        Log.i(TAG, "onRemove: true ${idsList.size}")
    }

    override fun onShare(lightDownload: MutableLiveData<FlashLightDownload>) {
        val path = lightDownload.value!!.path+"/"+lightDownload.value!!.title
        val uriForFile = FileProvider.getUriForFile(
            requireContext(),
            Constants.AUTHORITY,
            File(path)
        )
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "video/*"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,lightDownload.value!!.title)
        shareIntent.putExtra(Intent.EXTRA_STREAM,   uriForFile)
        shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(Intent.createChooser(shareIntent,"LightDownloader"))
    }

    private fun removeView(){
        showNormal()
        CHECK = false

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
    }

    private fun shareVideos(){
        val listUri:ArrayList<Uri> = ArrayList()
        idsList.forEach {
            val path = it.value!!.path+"/"+it.value!!.title
            val uriForFile = FileProvider.getUriForFile(
                requireContext(),
                Constants.AUTHORITY,
                File(path)
            )
            listUri.add(uriForFile)
        }
        Log.i(TAG, "shareVideos: ${listUri.size}")
        if (listUri.isNotEmpty()){
            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
            shareIntent.type = "video/*"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Videos")
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,   listUri)
            shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivity(Intent.createChooser(shareIntent, getString(R.string.app_name)))
        }

    }

    private fun showSelectedOptions(){
        binding.deleteContainer.visibility = View.VISIBLE
        binding.shareContainer.visibility = View.VISIBLE
        binding.backContainer.visibility = View.VISIBLE
        binding.title.visibility = View.GONE
    }

    private fun showNormal(){
        binding.deleteContainer.visibility = View.GONE
        binding.shareContainer.visibility = View.GONE
        binding.backContainer.visibility = View.GONE
        binding.title.visibility = View.VISIBLE
    }
}