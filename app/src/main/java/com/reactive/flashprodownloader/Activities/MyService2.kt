package com.reactive.flashprodownloader.Activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.reactive.flashprodownloader.Helper.Constants
import com.reactive.flashprodownloader.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyService2 : Service() {
    private val TAG = MyService2::class.simpleName
    private val CHANNEL_ID = Constants.PACKAGE_NAME
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    var i:Int = 0
    override fun onCreate() {
        super.onCreate()
        showNotification()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        coroutineScope.launch(Dispatchers.Default) {
            while (true){
                i++
                Log.i(TAG, "onStartCommand: $i")
                delay(1000)
            }
        }
        return START_NOT_STICKY

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun showNotification(){
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(Constants.PARAMS,true)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this,CHANNEL_ID)
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
        startForeground(4,builder.build())
    }
}