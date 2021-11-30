package com.reactive.flashprodownloader.Activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.reactive.flashprodownloader.Adapter.FragmentViewPager
import com.reactive.flashprodownloader.Helper.Constants
import com.reactive.flashprodownloader.Helper.Utils
import com.reactive.flashprodownloader.Interfaces.*
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.databinding.ActivityMainBinding
import com.reactive.flashprodownloader.model.FlashBookmark
import com.reactive.flashprodownloader.model.FlashHistory
import com.reactive.flashprodownloader.model.FlashHome


class MainActivity : BaseActivity() {
    private  val TAG = MainActivity::class.simpleName

    lateinit var binding: ActivityMainBinding

    private lateinit var fragmentViewPager: FragmentViewPager

    lateinit var listBrowsers:List<FlashHome>


    private lateinit var mListener: MainActivityListener

    private lateinit var bookmarkListener: MainActivityBookmarkListener

    private lateinit var historyListener: MainActivityHistoryListener

    private lateinit var tabListener: MainActivityTabListener

    private var listener: OnBackPressedListener? = null

    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if (it.resultCode == Constants.BOOKMARK){
                val data = it.data?.getSerializableExtra(Constants.PARAMS) as FlashBookmark
                showHomeScreen()
                bookmarkListener.onBookmark(data)
            }
            if(it.resultCode == Constants.HISTORY){
                val data = it.data?.getSerializableExtra(Constants.PARAMS) as FlashHistory
                showHomeScreen()
                historyListener.onHistory(data)
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        loadList()
        fragmentViewPager = FragmentViewPager(this)
        binding.pager.adapter = fragmentViewPager
        binding.pager.isUserInputEnabled = false


        if (intent.extras != null){
            downloadSelectIcon()
            homeNormal()
            completeNormal()
            binding.pager.currentItem = 1
        }else if (savedInstanceState == null){
            homeSelectIcon()
            downloadNormal()
            completeNormal()
            showHomeScreen()
        }


        binding.home.setOnClickListener {
            homeSelectIcon()
            downloadNormal()
            completeNormal()
            mListener.onHomeClick()
        }


        binding.download.setOnClickListener {
            downloadSelectIcon()
            homeNormal()
            completeNormal()
            binding.pager.currentItem = 1
        }

        binding.complete.setOnClickListener {
            completeSelectIcon()
            homeNormal()
            downloadNormal()
            binding.pager.currentItem = 2
        }



//        if (!Utils.getAccept(this)){
//            val acceptDialog = AcceptDialog()
//            acceptDialog.setStyle(DialogFragment.STYLE_NO_FRAME,
//                android.R.style.Theme_Translucent)
//            Utils.openDialog(supportFragmentManager,acceptDialog)
//        }


    }

    fun showHomeScreen(){
        binding.pager.currentItem = 0
    }


    private fun loadList(){
        listBrowsers = listOf(
            FlashHome(Constants.DAILYMOTION_URL,
            Constants.DAILYMOTION_TITLE,Constants.DAILYMOTION_ICON),
            FlashHome(Constants.VIMEO_URL,
                Constants.VIMEO_TITLE,Constants.VIMEO_ICON),
            FlashHome(Constants.FACEBOOK_URL,
                Constants.FACEBOOK_TITLE,Constants.FACEBOOK_ICON),
            FlashHome(Constants.INSTAGRAM_URL,
                Constants.INSTAGRAM_TITLE,Constants.INSTAGRAM_ICON),
            FlashHome(Constants.LIKEE_URL,
                Constants.LIKEE_TITLE,
                Constants.LIKEE_ICON),
            FlashHome(Constants.IMDB_URL,
                Constants.IMDB_TITLE,
                Constants.IMDB_ICON),
            FlashHome(Constants.WIKI_URL,
            Constants.WIKI_TITLE,Constants.WIKI_ICON),
            FlashHome(Constants.ALIEXPRESS_URL,
            Constants.ALIEXPRESS_TITLE,Constants.ALIEXPRESS_ICON),
            FlashHome(Constants.CRICBUZZ_URL,
            Constants.CRICBUZZ_TITLE,Constants.CRICBUZZ_ICON),
            FlashHome(Constants.ESPN_URL,Constants.ESPN_TITLE,Constants.ESPN_ICON)

        )
    }



    fun setMainActivityListener(listener: MainActivityListener){
        this.mListener = listener
    }

    fun setOnBackPressedListener(onBackPressedListener: OnBackPressedListener?) {
        this.listener = onBackPressedListener
    }

    fun setMainActivityBookmarkListener(listener: MainActivityBookmarkListener){
        bookmarkListener = listener
    }

    fun setMainActivityHistoryListener(listener: MainActivityHistoryListener){
        historyListener = listener
    }

    fun setMainTabListener(listener: MainActivityTabListener){
        tabListener = listener
    }

    override fun onBackPressed() {
        if (listener != null) {
            listener?.onBackPressed()
        } else {
            exitApp()
        }
    }


    fun homeSelectIcon() {
        binding.home.setImageDrawable(
            ContextCompat.getDrawable(
                this@MainActivity,
                R.drawable.home_icon_selected
            )
        )
    }

    fun downloadSelectIcon() {
        binding.download.setImageDrawable(
            ContextCompat.getDrawable(
                this@MainActivity,
                R.drawable.downloadind_icon_selected
            )
        )
    }

    fun completeSelectIcon() {
        binding.complete.setImageDrawable(
            ContextCompat.getDrawable(
                this@MainActivity,
                R.drawable.completed_icon_selected
            )
        )
    }

    fun homeNormal() {
        binding.home.setImageDrawable(
            ContextCompat.getDrawable(
                this@MainActivity,
                R.drawable.home_icon
            )
        )
    }

    fun downloadNormal() {
        binding.download.setImageDrawable(
            ContextCompat.getDrawable(
                this@MainActivity,
                R.drawable.downloadind_icon
            )
        )
    }

    fun completeNormal() {
        binding.complete.setImageDrawable(
            ContextCompat.getDrawable(
                this@MainActivity,
                R.drawable.completed_icon
            )
        )
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.i(TAG, "onNewIntent: called")
        Utils.showToast(this,"intent")
    }




    private fun exitApp(){
        AlertDialog.Builder(this)
            .setTitle("Alert")
            .setMessage("Do you want to exit?")
            .setPositiveButton("Yes"
            ) { dialog, which -> finish() }
            .setNegativeButton("No"){
                dialog,which -> dialog.dismiss()
            }
            .show()
//        val dialog = ExitDialog()
//        dialog.setStyle(DialogFragment.STYLE_NO_FRAME,
//            android.R.style.Theme_Translucent)
//        dialog.isCancelable = true
//        Utils.openDialog(supportFragmentManager,dialog)
    }
}