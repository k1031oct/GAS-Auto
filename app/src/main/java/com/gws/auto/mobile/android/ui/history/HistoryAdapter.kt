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
            val context = binding.root.context
            binding.workflowNameText.text = log.workflowName
            binding.timestampText.text = dateFormat.format(log.executionTime)

            val statusText: String
            val chipColorRes: Int

            when (log.status) {
                "Success" -> {
                    statusText = context.getString(R.string.execution_status_success)
                    chipColorRes = R.color.chip_success_color
                }
                "Failure" -> {
                    statusText = context.getString(R.string.execution_status_failure)
                    chipColorRes = R.color.chip_failure_color
                }
                else -> {
                    statusText = log.status
                    chipColorRes = R.color.chip_default_color
                }
            }
            binding.statusChip.text = statusText
            binding.statusChip.chipBackgroundColor = ContextCompat.getColorStateList(context, chipColorRes)
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
