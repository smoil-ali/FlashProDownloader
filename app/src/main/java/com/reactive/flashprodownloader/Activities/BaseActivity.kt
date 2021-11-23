package com.reactive.flashprodownloader.Activities

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.reactive.flashprodownloader.MyApp
import com.reactive.flashprodownloader.Room.FlashDao

open class BaseActivity:AppCompatActivity() {
    private val TAG = BaseActivity::class.simpleName
    lateinit var flashDao: FlashDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: ")
        flashDao = MyApp.instance.flashDao
    }
    
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Log.i(TAG, "onCreate: two")
    }
}