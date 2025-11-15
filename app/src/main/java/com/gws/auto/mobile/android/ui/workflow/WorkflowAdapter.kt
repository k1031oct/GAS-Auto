package com.gws.auto.mobile.android.ui.workflow

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ListItemAddWorkflowBinding
import com.gws.auto.mobile.android.databinding.ListItemWorkflowBinding
import com.gws.auto.mobile.android.domain.model.Workflow

class WorkflowAdapter(
    private val onRunClicked: (Workflow) -> Unit,
    private val onEditClicked: (Workflow) -> Unit,
    private val onDeleteClicked: (Workflow) -> Unit,
    private val onAddClicked: () -> Unit,
    private val onFavoriteClicked: (Workflow) -> Unit
) : ListAdapter<Workflow, RecyclerView.ViewHolder>(WorkflowDiffCallback()) {

    private val VIEW_TYPE_WORKFLOW = 1
    private val VIEW_TYPE_ADD = 2

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.setHasFixedSize(true)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) VIEW_TYPE_WORKFLOW else VIEW_TYPE_ADD
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1 // Add 1 for the "Add New" button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_WORKFLOW) {
            val binding = ListItemWorkflowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            WorkflowViewHolder(binding, onRunClicked, onEditClicked, onDeleteClicked, onFavoriteClicked)
        } else {
            val binding = ListItemAddWorkflowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            AddWorkflowViewHolder(binding, onAddClicked)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is WorkflowViewHolder) {
            holder.bind(getItem(position))
        }
    }

    class WorkflowViewHolder(
        private val binding: ListItemWorkflowBinding,
        private val onRunClicked: (Workflow) -> Unit,
        private val onEditClicked: (Workflow) -> Unit,
        private val onDeleteClicked: (Workflow) -> Unit,
        private val onFavoriteClicked: (Workflow) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(workflow: Workflow) {
            binding.workflowName.text = workflow.name
            binding.workflowDescription.text = workflow.description
            binding.workflowStatus.text = workflow.status
            binding.workflowTrigger.text = workflow.trigger

            binding.favoriteButton.isChecked = workflow.isFavorite

            binding.runButton.setOnClickListener { onRunClicked(workflow) }
            binding.editButton.setOnClickListener { onEditClicked(workflow) }
            binding.deleteButton.setOnClickListener { onDeleteClicked(workflow) }
            binding.favoriteButton.setOnClickListener { onFavoriteClicked(workflow) }
        }
    }

    class AddWorkflowViewHolder(
        binding: ListItemAddWorkflowBinding,
        private val onAddClicked: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener { onAddClicked() }
        }
    }
}

class WorkflowDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Workflow>() {
    override fun areItemsTheSame(oldItem: Workflow, newItem: Workflow): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Workflow, newItem: Workflow): Boolean {
        return oldItem == newItem
    }
}
