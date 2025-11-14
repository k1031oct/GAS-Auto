package com.gws.auto.mobile.android.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ListItemAddTagBinding
import com.gws.auto.mobile.android.databinding.ListItemTagBinding
import com.gws.auto.mobile.android.domain.model.Tag

class TagAdapter(
    private val onTagClicked: (Tag) -> Unit,
    private val onTagLongClicked: (Tag) -> Unit,
    private val onAddTagClicked: () -> Unit
) : ListAdapter<Tag, RecyclerView.ViewHolder>(TagDiffCallback()) {

    private val VIEW_TYPE_TAG = 0
    private val VIEW_TYPE_ADD = 1

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) VIEW_TYPE_ADD else VIEW_TYPE_TAG
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_TAG) {
            val binding = ListItemTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            TagViewHolder(binding, onTagClicked, onTagLongClicked)
        } else {
            val binding = ListItemAddTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            AddTagViewHolder(binding, onAddTagClicked)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TagViewHolder) {
            holder.bind(getItem(position))
        }
    }

    class TagViewHolder(
        private val binding: ListItemTagBinding,
        private val onTagClicked: (Tag) -> Unit,
        private val onTagLongClicked: (Tag) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: Tag) {
            binding.tagName.text = tag.name
            binding.root.setOnClickListener { onTagClicked(tag) }
            binding.root.setOnLongClickListener {
                onTagLongClicked(tag)
                true
            }
        }
    }

    class AddTagViewHolder(
        private val binding: ListItemAddTagBinding,
        private val onAddTagClicked: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { onAddTagClicked() }
        }
    }
}

class TagDiffCallback : DiffUtil.ItemCallback<Tag>() {
    override fun areItemsTheSame(oldItem: Tag, newItem: Tag): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Tag, newItem: Tag): Boolean {
        return oldItem == newItem
    }
}
