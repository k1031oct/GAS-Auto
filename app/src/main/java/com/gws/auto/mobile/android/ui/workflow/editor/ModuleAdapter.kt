package com.gws.auto.mobile.android.ui.workflow.editor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.databinding.ListItemModuleBinding
import com.gws.auto.mobile.android.domain.model.Module
import java.util.Collections

class ModuleAdapter(
    private val modules: MutableList<Module>,
    private val onEditClicked: (Module) -> Unit,
    private val onRemoveClicked: (Int) -> Unit
) : RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder>(), ModuleTouchHelperCallback.ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding = ListItemModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val module = modules[position]
        holder.bind(module)
        holder.itemView.setOnClickListener { onEditClicked(module) }
        holder.binding.deleteButton.setOnClickListener { onRemoveClicked(position) }
    }

    override fun getItemCount(): Int = modules.size

    fun getModules(): List<Module> = modules

    fun updateModules(newModules: List<Module>) {
        modules.clear()
        modules.addAll(newModules)
        notifyDataSetChanged()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(modules, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(modules, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    class ModuleViewHolder(val binding: ListItemModuleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(module: Module) {
            binding.moduleName.text = module.type
        }
    }
}
