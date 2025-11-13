package com.gws.auto.mobile.android.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ListItemSettingBinding

class SettingsAdapter(private val items: List<SettingsItem>) : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemSettingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(private val binding: ListItemSettingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SettingsItem) {
            binding.settingTitle.text = item.title
            binding.root.setOnClickListener { item.onClick() }
        }
    }
}
