package com.reactive.flashprodownloader

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import androidx.room.Room
import com.reactive.flashprodownloader.Helper.Constants
import com.reactive.flashprodownloader.Room.AppDatabase
import com.reactive.flashprodownloader.Room.FlashDao

class MyApp:Application() {

    companion object{
        lateinit var instance:MyApp
    }
    lateinit var flashDao: FlashDao
    override fun onCreate() {
        super.onCreate()
        val appDatabase = Room.databaseBuilder(this,
            AppDatabase::class.java, Constants.DATABASE_NAME).build()
        flashDao = appDatabase.windowDao()
        instance = this
    }
}