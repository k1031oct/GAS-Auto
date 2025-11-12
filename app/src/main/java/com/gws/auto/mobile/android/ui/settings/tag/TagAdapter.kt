package com.gws.auto.mobile.android.ui.settings.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ListItemTagBinding
import com.gws.auto.mobile.android.domain.model.Tag

class TagAdapter(
    private val onDeleteClicked: (Tag) -> Unit
) : ListAdapter<Tag, TagAdapter.TagViewHolder>(TagDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding = ListItemTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagViewHolder(binding, onDeleteClicked)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TagViewHolder(
        private val binding: ListItemTagBinding,
        private val onDeleteClicked: (Tag) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: Tag) {
            binding.tagNameText.text = tag.name
            binding.deleteTagButton.setOnClickListener { onDeleteClicked(tag) }
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
