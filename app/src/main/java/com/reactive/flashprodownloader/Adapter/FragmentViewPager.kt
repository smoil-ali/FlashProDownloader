package com.reactive.flashprodownloader.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.reactive.flashprodownloader.Fragments.CompleteFragment
import com.reactive.flashprodownloader.Fragments.HomeFragment
import com.reactive.flashprodownloader.Fragments.ProgressFragment


class FragmentViewPager(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    val MAX:Int = 3
    override fun getItemCount(): Int {
        return MAX
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return HomeFragment()
            1 -> return ProgressFragment()
            2 -> return CompleteFragment()
        }
        return HomeFragment()
    }
}