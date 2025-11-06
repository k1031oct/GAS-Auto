package com.gws.auto.mobile.android.ui.workflow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.domain.model.Workflow

class WorkflowAdapter(private val workflowList: List<Workflow>) : RecyclerView.Adapter<WorkflowAdapter.WorkflowViewHolder>() {

    class WorkflowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workflowName: TextView = itemView.findViewById(R.id.workflow_name)
        val status: TextView = itemView.findViewById(R.id.status)
        val trigger: TextView = itemView.findViewById(R.id.trigger)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkflowViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_workflow_table, parent, false)
        return WorkflowViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkflowViewHolder, position: Int) {
        val currentItem = workflowList[position]
        holder.workflowName.text = currentItem.name
        holder.status.text = currentItem.status
        holder.trigger.text = currentItem.trigger
    }

    override fun getItemCount() = workflowList.size
}