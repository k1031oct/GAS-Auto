package com.gws.auto.mobile.android.ui.workflow

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.ListItemWorkflowBinding
import com.gws.auto.mobile.android.domain.model.Workflow

class WorkflowAdapter(private val workflows: List<Workflow>) : RecyclerView.Adapter<WorkflowAdapter.WorkflowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkflowViewHolder {
        val binding = ListItemWorkflowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkflowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkflowViewHolder, position: Int) {
        val workflow = workflows[position]
        holder.bind(workflow)
    }

    override fun getItemCount() = workflows.size

    class WorkflowViewHolder(private val binding: ListItemWorkflowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(workflow: Workflow) {
            val context = itemView.context
            binding.workflowNameText.text = workflow.name

            // TODO: Replace with actual data
            binding.triggerIcon.setImageResource(R.drawable.ic_schedule)
            binding.triggerText.text = context.getString(R.string.dummy_trigger_daily)
            binding.statusChip.text = context.getString(R.string.dummy_status_active)

            binding.runButton.setOnClickListener {
                Toast.makeText(context, "Run: ${workflow.name}", Toast.LENGTH_SHORT).show()
            }

            binding.menuButton.setOnClickListener {
                Toast.makeText(context, "Menu for: ${workflow.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
