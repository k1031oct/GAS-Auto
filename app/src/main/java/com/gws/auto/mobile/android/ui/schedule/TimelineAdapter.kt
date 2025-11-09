package com.gws.auto.mobile.android.ui.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.api.services.calendar.model.Event
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.data.model.Schedule
import java.text.SimpleDateFormat
import java.util.Locale

class TimelineAdapter(private val items: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_SCHEDULE = 0
        private const val TYPE_EVENT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Schedule -> TYPE_SCHEDULE
            is Event -> TYPE_EVENT
            else -> throw IllegalArgumentException("Invalid type of data at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SCHEDULE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_timeline, parent, false)
                ScheduleViewHolder(view)
            }
            TYPE_EVENT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_timeline_event, parent, false)
                EventViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ScheduleViewHolder -> holder.bind(items[position] as Schedule)
            is EventViewHolder -> holder.bind(items[position] as Event)
        }
    }

    override fun getItemCount() = items.size

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeText: TextView = itemView.findViewById(R.id.time_text)
        private val titleText: TextView = itemView.findViewById(R.id.schedule_title_text)

        fun bind(schedule: Schedule) {
            timeText.text = schedule.time
            // TODO: Get workflow name from workflowId
            titleText.text = "Workflow ID: ${schedule.workflowId}"
        }
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeText: TextView = itemView.findViewById(R.id.event_time_text)
        private val titleText: TextView = itemView.findViewById(R.id.event_title_text)
        private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())


        fun bind(event: Event) {
            titleText.text = event.summary
            val start = event.start?.dateTime
            if (start != null) {
                timeText.text = timeFormat.format(start.value)
            } else {
                timeText.text = "All day"
            }
        }
    }
}
