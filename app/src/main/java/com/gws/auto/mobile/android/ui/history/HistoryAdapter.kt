package com.gws.auto.mobile.android.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.ListItemHistoryBinding
import com.gws.auto.mobile.android.domain.model.WorkflowExecutionLog
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter : ListAdapter<WorkflowExecutionLog, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ListItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HistoryViewHolder(private val binding: ListItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        fun bind(log: WorkflowExecutionLog) {
            binding.workflowNameText.text = log.workflowName
            binding.timestampText.text = dateFormat.format(log.executionTime)
            binding.statusChip.text = log.status

            val color = when (log.status) {
                "Success" -> R.color.success_color
                "Failure" -> R.color.md_theme_dark_error
                else -> android.R.color.darker_gray
            }
            binding.statusChip.setChipBackgroundColorResource(color)
        }
    }
}

class HistoryDiffCallback : DiffUtil.ItemCallback<WorkflowExecutionLog>() {
    override fun areItemsTheSame(oldItem: WorkflowExecutionLog, newItem: WorkflowExecutionLog): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WorkflowExecutionLog, newItem: WorkflowExecutionLog): Boolean {
        return oldItem == newItem
    }
}
