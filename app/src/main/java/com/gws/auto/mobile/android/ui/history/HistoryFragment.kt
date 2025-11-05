package com.gws.auto.mobile.android.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.domain.model.HistoryItem

class HistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        val historyItems = listOf(
            HistoryItem("Workflow B", "Success", "2 hours ago"),
            HistoryItem("Workflow A", "Failure", "5 hours ago"),
            HistoryItem("Workflow C", "Success", "1 day ago"),
            HistoryItem("Workflow A", "Failure", "2 days ago"),
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.history_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = HistoryAdapter(historyItems)

        return view
    }
}
