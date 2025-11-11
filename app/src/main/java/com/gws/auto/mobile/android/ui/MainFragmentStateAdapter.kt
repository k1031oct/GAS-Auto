package com.gws.auto.mobile.android.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gws.auto.mobile.android.ui.dashboard.DashboardFragment
import com.gws.auto.mobile.android.ui.history.HistoryFragment
import com.gws.auto.mobile.android.ui.schedule.ScheduleFragment
import com.gws.auto.mobile.android.ui.workflow.WorkflowFragment

class MainFragmentStateAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> WorkflowFragment()
            1 -> ScheduleFragment()
            2 -> HistoryFragment()
            3 -> DashboardFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
