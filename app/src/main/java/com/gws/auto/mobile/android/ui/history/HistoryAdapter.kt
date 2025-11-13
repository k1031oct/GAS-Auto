package com.gws.auto.mobile.android.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.ListItemHistoryBinding
import com.gws.auto.mobile.android.databinding.ListItemHistoryLogBinding
import com.gws.auto.mobile.android.domain.model.History
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(
    private val onHeaderClick: (HistoryListItem.HeaderItem, Int) -> Unit
) : ListAdapter<HistoryListItem, RecyclerView.ViewHolder>(HistoryDiffCallback()) {

    private val VIEW_TYPE_HEADER = 1
    private val VIEW_TYPE_LOG = 2

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HistoryListItem.HeaderItem -> VIEW_TYPE_HEADER
            is HistoryListItem.LogItem -> VIEW_TYPE_LOG
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ListItemHistoryBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding, onHeaderClick)
            }
            else -> {
                val binding = ListItemHistoryLogBinding.inflate(inflater, parent, false)
                LogViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HistoryListItem.HeaderItem -> (holder as HeaderViewHolder).bind(item)
            is HistoryListItem.LogItem -> (holder as LogViewHolder).bind(item)
        }
    }

    class HeaderViewHolder(
        private val binding: ListItemHistoryBinding,
        private val onHeaderClick: (HistoryListItem.HeaderItem, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        fun bind(headerItem: HistoryListItem.HeaderItem) {
            val context = binding.root.context
            val history = headerItem.history
            binding.workflowNameText.text = history.workflowName
            binding.timestampText.text = dateFormat.format(history.executedAt)

            val (statusText, chipColorRes) = when (history.status) {
                "Success" -> context.getString(R.string.execution_status_success) to R.color.chip_success_color
                "Failure" -> context.getString(R.string.execution_status_failure) to R.color.chip_failure_color
                else -> history.status to R.color.chip_default_color
            }
            binding.statusChip.text = statusText
            binding.statusChip.chipBackgroundColor = ContextCompat.getColorStateList(context, chipColorRes)

            itemView.setOnClickListener {
                onHeaderClick(headerItem, adapterPosition)
            }
        }
    }

    class LogViewHolder(private val binding: ListItemHistoryLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(logItem: HistoryListItem.LogItem) {
            binding.logText.text = logItem.log
        }
    }
}

class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryListItem>() {
    override fun areItemsTheSame(oldItem: HistoryListItem, newItem: HistoryListItem): Boolean {
        return when {
            oldItem is HistoryListItem.HeaderItem && newItem is HistoryListItem.HeaderItem -> oldItem.history.id == newItem.history.id
            oldItem is HistoryListItem.LogItem && newItem is HistoryListItem.LogItem -> oldItem.log == newItem.log // Not ideal, but works for simple logs
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: HistoryListItem, newItem: HistoryListItem): Boolean {
        return oldItem == newItem
    }
}
