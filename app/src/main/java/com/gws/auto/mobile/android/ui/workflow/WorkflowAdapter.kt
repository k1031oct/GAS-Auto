package com.gws.auto.mobile.android.ui.workflow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.domain.model.Workflow

class WorkflowAdapter(private val workflows: List<Workflow>) : RecyclerView.Adapter<WorkflowAdapter.WorkflowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkflowViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workflow, parent, false)
        return WorkflowViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkflowViewHolder, position: Int) {
        val workflow = workflows[position]
        holder.workflowName.text = workflow.name
        holder.workflowDescription.text = workflow.description
    }

    override fun getItemCount() = workflows.size

    class WorkflowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workflowName: TextView = itemView.findViewById(R.id.workflow_name)
        val workflowDescription: TextView = itemView.findViewById(R.id.workflow_description)
    }
}
