package com.victor.achieveplay.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.victor.achieveplay.ui.view.SearchGamesFragment
import com.victor.achieveplay.ui.view.SearchUsersFragment

class DiscoveryPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SearchGamesFragment()
            1 -> SearchUsersFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
