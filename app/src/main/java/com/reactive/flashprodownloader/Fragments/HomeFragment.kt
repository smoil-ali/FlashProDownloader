package com.reactive.flashprodownloader.Fragments

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.*

import android.webkit.WebView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.reactive.flashprodownloader.Activities.BookmarkActivity
import com.reactive.flashprodownloader.Activities.HistoryActivity
import com.reactive.flashprodownloader.Activities.TabActivity
import com.reactive.flashprodownloader.Adapter.HomePageAdapter
import com.reactive.flashprodownloader.Dialogs.SizeFragment
import com.reactive.flashprodownloader.Helper.Constants
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.databinding.FragmentHomeBinding
import com.reactive.flashprodownloader.Helper.Utils
import com.reactive.flashprodownloader.Interfaces.*
import com.reactive.flashprodownloader.WebView.CustomWebView
import com.reactive.flashprodownloader.WebView.FsdEngine
import com.reactive.flashprodownloader.model.*
import kotlinx.coroutines.*
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class HomeFragment : BaseFragment(), WebViewCallbacks, HomePageAdapterCallbacks
    , OnBackPressedListener
    , MainActivityListener
    , MainActivityBookmarkListener
    , MainActivityHistoryListener
    , MainActivityTabListener
    , androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener
    , SharedPreferences.OnSharedPreferenceChangeListener {
    private val TAG = HomeFragment::class.java.simpleName
    private val map: HashMap<Int, WebView> = HashMap()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val SNAPSHOT_DELAY: Long = 4000
    private lateinit var sizeFragment: SizeFragment

    lateinit var binding: FragmentHomeBinding
    lateinit var adapter: HomePageAdapter
    lateinit var currentWindow: FlashWindow
    lateinit var list: List<FlashWindow>
    lateinit var sdf: SimpleDateFormat
    var currentWebView: WebView? = null
    var customWebView: CustomWebView? = null
    var stopRun = true
    lateinit var sharedPrefernces: SharedPreferences

    private val videoList: MutableLiveData<MutableList<FlashLightDownloadPro>> = MutableLiveData()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = HomePageAdapter(requireContext(), listBrowsers, this)
        binding.homePage.iconRecycler.layoutManager = GridLayoutManager(requireContext(), 5)
        binding.homePage.iconRecycler.adapter = adapter
        stopRun = true
        videoList.value = mutableListOf()

        binding.homePage.searchUrl.setOnEditorActionListener { textView, i, keyEvent ->
            if (binding.homePage.searchUrl.text.isNotEmpty()) {
                val search = binding.homePage.searchUrl.text.toString().trim()
                currentWindow.title = binding.homePage.searchUrl.text.toString()
                currentWindow.url = makeUrl(search)
                currentWindow.path = sdf.format(Date())
                loadWebView()
                binding.homePage.searchUrl.setText("")
            }
            Utils.hideKeyBoardEdt(binding.homePage.searchUrl, requireContext())
            binding.homePage.searchUrl.clearFocus()
            true
        }

        binding.homeBrowser.searchUrl.setOnEditorActionListener { textView, i, keyEvent ->
            if (binding.homeBrowser.searchUrl.text.isNotEmpty()) {
                val search = binding.homeBrowser.searchUrl.text.toString().trim()
                currentWindow.title = binding.homeBrowser.searchUrl.text.toString()
                currentWindow.url = makeUrl(search)
                currentWindow.path = sdf.format(Date())
                map.remove(currentWindow.id)
                loadWebView()
            }
            Utils.hideKeyBoardEdt(binding.homeBrowser.searchUrl, requireContext())
            binding.homeBrowser.searchUrl.clearFocus()
            true
        }

        binding.homePage.menu.setOnClickListener {
            showPopup(it,R.menu.menu1)
        }

        binding.homeBrowser.menu.setOnClickListener {
            showPopup(it,R.menu.menu2)
        }

        binding.homeBrowser.reload.setOnClickListener {
            binding.homeBrowser.webViewContainer.removeView(currentWebView)
            currentWebView = newWebView()
            map[currentWindow.id] = currentWebView!!
            binding.homeBrowser.webViewContainer.addView(currentWebView, 0)
            binding.homeBrowser.pbPageLoading.visibility = View.VISIBLE
            currentWindow.url = binding.homeBrowser.searchUrl.text.toString()
            currentWebView!!.loadUrl(currentWindow.url!!)
            currentWebView!!.requestFocus()
        }

        binding.homeBrowser.downloadButton.setOnClickListener {
            sizeFragment = SizeFragment(videoList.value)
            Utils.openDialog(requireActivity().supportFragmentManager, sizeFragment)
        }

        binding.homePage.tab.setOnClickListener {
            openNewTab()
        }

        binding.homeBrowser.tab.setOnClickListener{
            openNewTab()
        }


        videoList.observe(requireActivity(), androidx.lifecycle.Observer {
            Log.i(TAG, "onViewCreated: hm yha b h ${it.size} $it")
            if (it.isNotEmpty()){
                binding.homeBrowser.downloadButton
                    .startAnimation(Constants.loadAnimation(requireContext()))
            }
            binding.homeBrowser.downloadButton.isEnabled = it.isNotEmpty()
        })



        getAllWindows()

        sharedPrefernces = requireContext().getSharedPreferences(
            "com.reactive.FlashProDownloader",
            Context.MODE_PRIVATE
        )

        sharedPrefernces.registerOnSharedPreferenceChangeListener(this);

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        sdf = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.ENGLISH)


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        getWindow()
    }


    override fun onResume() {
        super.onResume()


        if (currentWebView != null){
            currentWebView?.onResume()
            setOnBackPressedListener(this)
        }


        HomeSelection()
        setMainActivityListener(this)
        setBookmarkMain(this)
        setHistoryMain(this)
        setTabListener(this)



        if (!Utils.isSnapShotTaken(requireContext())) {
            binding.parentView.viewTreeObserver
                .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        binding.parentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        Utils.takeScreenShot(
                            binding.parentView, requireContext(),
                            Constants.HOME_PAGE_TITLE
                        )
                    }
                })
        }
    }

    private fun showPopup(v: View?,menuRes: Int) {
        val popup = androidx.appcompat.widget.PopupMenu(
            requireContext(),
            v!!,Gravity.NO_GRAVITY,
            R.attr.actionOverflowMenuStyle,0
        )
        popup.setOnMenuItemClickListener(this)
        popup.inflate(menuRes)

        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.bookmark) {
            coroutineScope.launch {
                val msg = withContext(Dispatchers.Default) {
                    flashDao.addBookmark(
                        FlashBookmark(
                            0,
                            currentWindow.url,
                            currentWindow.title
                        )
                    )
                    "Added"
                }
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
            }

        } else if (id == R.id.history) {
            val intent = Intent(requireContext(), HistoryActivity::class.java)
            resultLauncher.launch(intent)
            return true
        } else if (id == R.id.bookmarkAdd) {
            val intent = Intent(requireContext(), BookmarkActivity::class.java)
            resultLauncher.launch(intent)
            return true
        } else if (id == R.id.privacy_policy){
            Log.i(TAG, "onMenuItemClick: Privacy Policy")
        } else if (id == R.id.share){
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,R.string.app_name)
            val shareMessage = "Let me recommend you this application\nhttps://play.google.com/store/apps/details?id="+Constants.PACKAGE_NAME
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } else if (id == R.id.rate){
            val uri: Uri = Uri.parse("market://details?id=${Constants.PACKAGE_NAME}")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=${Constants.PACKAGE_NAME}")))
            }
        }
        return false
    }

    private fun openNewTab() {
        val intent = Intent(requireContext(),TabActivity::class.java)
        startActivity(intent)
    }

    private fun getWindow() {
        val save_id = Utils.getSelectedId(requireContext())
        Log.i(TAG, "getWindow: save id $save_id")
        if (save_id != 0) {
            coroutineScope.launch {
                try {
                    currentWindow = withContext(Dispatchers.Default) {
                        val window = flashDao.getFlashWindowById(save_id)
                        return@withContext window
                    }
                    if (currentWindow.url != Constants.BLANK_URL) {
                        loadWebView()
                    } else {
                        binding.homeBrowser.webViewContainer.removeAllViews()
                        showHomePage()
                    }
                }catch (exception: NullPointerException){
                    Log.i(TAG, "getWindow: null e oye")
                    createNewTab()
                }
                Log.i(TAG, "getWindow: $currentWindow")
            }
        }
    }

    private fun getAllWindows() {
        flashDao.getAllFlashWindows().observe(requireActivity(),
            androidx.lifecycle.Observer {
            Log.i(TAG, "getAllWindows: $it")
            if (it.isNotEmpty()) {
                if (it.size == 1) {
                    currentWindow = it[0]
                    Log.i(TAG, "getAllWindows: ${currentWindow.id}")
                    Utils.saveSelectedId(requireContext(), currentWindow.id)
                }
                binding.homePage.tabNumber.text = it.size.toString()
                binding.homeBrowser.tabNumber.text = it.size.toString()
            }else{
                createNewTab()
            }
        })

    }

    private fun createNewTab(){
        showHomePage()
        currentWindow = FlashWindow(
            0, Constants.BLANK_URL,
            Constants.BLANK_URL, Constants.BLANK_URL,true
        )
        coroutineScope.launch(Dispatchers.Default) {
            flashDao.createNewFlashWindow(currentWindow)
            Log.i(TAG, "getWindow: $currentWindow")
        }
    }


    private fun getMyWebView(): Boolean {
        return map.containsKey(currentWindow.id)
    }

    private fun showBrowser() {
        binding.homePage.root.visibility = View.GONE
        binding.homeBrowser.root.visibility = View.VISIBLE
    }

    private fun showHomePage() {
        currentWebView?.destroy()
        binding.homePage.root.visibility = View.VISIBLE
        binding.homeBrowser.root.visibility = View.GONE
    }


    private fun loadWebView() {
        binding.homeBrowser.webViewContainer.removeAllViews()
        videoList.value = mutableListOf()
        if (getMyWebView()) {
            Log.i(TAG, "loadwebView: current webview null")
            currentWebView = map[currentWindow.id]!!
            binding.homeBrowser.webViewContainer.addView(currentWebView, 0)
        } else {
            currentWebView = newWebView()
            map.put(currentWindow.id, currentWebView!!)
            binding.homeBrowser.webViewContainer.addView(currentWebView, 0)
            binding.homeBrowser.pbPageLoading.visibility = View.VISIBLE
            currentWebView!!.loadUrl(currentWindow.url!!)
        }
        setOnBackPressedListener(this)
        binding.homeBrowser.searchUrl.setText(currentWindow.url)
        currentWebView!!.requestFocus()
        showBrowser()

    }

    private fun takeSnapShot(title: String) {
        Utils.takeScreenShot(
            binding.homeBrowser.webViewContainer,
            requireContext(), title
        )
    }


    private fun updateData() {
        coroutineScope.launch(Dispatchers.Default) {
            flashDao.updateFlashWindow(
                currentWindow.id, currentWindow.title!!,
                currentWindow.url!!, currentWindow.path!!
            )
        }

    }

    private fun newWebView(): WebView {
        customWebView = CustomWebView(this, currentWindow, requireContext())
        return customWebView!!.getWebView()
    }


    private fun makeUrl(str: String): String {
        val http = "http://"
        val concat = if (str.startsWith(http) || str.startsWith("https://")) str else http + str
        return if (Patterns.WEB_URL.matcher(concat).matches()) {
            concat
        } else "https://www.google.com/search?q=$str"
    }

    private fun addHistory() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        coroutineScope.launch(Dispatchers.Default) {
            flashDao.addHistory(
                FlashHistory(
                    0, currentWindow.url!!,
                    currentWindow.title!!, dateFormat.format(Date())
                )
            )
        }
    }

    private fun same(link: String): Boolean {
        videoList.value?.forEach {
            if (it.link.equals(link)) {
                return false
            }
        }
        return true
    }


    override fun OnScroll() {
        Log.i(TAG, "OnScroll: ")
        currentWindow.path = sdf.format(Date())
        takeSnapShot(currentWindow.path!!)
        updateData()
    }

    override fun OnPageStart(window: FlashWindow) {
        currentWindow.url = window.url
        currentWindow.path = window.path
        currentWindow.title = window.title
        addHistory()
        updateData()
    }

    override fun OnPageFinish(window: FlashWindow, webView: WebView) {
        Log.i(TAG, "OnPageFinish: $window")
        coroutineScope.launch(Dispatchers.Default) {
            delay(SNAPSHOT_DELAY)
            takeSnapShot(currentWindow.path!!)
        }
        binding.homeBrowser.pbPageLoading.visibility = View.GONE
    }


    override fun OnLoadingResource(window: FlashWindow) {

    }

    override fun OnNewPage(window: FlashWindow) {
        takeSnapShot(window.path!!)
        currentWindow.url = window.url
        currentWindow.path = window.path
        binding.homeBrowser.searchUrl.setText(currentWindow.url)
        updateData()
        addHistory()
    }

    override fun OnProgressChange(progress: Int) {
        binding.homeBrowser.pbPageLoading.setProgress(progress)
    }

    override fun OnSelect(home: FlashHome) {

        stopRun = true
        binding.homeBrowser.webViewContainer.removeAllViews()
        currentWindow.url = home.url
        currentWindow.title = home.title
        currentWindow.path = sdf.format(Date())
        currentWebView = newWebView()
        map.put(currentWindow.id, currentWebView!!)
        binding.homeBrowser.webViewContainer.addView(currentWebView, 0)
        binding.homeBrowser.searchUrl.setText(home.url)
        binding.homeBrowser.pbPageLoading.visibility = View.VISIBLE
        setOnBackPressedListener(this)
        currentWebView!!.loadUrl(home.url)
        showBrowser()
    }

    override fun onBackPressed() {
        Log.i(TAG, "onBackPressed: ${currentWebView?.canGoBack()}")
        if (currentWebView?.canGoBack() == true) {
            stopRun = false
            videoList.value = mutableListOf()
            currentWebView!!.goBack()
        } else {
            binding.homeBrowser.webViewContainer.removeAllViews()
            showHomePage()
            map.remove(currentWindow.id)
            currentWindow.title = Constants.BLANK_URL
            currentWindow.url = Constants.BLANK_URL
            currentWindow.path = Constants.BLANK_URL
            currentWebView = null
            customWebView?.stopEngine()
            videoList.value = mutableListOf()
            updateData()
            setOnBackPressedListener(null)
        }
    }

    override fun onHomeClick() {
        binding.homeBrowser.webViewContainer.removeAllViews()
        showHomePage()
        currentWindow.url = Constants.BLANK_URL
        currentWindow.title = Constants.BLANK_URL
        currentWindow.path = Constants.BLANK_URL
        map.remove(currentWindow.id)
        customWebView?.stopEngine()
        videoList.value = mutableListOf()
        updateData()
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop: ")
        map.remove(currentWindow.id)
        binding.homeBrowser.webViewContainer.removeView(currentWebView)
        currentWebView?.removeAllViews()
        currentWebView?.destroy()
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
        coroutineScope.coroutineContext.cancelChildren()
        sharedPrefernces.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onBookmark(bookmark: FlashBookmark) {
        currentWindow.url = bookmark.url
        currentWindow.title = bookmark.title
        if (getMyWebView()) {
            currentWebView = map[currentWindow.id]!!
            binding.homeBrowser.webViewContainer.removeView(currentWebView)
        }
        currentWebView = newWebView()
        map[currentWindow.id] = currentWebView!!
        binding.homeBrowser.webViewContainer.addView(currentWebView, 0)
        binding.homeBrowser.pbPageLoading.visibility = View.VISIBLE
        currentWebView!!.loadUrl(currentWindow.url!!)
        setOnBackPressedListener(this)
        binding.homeBrowser.searchUrl.setText(currentWindow.url)
        currentWebView!!.requestFocus()
        showBrowser()
        updateData()
    }


    override fun onHistory(history: FlashHistory) {
        currentWindow.url = history.url
        currentWindow.title = history.title
        if (getMyWebView()) {
            currentWebView = map[currentWindow.id]!!
            binding.homeBrowser.webViewContainer.removeView(currentWebView)
        }
        currentWebView = newWebView()
        map[currentWindow.id] = currentWebView!!
        binding.homeBrowser.webViewContainer.addView(currentWebView, 0)
        binding.homeBrowser.pbPageLoading.visibility = View.VISIBLE
        currentWebView!!.loadUrl(currentWindow.url!!)
        setOnBackPressedListener(this)
        binding.homeBrowser.searchUrl.setText(currentWindow.url)
        currentWebView!!.requestFocus()
        showBrowser()
        updateData()
    }

    override fun milGaiVideo(lightDownloadPro: FlashLightDownloadPro) {
            Log.i(TAG, "milGaiVideo: $lightDownloadPro")

        try {
            if (lightDownloadPro.website == "dailymotion.com") {
                currentWindow.url = lightDownloadPro.link
                currentWindow.title = lightDownloadPro.website
                updateData()
                binding.homeBrowser.searchUrl.setText(lightDownloadPro.link)
                val sb = StringBuilder(lightDownloadPro.link!!)
                sb.insert(27, Constants.JSON)
                sb.append(Constants.TITLE + "," + Constants.HD)
                lightDownloadPro.link = sb.toString()
                videoList.value?.clear()
                videoList.value?.add(lightDownloadPro)
                videoList.value = videoList.value
            } else if (lightDownloadPro.website == "vimeo.com") {
                videoList.value?.clear()
                videoList.value?.add(lightDownloadPro)
                videoList.value = videoList.value
            } else if (lightDownloadPro.website == "facebook.com") {
                if (same(lightDownloadPro.link!!)) {
                    videoList.value?.add(lightDownloadPro)
                    videoList.value = videoList.value
                }
            } else if (lightDownloadPro.website == "like.com") {
                if (same(lightDownloadPro.link!!)) {
                    videoList.value?.add(lightDownloadPro)
                    videoList.value = videoList.value
                }

            } else if (lightDownloadPro.website == "imdb.com") {
                videoList.value?.clear()
                videoList.value?.add(lightDownloadPro)
                videoList.value = videoList.value
            }
        }catch (e:CancellationException){
            Log.i(TAG, "milGaiVideo: cancellation ${e.message}")
        }



    }

    override fun NotFound() {
        Log.i(TAG, "NotFound: ")
    }


    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        Log.i(TAG, "onSharedPreferenceChanged: $p1")

        if (p1.equals("SELECTED_ID")) {
            Log.i(TAG, "onSharedPreferenceChanged: called")
            getWindow()
        }

    }

    override fun onSelectTab() {
        Utils.showToast(requireContext(),"here")
    }

    override fun onPause() {
        super.onPause()
        currentWebView?.onPause()
    }




}