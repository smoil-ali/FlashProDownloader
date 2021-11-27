package com.reactive.flashprodownloader.WebView

import android.util.Log
import com.reactive.flashprodownloader.Helper.Utils
import com.reactive.flashprodownloader.model.FlashLightDownloadPro
import com.reactive.flashprodownloader.Interfaces.WebViewCallbacks
import kotlinx.coroutines.*
import java.net.URL
import java.net.URLConnection
import kotlin.coroutines.coroutineContext


object FsdEngine {
    private val TAG = FsdEngine::class.simpleName
    private lateinit var coroutineScope:CoroutineScope;

    fun startEngine(link: String, page: String, title: String?, listener: WebViewCallbacks){
        Log.i(TAG, "startEngine: ${coroutineScope.coroutineContext.isActive}")
        coroutineScope.launch(Dispatchers.Default) {
            val host = URL(page).host
            var website: String? = null
            val name = "video"
            if (host.contains("something naughty")){
                Log.i(TAG, "startEngine: dont")
            }
                if (link.contains("www.dailymotion.com/player/metadata/video/") ||
                    link.contains("www.dailymotion.com/embed/video")){
                    val parts = link.split("/","?").toTypedArray()
                    parts.forEach {
                        Log.i(TAG, "onLoadResource: $it")
                    }
                    var i = 0
                    while (i < parts.size) {
                        if (parts[i] == "video") {
                            Log.i(TAG, parts[i])
                            val videoUrl = "https://www.dailymotion.com/video/" + parts[i + 1]
                            Log.i(TAG, "onLoadResource: final video url $videoUrl")
                            website = "dailymotion.com"
                            i = parts.size

                            coroutineScope.launch(Dispatchers.Main) {
                                listener.milGaiVideo(
                                    FlashLightDownloadPro(null,
                                        "mp4",videoUrl,name,page,false,website)
                                )
                            }
                        }
                        i++
                    }
                }else if(link.contains("player.vimeo.com/video/") && link.contains("config")){

                    val parts = link.split("/").toTypedArray()
                    var i = 0
                    while (i < parts.size){
                        if (parts[i] == "video"){
                            val url = "https://vimeo.com/" + parts[i + 1]
                            i = parts.size

                            coroutineScope.launch(Dispatchers.Main) {
                                listener.milGaiVideo(
                                    FlashLightDownloadPro(null,"mp4",
                                        url,page,page,false,"vimeo.com")
                                )
                            }

                        }
                        i++
                    }
                }else if(link.contains("bytestart")){
                    Log.i(TAG, "startEngine: $link")
                    var mLink = link
                    val lastIndex = link.lastIndexOf("&bytestart")
                    val index = link.indexOf("fbcdn")

                    if (lastIndex > 0){
                        mLink = "https://video.xx." + link.substring(index, lastIndex)
                    }
                    val fbcon:URLConnection = URL(mLink).openConnection()
                    fbcon.connect()
                    val size = fbcon.getHeaderField("content-length")
                    website = "facebook.com"
                    Log.i(TAG, "startEngine: ${Utils.getStringSizeLengthFile(size.toInt())} $mLink")
                    coroutineScope.launch(Dispatchers.Main) {
                        listener.milGaiVideo(
                            FlashLightDownloadPro(size,"mp4",
                                mLink,"video",page,false,website)
                        )
                    }


                }else if (link.contains("https://video.like.video/eu_live")){
                    if (link.contains(".mp4")) {
                        val lCon = URL(link).openConnection()
                        lCon.connect()
                        val size:String? = lCon.getHeaderField("content-length")
                        website = "like.com"
                        if (size != null) {
                            Log.i(TAG, "startEngine: ${Utils.getStringSizeLengthFile(size.toInt())}")
                            coroutineScope.launch(Dispatchers.Main) {
                                listener.milGaiVideo(
                                    FlashLightDownloadPro(size,"mp4",
                                        link,"video",page,false,website)
                                    )
                                }
                        }

                    }
                }else if (link.contains("assets") || link.contains("internal") ||
                    link.contains("language")
                ) {
                    Log.i(TAG, "startEngine: no no oono")
                } else if (link.contains( "https://imdb-video.media-imdb.com")){
                    val lCon = URL(link).openConnection()
                    lCon.connect()
                    val size = lCon.getHeaderField("content-length")
                    Log.i(TAG, "startEngine: ${Utils.getStringSizeLengthFile(size.toInt())}")
                    website = "imdb.com"
                    coroutineScope.launch(Dispatchers.Main) {
                        listener.milGaiVideo(
                            FlashLightDownloadPro(size,"mp4",
                                link,"video",page,false,website)
                        )
                    }
                }
        }


    }

    fun stopEngine(){
        try {
            coroutineScope.cancel()
        }catch (e:CancellationException){
            Log.i(TAG, "stopEngine: ${e.printStackTrace()}")
        }


    }

    fun initializeEngine(){
        coroutineScope = CoroutineScope(Dispatchers.Main)
    }

}