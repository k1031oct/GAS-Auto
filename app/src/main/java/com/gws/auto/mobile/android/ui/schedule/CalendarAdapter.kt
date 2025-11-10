package com.gws.auto.mobile.android.ui.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.ListItemCalendarDayBinding
import com.gws.auto.mobile.android.domain.model.Holiday
import java.time.LocalDate

class CalendarAdapter(
    private val onDateClick: (LocalDate) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private val items = mutableListOf<CalendarItem>()
    private var holidays = listOf<Holiday>()

    fun updateData(newItems: List<CalendarItem>, newHolidays: List<Holiday>) {
        items.clear()
        items.addAll(newItems)
        holidays = newHolidays
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ListItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CalendarViewHolder(private val binding: ListItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CalendarItem) {
            binding.dayText.text = item.day

            // Reset styles before applying new ones
            binding.dayText.background = null
            // This will make the text color follow the theme attribute set in XML
            binding.dayText.setTextAppearance(android.R.style.TextAppearance_Material_Body1)


            if (item.date != null) {
                if (item.date == LocalDate.now()) {
                    binding.dayText.setBackgroundResource(R.drawable.bg_today)
                }

                val holiday = holidays.find { it.date == item.date }
                if (holiday != null) {
                    binding.dayText.text = "${item.day}\n${holiday.name}"
                    binding.dayText.setTextColor(ContextCompat.getColor(itemView.context, R.color.holiday_color))
                }

                itemView.setOnClickListener { onDateClick(item.date) }
            } else {
                // This is a placeholder or header, make it non-clickable
                itemView.setOnClickListener(null)
            }
        }
    }
}

data class CalendarItem(
    val day: String,
    val date: LocalDate? // Null for empty placeholders
)
