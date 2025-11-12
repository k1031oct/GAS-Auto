package com.gws.auto.mobile.android.ui.wizard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class WizardPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LocaleFragment()
            1 -> WeekStartFragment()
            2 -> ThemeFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
