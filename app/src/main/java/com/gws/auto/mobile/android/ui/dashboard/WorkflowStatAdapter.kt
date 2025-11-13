package com.gws.auto.mobile.android.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ListItemWorkflowStatBinding

class WorkflowStatAdapter : ListAdapter<WorkflowExecutionCount, WorkflowStatAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemWorkflowStatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ListItemWorkflowStatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WorkflowExecutionCount) {
            binding.workflowNameText.text = item.workflowName
            binding.executionCountText.text = item.executionCount.toString()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<WorkflowExecutionCount>() {
        override fun areItemsTheSame(oldItem: WorkflowExecutionCount, newItem: WorkflowExecutionCount): Boolean {
            return oldItem.workflowName == newItem.workflowName
        }

        override fun areContentsTheSame(oldItem: WorkflowExecutionCount, newItem: WorkflowExecutionCount): Boolean {
            return oldItem == newItem
        }
    }
}
