package com.gws.auto.mobile.android.ui.workflow.editor

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ItemModuleBinding
import com.gws.auto.mobile.android.domain.model.Module

class ModuleAdapter(
    private var modules: MutableList<Module>,
    private val onModuleRemoved: (Int) -> Unit
) : RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding = ItemModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        holder.bind(modules[position])
    }

    override fun getItemCount(): Int = modules.size

    fun getModules(): List<Module> = modules

    fun getModuleAt(position: Int): Module {
        return modules[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateModules(newModules: List<Module>) {
        modules.clear()
        modules.addAll(newModules)
        notifyDataSetChanged() // In a real app, use DiffUtil for better performance
    }

    class ModuleViewHolder(private val binding: ItemModuleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(module: Module) {
            binding.moduleType.text = module.type
        }
    }
}
