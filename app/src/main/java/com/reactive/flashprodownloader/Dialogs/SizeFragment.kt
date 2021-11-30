package com.reactive.flashprodownloader.Dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.reactive.flashprodownloader.Adapter.SizeAdapter
import com.reactive.flashprodownloader.Helper.Constants
import com.reactive.flashprodownloader.Helper.Utils
import com.reactive.flashprodownloader.Interfaces.SizeFragmentListener
import com.reactive.flashprodownloader.databinding.FragmentSizeBinding
import com.reactive.flashprodownloader.model.FlashDailymotionResponse
import com.reactive.flashprodownloader.model.FlashLightDownload
import com.reactive.flashprodownloader.model.FlashLightDownloadPro
import com.reactive.lightvideodownloader.Retrofit.MyRetrofit

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class SizeFragment(private val videoList: List<FlashLightDownloadPro>?) : BaseFragment(),
    SizeFragmentListener {
    private val TAG = SizeFragment::class.simpleName
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var binding: FragmentSizeBinding
    private lateinit var url: String
    private val mHEIGHT = 700
    private lateinit var sdf: SimpleDateFormat
    private lateinit var dailymotionResponse: FlashDailymotionResponse
    private var fileLength: Int = 0
    val list:MutableList<FlashLightDownload> = mutableListOf()
    lateinit var adapter: SizeAdapter

    companion object{
        var size = false
    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSizeBinding.inflate(inflater,container,false)



//        dialog?.setOnShowListener(DialogInterface.OnShowListener {
//            val d = it as BottomSheetDialog
//            val bottomSheet = d.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
//            val coordinatorLayout = bottomSheet!!.parent as CoordinatorLayout
//            val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
//            bottomSheetBehavior.peekHeight = mHEIGHT
//            coordinatorLayout.parent.requestLayout()
//        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        adapter = SizeAdapter(requireContext(),list)
        adapter.setMyListener(this)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter

        sdf = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.ENGLISH)

        Log.i(TAG, "onViewCreated: ${videoList?.size}")

        videoList?.forEach {
            if (it.link!!.contains("dailymotion")){
                getRemoteContent(it)
            }else if (it.link!!.contains("vimeo")){
                getVimeoContent(it)
            }else if (it.website == "facebook.com"){
                fileLength = it.size!!.toInt()
                val title = it.name + sdf.format(Date())+".mp4"
                val path = Utils.getDownloadVideoPath(requireContext())
                list.add(FlashLightDownload(0, it.link!!,
                    title,path,Utils.getStringSizeLengthFile(fileLength)
                    ,fileLength,false,
                    Constants.PROGRESS))
                binding.loading.visibility = View.GONE
                adapter.notifyDataSetChanged()
            }else if (it.website == "like.com"){
                fileLength = it.size!!.toInt()
                val title = it.name + sdf.format(Date())+".mp4"
                val path = Utils.getDownloadVideoPath(requireContext())
                list.add(FlashLightDownload(0, it.link!!,
                    title,path,Utils.getStringSizeLengthFile(fileLength)
                    ,fileLength,false,
                    Constants.PROGRESS))
                binding.loading.visibility = View.GONE
                adapter.notifyDataSetChanged()
            }else if (it.website == "imdb.com"){
                fileLength = it.size!!.toInt()
                val title = it.name + sdf.format(Date())+".mp4"
                val path = Utils.getDownloadVideoPath(requireContext())
                list.add(FlashLightDownload(0, it.link!!,
                    title,path,Utils.getStringSizeLengthFile(fileLength)
                    ,fileLength,false,
                    Constants.PROGRESS))
                binding.loading.visibility = View.GONE
                adapter.notifyDataSetChanged()
            }
        }



    }

    private fun getVimeoContent(str: FlashLightDownloadPro){
        val call = MyRetrofit.getClient().getVimeoResponse(str.link!!)
        Log.i(TAG, "getVimeoContent: ${call.request().url}")
        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful){
                    if (response.body() != null){
                        if(!isAdded)
                            return
                        val path = Utils.getDownloadVideoPath(requireContext())
                        val jsonObject = JSONObject(response.body()!!)
                        var title = jsonObject.
                        getString("title").replace("/", "-")
                        title += ".mp4"
                        val formatList = jsonObject.getJSONArray("formats")
                        for (i in 0 until formatList.length()) {
                            val obj = formatList.getJSONObject(i)
                            coroutineScope.launch {
                                if(!obj.getString("url").contains("m3u8") &&
                                        obj.getString("url").contains("mp4")){
                                    fileLength =   withContext(Dispatchers.Default){
                                        obj.getString("url").let {
                                            return@withContext URL(it).openConnection().contentLength
                                        }
                                    }
                                    if (isAdded){
                                        if (fileLength != 0){
                                            str.size = Utils.getStringSizeLengthFile(fileLength)
                                            binding.loading.visibility = View.GONE
                                        }else{
                                            str.size = "0"
                                            binding.loading.visibility = View.GONE
                                            Utils.showToast(requireContext(),"invalid url,restart app")
                                        }
                                        list.add(FlashLightDownload(0,obj.getString("url"),title,path
                                            ,Utils.getStringSizeLengthFile(fileLength),fileLength,
                                            false,Constants.PROGRESS))
                                        adapter.notifyDataSetChanged()
                                        binding.loading.visibility = View.GONE
                                    }
                                }

                            }

                        }
                    }
                }else{
                    if (isAdded){
                        binding.loading.visibility = View.GONE
                        Utils.showToast(requireContext(),"something went wrong")
                        Log.i(TAG, "onResponse: ${response.message()}")
                        dismiss()
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                if (isAdded){
                    Log.i(TAG, "onFailure: ${t.message}")
                    binding.loading.visibility = View.GONE
                    Utils.showToast(requireContext(),"check internet connection")
                    dismiss()
                }
            }
        })
    }


    private fun getRemoteContent(str: FlashLightDownloadPro) {
        val call = MyRetrofit.getClient().getResponse(str.link!!)
        Log.i(TAG, "getRemoteContent: ${call.request().url}")
        call.enqueue(object : Callback<FlashDailymotionResponse> {
            override fun onResponse(
                call: Call<FlashDailymotionResponse>,
                response: Response<FlashDailymotionResponse>
            ) {
                if(response.isSuccessful){
                    if (!isAdded)
                        return
                    dailymotionResponse = response.body()!!
                    Log.i(TAG, "onResponse: $dailymotionResponse")
                    coroutineScope.launch {
                        fileLength =   withContext(Dispatchers.Default){
                            dailymotionResponse.stream_h264_sd_url.let {
                                if (it != null){
                                    val url = URL(it)
                                    return@withContext url.openConnection().contentLength
                                }
                                0
                            }
                        }
                        if (isAdded){
                            if (fileLength != 0){
                                str.size = Utils.getStringSizeLengthFile(fileLength)
                                binding.loading.visibility = View.GONE
                                updateData(str)
                            }else{
                                str.size = "0"
                                updateData(str)
                                binding.loading.visibility = View.GONE
                                Utils.showToast(requireContext(),"invalid url,restart app")
                            }
                        }

                    }
                }else{
                    if (isAdded){
                        binding.loading.visibility = View.GONE
                        Utils.showToast(requireContext(),"something went wrong")
                        Log.i(TAG, "onResponse: ${response.message()}")
                        dismiss()
                    }
                }
            }

            override fun onFailure(call: Call<FlashDailymotionResponse>, t: Throwable) {
                if (isAdded){
                    Log.i(TAG, "onFailure: ${t.message}")
                    binding.loading.visibility = View.GONE
                    dismiss()
                    Utils.showToast(requireContext(),"check internet connection")
                }
            }
        })
    }

    fun updateData(str: FlashLightDownloadPro){
        val title = str.name + sdf.format(Date())+".mp4"
        val path = Utils.getDownloadVideoPath(requireContext())
        list.add(FlashLightDownload(0,dailymotionResponse.stream_h264_sd_url!!,
            title,path,str.size!!,fileLength,false,Constants.PROGRESS))
        adapter.notifyDataSetChanged()
    }

    override fun onSelect(lightDownload: FlashLightDownload) {
                    coroutineScope.launch {

                val msg =  withContext(Dispatchers.Default){
                    windowDao.addDownload(lightDownload)
                    return@withContext "Download Start"
                }
                dismiss()
                Log.i(TAG, "onViewCreated: $msg")
                        size = true
                mainBinding.pager.currentItem = 1

            }
    }


}