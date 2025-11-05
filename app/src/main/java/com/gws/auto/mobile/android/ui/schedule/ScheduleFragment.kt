package com.gws.auto.mobile.android.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gws.auto.mobile.android.R
import timber.log.Timber
import java.util.Calendar

class ScheduleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        val calendarGrid = view.findViewById<GridLayout>(R.id.calendar_grid)

        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        // Add day of week headers
        val daysOfWeek = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        for (day in daysOfWeek) {
            val textView = TextView(context)
            textView.text = day
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            textView.layoutParams = params
            calendarGrid.addView(textView)
        }

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        for (i in 0 until firstDayOfWeek) {
            val textView = TextView(context)
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = 100
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            textView.layoutParams = params
            calendarGrid.addView(textView)
        }

        for (i in 1..daysInMonth) {
            val textView = TextView(context)
            textView.text = i.toString()
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

            if (i == 10 || i == 22) {
                textView.append("\n(Schedule)")
            }

            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = 200
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            textView.layoutParams = params
            calendarGrid.addView(textView)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated called")
    }
}
