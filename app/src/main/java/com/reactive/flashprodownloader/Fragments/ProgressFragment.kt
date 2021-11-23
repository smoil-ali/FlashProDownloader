package com.reactive.flashprodownloader.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.reactive.flashprodownloader.Interfaces.MainActivityListener
import com.reactive.flashprodownloader.Interfaces.OnBackPressedListener
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.databinding.FragmentProgressBinding


class ProgressFragment : BaseFragment(), MainActivityListener, OnBackPressedListener {
    private val TAG = ProgressFragment::class.simpleName
    lateinit var binding: FragmentProgressBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProgressBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setMainActivityListener(this)
        setOnBackPressedListener(null)

    }

    override fun onHomeClick() {
        showHomeScreen()
    }

    override fun onBackPressed() {

    }

}