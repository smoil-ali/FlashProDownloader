package com.reactive.flashprodownloader.Activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.*
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.room.CoroutinesRoom
import com.bumptech.glide.util.Util
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.reactive.flashprodownloader.Helper.Constants
import com.reactive.flashprodownloader.Helper.PR
import com.reactive.flashprodownloader.Helper.Utils
import com.reactive.flashprodownloader.Interfaces.ProgressListener
import com.reactive.flashprodownloader.MyApp
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.Room.FlashDao
import com.reactive.flashprodownloader.model.FlashLightDownload
import com.reactive.flashprodownloader.model.FlashLightDownloadsIds
import com.reactive.flashprodownloader.model.FlashProgress
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import kotlinx.coroutines.*
import java.lang.Process

class MyService : LifecycleService() {
    private val TAG = MyService::class.simpleName
    private val CHANNEL_ID = Constants.PACKAGE_NAME
    private var ids: MutableLiveData<Int> = MutableLiveData()
    lateinit var builder:NotificationCompat.Builder
//    private lateinit var itemLightDownload: FlashLightDownload
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    lateinit var flashDao: FlashDao
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: ")
        val config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .build()
        PRDownloader.initialize(this, config)

        flashDao = MyApp.instance.flashDao

        showNotification()

//        ids.observe(this, Observer {
//            Log.i(TAG, "onCreate: ${ids.value}")
//            if(it != 0){
//                Log.i(TAG, "onStart: $ids")
//                PR.addDownloadId(FlashLightDownloadsIds(0,it,itemLightDownload.id))
//            }
//        })

        flashDao.getProgressDownloads(Constants.PROGRESS).observe(this, Observer {
            Log.i(TAG, "onCreate: $it")
            if (it.isEmpty()){
                stopSelf()
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        coroutineScope.launch(Dispatchers.Default) {
        val itemLightDownload = intent?.extras?.getSerializable(Constants.PARAMS) as FlashLightDownload
        val exist:FlashLightDownloadsIds? = PR.existId(itemLightDownload.id)
        Log.i(TAG, "onStartCommand: $itemLightDownload")
            if (true){
                PRDownloader.download(itemLightDownload.url,
                    itemLightDownload.path,
                    itemLightDownload.title)
                    .build()
                    .setOnStartOrResumeListener {
                        Log.i(TAG, "onStart resume: ${ids.value}")
                        coroutineScope.launch(Dispatchers.Default) {
                            flashDao.addProgress(FlashProgress(0,itemLightDownload.id,0))
                        }
                    }
                    .setOnPauseListener {
                        Log.i(TAG, "on pause: ")
                    }
                    .setOnCancelListener {
                        Log.i(TAG, "on cancel: ")
                    }
                    .setOnProgressListener {
                        val result = (it.currentBytes * 100) / it.totalBytes
                        Log.i(TAG, "onStartCommand: $itemLightDownload.id $result")
                        coroutineScope.launch(Dispatchers.Default) {
                            flashDao.updateProgress(itemLightDownload.id
                                ,result.toInt())
                        }
                    }
                    .start(object : OnDownloadListener {
                        override fun onDownloadComplete() {
                            Log.i(TAG, "onDownloadComplete: ")
                            PR.deleteId(itemLightDownload.id)
                            coroutineScope.launch(Dispatchers.Default) {
                                flashDao.deleteFlashProgress(itemLightDownload.id)
                            }
                            update(itemLightDownload,Constants.COMPLETE);
                        }
                        override fun onError(error: com.downloader.Error?) {
                            Log.i(TAG, "onError: ${error.toString()}")
                        }
                    })
            }else{
                if (exist != null) {
                    PRDownloader.resume(exist.downloadId)
                }
            }
        }


        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
    }



    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        Log.i(TAG, "onBind: ")
        return null
    }

    private fun showNotification(){
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(Constants.PARAMS,true)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_icon)
            .setContentTitle("Downloading...")
            .setContentText("File is downloading...")
            .setStyle(NotificationCompat.BigTextStyle().bigText("oye hoye..."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelName = getString(R.string.app_name)
            val description = "Video Downloading..."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID,channelName,importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
        startForeground(1,builder.build())
    }

    private fun update(lightDownload: FlashLightDownload,status: String){
        coroutineScope.launch(Dispatchers.Default) {
            flashDao.updateDownload(status,lightDownload.id,
                Utils.getDownloadVideoPath(this@MyService))
        }
    }

}