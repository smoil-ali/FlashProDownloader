package com.reactive.flashprodownloader.Activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.reactive.flashprodownloader.Adapter.BookmarkAdapter
import com.reactive.flashprodownloader.Helper.Constants
import com.reactive.flashprodownloader.Interfaces.BookmarkListener
import com.reactive.flashprodownloader.databinding.ActivityBookmarkBinding
import com.reactive.flashprodownloader.model.FlashBookmark
import kotlinx.coroutines.*
import java.io.Serializable

class BookmarkActivity : BaseActivity(), BookmarkListener {
    private val TAG = BookmarkActivity::class.simpleName
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    lateinit var binding: ActivityBookmarkBinding
    lateinit var adapter: BookmarkAdapter
    val list:MutableList<FlashBookmark> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookmarkBinding.inflate(layoutInflater)

        adapter = BookmarkAdapter(this,list)
        adapter.listener = this
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.hasFixedSize()
        binding.recycler.adapter = adapter

        setContentView(binding.root)

        getData()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun getData(){
        flashDao.getBookmarks().observe(this, Observer {
            adapter.setData(it)
            if (it.isNotEmpty()){
                binding.alertTitle.visibility = View.GONE
            }else{
                binding.alertTitle.visibility = View.VISIBLE
            }
        })
    }

    override fun onSelect(bookmark: FlashBookmark) {
        val intent = Intent()
        intent.putExtra(Constants.PARAMS,bookmark as Serializable)
        setResult(Constants.BOOKMARK,intent)
        finish()
    }

    override fun onDelete(bookmark: FlashBookmark) {
        coroutineScope.launch(Dispatchers.Default) {
            flashDao.deleteBookmark(bookmark)
        }
    }

    override fun onStop() {
        super.onStop()
        coroutineScope.coroutineContext.cancelChildren()
    }

}