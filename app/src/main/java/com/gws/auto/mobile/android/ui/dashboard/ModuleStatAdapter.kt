package com.gws.auto.mobile.android.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ListItemModuleStatBinding

class ModuleStatAdapter : ListAdapter<ModuleStat, ModuleStatAdapter.ViewHolder>(ModuleStatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemModuleStatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ListItemModuleStatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(stat: ModuleStat) {
            binding.moduleNameText.text = stat.moduleName
            binding.usageCountText.text = "Used: ${stat.usageCount} times"
            binding.errorCountText.text = "Errors: ${stat.errorCount}"
        }
    }
}

class ModuleStatDiffCallback : DiffUtil.ItemCallback<ModuleStat>() {
    override fun areItemsTheSame(oldItem: ModuleStat, newItem: ModuleStat): Boolean {
        return oldItem.moduleName == newItem.moduleName
    }

    override fun areContentsTheSame(oldItem: ModuleStat, newItem: ModuleStat): Boolean {
        return oldItem == newItem
    }
}
