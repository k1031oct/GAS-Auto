package com.gws.auto.mobile.android.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.domain.model.HistoryItem

class HistoryAdapter(private val historyItems: List<HistoryItem>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val workflowName: TextView = view.findViewById(R.id.workflow_name)
        val status: TextView = view.findViewById(R.id.status)
        val timestamp: TextView = view.findViewById(R.id.timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = historyItems[position]
        holder.workflowName.text = item.workflowName
        holder.status.text = item.status
        holder.timestamp.text = item.timestamp
    }

    override fun getItemCount() = historyItems.size
}
