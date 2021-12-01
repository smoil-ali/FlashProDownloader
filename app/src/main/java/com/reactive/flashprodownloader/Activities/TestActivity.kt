package com.reactive.flashprodownloader.Activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.reactive.flashprodownloader.Helper.Constants
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.databinding.ActivityTestBinding
import java.util.*
import androidx.core.content.ContextCompat




class TestActivity : AppCompatActivity() {
    private val TAG = TestActivity::class.simpleName
    val CHANNEL_ID = Constants.PACKAGE_NAME
    lateinit var binding: ActivityTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.button.setOnClickListener {
            Intent(this,MyService::class.java).also {
                startService()
            }
        }

        binding.stop.setOnClickListener {
            Intent(this,MyService::class.java).also {
                stopService()
            }
        }

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


        val MAX_PROGRESS = 100
        val CURRENT_PROGRESS = 50
        builder.setProgress(MAX_PROGRESS,CURRENT_PROGRESS,false)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1,builder.build())
    }


    fun startService() {
        val serviceIntent = Intent(this, MyService2::class.java)
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android")
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun stopService() {
        val serviceIntent = Intent(this, MyService::class.java)
        stopService(serviceIntent)
    }

}