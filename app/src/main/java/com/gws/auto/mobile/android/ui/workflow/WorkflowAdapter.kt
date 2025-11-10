package com.gws.auto.mobile.android.ui.workflow

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ListItemWorkflowBinding
import com.gws.auto.mobile.android.domain.model.Workflow

class WorkflowAdapter(
    private var workflows: List<Workflow>,
    private val onRunClicked: (Workflow) -> Unit,
    private val onDeleteClicked: (Workflow) -> Unit
) : RecyclerView.Adapter<WorkflowAdapter.WorkflowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkflowViewHolder {
        val binding = ListItemWorkflowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WorkflowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkflowViewHolder, position: Int) {
        holder.bind(workflows[position])
    }

    override fun getItemCount() = workflows.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateWorkflows(newWorkflows: List<Workflow>) {
        workflows = newWorkflows
        notifyDataSetChanged() // A more efficient diffing mechanism can be used later.
    }

    inner class WorkflowViewHolder(private val binding: ListItemWorkflowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(workflow: Workflow) {
            binding.workflowName.text = workflow.name
            binding.workflowDescription.text = workflow.description
            binding.workflowStatus.text = workflow.status
            binding.workflowTrigger.text = workflow.trigger

            binding.runButton.setOnClickListener {
                onRunClicked(workflow)
            }

            binding.deleteButton.setOnClickListener {
                onDeleteClicked(workflow)
            }
        }
    }
}
