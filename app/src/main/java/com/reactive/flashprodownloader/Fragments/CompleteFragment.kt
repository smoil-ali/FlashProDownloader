package com.reactive.flashprodownloader.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.reactive.flashprodownloader.Interfaces.MainActivityListener
import com.reactive.flashprodownloader.Interfaces.OnBackPressedListener
import com.reactive.flashprodownloader.R
import com.reactive.flashprodownloader.databinding.FragmentCompleteBinding


class CompleteFragment : BaseFragment(),MainActivityListener,OnBackPressedListener {
    private val TAG = CompleteFragment::class.simpleName
    lateinit var binding: FragmentCompleteBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompleteBinding.inflate(inflater,container,false)
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