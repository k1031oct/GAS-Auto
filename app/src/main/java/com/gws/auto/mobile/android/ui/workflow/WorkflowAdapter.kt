package com.gws.auto.mobile.android.ui.workflow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ListItemWorkflowBinding
import com.gws.auto.mobile.android.domain.engine.WorkflowEngine
import com.gws.auto.mobile.android.domain.model.Workflow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkflowAdapter(
    private var workflows: List<Workflow>,
    private val workflowEngine: WorkflowEngine
) : RecyclerView.Adapter<WorkflowAdapter.WorkflowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkflowViewHolder {
        val binding = ListItemWorkflowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkflowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkflowViewHolder, position: Int) {
        holder.bind(workflows[position])
    }

    override fun getItemCount() = workflows.size

    inner class WorkflowViewHolder(private val binding: ListItemWorkflowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(workflow: Workflow) {
            binding.workflowNameText.text = workflow.name
            binding.triggerText.text = workflow.trigger
            binding.statusChip.text = workflow.status

            binding.runButton.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    workflowEngine.execute(workflow.modules)
                }
            }
        }
    }
}
