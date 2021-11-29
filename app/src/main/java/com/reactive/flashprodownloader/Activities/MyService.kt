package com.reactive.flashprodownloader.Activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.*
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.room.CoroutinesRoom
import com.bumptech.glide.util.Util
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.reactive.flashprodownloader.Helper.Constants
import com.reactive.flashprodownloader.Helper.PR
import com.reactive.flashprodownloader.Helper.Utils
import com.reactive.flashprodownloader.Interfaces.ProgressListener
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.model.FlashLightDownload
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import kotlinx.coroutines.*
import java.lang.Process

class MyService : Service() {
    private val TAG = MyService::class.simpleName
    val CHANNEL_ID = Constants.PACKAGE_NAME
    lateinit var fetch: Fetch
    lateinit var builder:NotificationCompat.Builder
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: ")
        val config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .build()
        PRDownloader.initialize(this, config)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        val lightDownload = intent?.extras?.getSerializable(Constants.PARAMS) as FlashLightDownload

        Log.i(TAG, "onStartCommand: $lightDownload")


        PRDownloader.download(lightDownload.url, lightDownload.path,
            lightDownload.title)
            .build()
            .setOnStartOrResumeListener {
                Log.i(TAG, "onStartCommand: ")
                showNotification(lightDownload.id,0)
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
                showNotification(lightDownload.id,result.toInt())
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Log.i(TAG, "onDownloadComplete: ")
                    PR.deleteId(lightDownload.id)
                    stopSelf()

                }
                override fun onError(error: com.downloader.Error?) {
                    Log.i(TAG, "onError: ${error.toString()}")
                }
            })


        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
    }



    override fun onBind(intent: Intent): IBinder? {
        Log.i(TAG, "onBind: ")
        return null
    }

    private fun showNotification(id: Int,progress: Int){
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


        val MAX_PROGRESS = 100
        builder.setProgress(MAX_PROGRESS,progress,false)
        val notificationManager = NotificationManagerCompat.from(this)
        startForeground(id,builder.build())
    }


}