package com.reactive.flashprodownloader.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.databinding.ActivitySplashBinding
import kotlinx.coroutines.*

class Splash : AppCompatActivity() {
    private val TAG = Splash::class.simpleName
    lateinit var binding:ActivitySplashBinding
    val coroutine = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = Intent(this,MainActivity::class.java)

        coroutine.launch {
            delay(3000)
            startActivity(intent)
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        coroutine.coroutineContext.cancelChildren()
    }
}