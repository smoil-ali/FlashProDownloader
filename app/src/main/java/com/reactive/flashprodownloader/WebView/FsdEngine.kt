package com.reactive.flashprodownloader.WebView

import android.util.Log
import com.reactive.flashprodownloader.Helper.Utils
import com.reactive.flashprodownloader.model.FlashLightDownloadPro
import com.reactive.flashprodownloader.Interfaces.WebViewCallbacks
import kotlinx.coroutines.*
import java.net.URL
import java.net.URLConnection

object FsdEngine {
    private val TAG = FsdEngine::class.simpleName
    private var coroutineScope = CoroutineScope(Dispatchers.Main)

    fun startEngine(link: String, page: String, title: String?, listener: WebViewCallbacks){
        coroutineScope.launch(Dispatchers.Default) {
            val host = URL(page).host
            var website: String? = null
            val name = "video"
            if (host.contains("something naughty")){
                Log.i(TAG, "startEngine: dont")
            }

            Log.i(TAG, "startEngine: $link")
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
                    val size = lCon.getHeaderField("content-length")
                    Log.i(TAG, "startEngine: ${Utils.getStringSizeLengthFile(size.toInt())}")
                    website = "like.com"
                    coroutineScope.launch(Dispatchers.Main) {
                        listener.milGaiVideo(
                            FlashLightDownloadPro(size,"mp4",
                                link,"video",page,false,website)
                        )
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

    private fun runDailyEngine(
        link: String, page: String, listener: WebViewCallbacks
    ){


        Log.i(TAG, "runDailyEngine: $link")
        val name = "video"
        if (link.contains("www.dailymotion.com/player/metadata/video/") ||
            link.contains("www.dailymotion.com/embed/video")){

            Log.i(TAG, "onLoadResource: dailymotion url $link")

        }

    }


    private fun runEngine(
        urlCon: URLConnection, contentType: String, page: String,
        title: String?,
        listener: WebViewCallbacks,
        url: String
    ) {
        var size = urlCon.getHeaderField("content-length")
        var link = urlCon.getHeaderField("Location")
        if (link == null){
            link = urlCon.url.toString()
        }

        val host = URL(page).host
        var website: String? = null
        var chunked: Boolean = false

        Log.i(TAG, "runEngine: $url")
        if (host.contains("something naughty"))
            return;

        var name = "video"
        if (title != null){
            name = if (contentType.contains("audio")){
                "[AUDIO ONLY]$title"
            }else {
                title
            }
        }else if (contentType.contains("audio")){
            name = "audio"
        }
        val type = when(contentType){
            "vide/mp4" -> "mp4"
            "video/webm" -> "webm"
            "video/mp2t" -> "ts"
            "audio/webm" -> "webm"
            else -> "mp4"
        }

        if(host.contains("facebook.com") &&
            link.contains("bytestart")){
            val lastIndex = link.lastIndexOf("&bytestart")
            val index = link.indexOf("fbcdn")

            if (lastIndex > 0){
                link = "https://video.xx." + link.substring(index, lastIndex)
            }
            val fbcon:URLConnection = URL(link).openConnection()
            fbcon.connect()
            size = fbcon.getHeaderField("content-length")
            website = "facebook.com"
            coroutineScope.launch(Dispatchers.Main) {
                listener.milGaiVideo(FlashLightDownloadPro(size,type,link,name,page,chunked,website))
            }

        }
    }

    fun stopEngine(){
        Log.i(TAG, "stopEngine: called")
    }

}