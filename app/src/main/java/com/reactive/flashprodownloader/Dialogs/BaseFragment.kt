package com.reactive.flashprodownloader.Dialogs

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.reactive.flashprodownloader.Activities.MainActivity
import com.reactive.flashprodownloader.Room.FlashDao
import com.reactive.flashprodownloader.databinding.ActivityMainBinding
import com.reactive.flashprodownloader.model.FlashLightMenu


open class BaseFragment:BottomSheetDialogFragment() {

    private lateinit var mainActivity: MainActivity
    lateinit var mainBinding: ActivityMainBinding
    lateinit var listMenuItem: List<FlashLightMenu>
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    lateinit var windowDao: FlashDao
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = requireActivity() as MainActivity
        mainBinding = mainActivity.binding
        windowDao = mainActivity.flashDao
    }
}