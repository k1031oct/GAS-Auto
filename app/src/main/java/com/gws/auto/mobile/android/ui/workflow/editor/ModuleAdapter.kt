package com.gws.auto.mobile.android.ui.workflow.editor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.domain.model.Module

class ModuleAdapter(private val modules: MutableList<Module>) : RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_module, parent, false)
        return ModuleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val module = modules[position]
        holder.moduleType.text = module.type
    }

    override fun getItemCount() = modules.size

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        val movedModule = modules.removeAt(fromPosition)
        modules.add(toPosition, movedModule)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun onItemDismiss(position: Int) {
        modules.removeAt(position)
        notifyItemRemoved(position)
    }

    class ModuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val moduleType: TextView = itemView.findViewById(R.id.module_type)
    }
}
