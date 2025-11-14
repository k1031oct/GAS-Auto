package com.gws.auto.mobile.android.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ListItemSearchHistoryBinding
import com.gws.auto.mobile.android.domain.model.SearchHistory

class SearchHistoryAdapter(
    private val onHistoryItemClicked: (SearchHistory) -> Unit,
    private val onHistoryItemLongClicked: (SearchHistory) -> Unit
) : ListAdapter<SearchHistory, SearchHistoryAdapter.SearchHistoryViewHolder>(SearchHistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        val binding = ListItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchHistoryViewHolder(binding, onHistoryItemClicked, onHistoryItemLongClicked)
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SearchHistoryViewHolder(
        private val binding: ListItemSearchHistoryBinding,
        private val onHistoryItemClicked: (SearchHistory) -> Unit,
        private val onHistoryItemLongClicked: (SearchHistory) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: SearchHistory) {
            binding.queryText.text = history.query
            binding.root.setOnClickListener { onHistoryItemClicked(history) }
            binding.root.setOnLongClickListener {
                onHistoryItemLongClicked(history)
                true
            }
        }
    }
}

class SearchHistoryDiffCallback : DiffUtil.ItemCallback<SearchHistory>() {
    override fun areItemsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean {
        return oldItem.query == newItem.query
    }

    override fun areContentsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean {
        return oldItem == newItem
    }
}
