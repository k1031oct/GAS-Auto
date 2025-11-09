package com.gws.auto.mobile.android.ui.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.data.model.Schedule
import com.gws.auto.mobile.android.databinding.ListItemTimelineBinding

class TimelineAdapter(private val schedules: List<Schedule>) : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val binding = ListItemTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.bind(schedules[position])
    }

    override fun getItemCount() = schedules.size

    inner class TimelineViewHolder(private val binding: ListItemTimelineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(schedule: Schedule) {
            binding.timeText.text = schedule.time
            // TODO: Get workflow name from workflowId
            binding.scheduleTitleText.text = "Workflow ID: ${schedule.workflowId}"
        }
    }
}
