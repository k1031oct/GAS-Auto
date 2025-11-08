package com.gws.auto.mobile.android.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.gws.auto.mobile.android.R
// import com.gws.auto.mobile.android.ui.components.AppChart
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        view.findViewById<ComposeView>(R.id.dashboard_compose_view).apply {
            setContent {
                GWSAutoForAndroidTheme {
                    // AppChart()
                }
            }
        }

        return view
    }
}
