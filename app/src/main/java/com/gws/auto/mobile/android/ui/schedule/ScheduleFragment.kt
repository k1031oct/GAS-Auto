package com.gws.auto.mobile.android.ui.schedule

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.data.model.Schedule
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private val holidays = mutableMapOf<Int, MutableList<String>>()
    private var schedules: List<Schedule> = emptyList()
    private val viewModel: ScheduleViewModel by viewModels()
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        fetchHolidays()

        view.findViewById<FloatingActionButton>(R.id.fab_add_schedule).setOnClickListener {
            startActivity(Intent(activity, ScheduleSettingsActivity::class.java))
        }

        view.findViewById<Button>(R.id.prev_month_button).setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            fetchHolidays()
        }

        view.findViewById<Button>(R.id.next_month_button).setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            fetchHolidays()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated called")

        viewModel.schedules.observe(viewLifecycleOwner) { schedules ->
            this.schedules = schedules
            setupCalendar() // Re-render calendar when schedules change
        }
    }

    private fun fetchHolidays() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val country = prefs.getString("country", "US")
                val year = calendar.get(Calendar.YEAR)
                val url = URL("https://date.nager.at/api/v3/PublicHolidays/$year/$country")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                val jsonArray = JSONArray(response)
                holidays.clear()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val date = jsonObject.getString("date")
                    val name = jsonObject.getString("localName")
                    val day = date.substring(8, 10).toInt()
                    if (!holidays.containsKey(day)) {
                        holidays[day] = mutableListOf()
                    }
                    holidays[day]?.add(name)
                }

                withContext(Dispatchers.Main) {
                    setupCalendar()
                }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching holidays")
            }
        }
    }

    private fun setupCalendar() {
        val view = view ?: return
        val calendarGrid = view.findViewById<GridLayout>(R.id.calendar_grid)
        calendarGrid.removeAllViews()

        val monthYearTextView = view.findViewById<TextView>(R.id.month_year_text_view)
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthYearTextView.text = sdf.format(calendar.time)

        val firstDayOfWeekPref = prefs.getString("first_day_of_week", "Sunday")
        val daysOfWeek = if (firstDayOfWeekPref == "Sunday") {
            arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        } else {
            arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        }

        // Add day of week headers
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

        val tempCalendar = calendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        var firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1 // Sunday is 0
        if (firstDayOfWeekPref == "Monday") {
            firstDayOfWeek = (firstDayOfWeek + 6) % 7 // Monday is 0
        }

        val daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        val today = Calendar.getInstance()
        val isCurrentMonth = calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                             calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)

        for (i in 0 until firstDayOfWeek) {
            val textView = TextView(context)
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = 100
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            textView.layoutParams = params
            calendarGrid.addView(textView)
        }

        for (day in 1..daysInMonth) {
            val textView = TextView(context)
            textView.text = day.toString()
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

            if (isCurrentMonth && day == today.get(Calendar.DAY_OF_MONTH)) {
                textView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlight_color))
            }

            if (holidays.containsKey(day)) {
                for (holiday in holidays[day]!!) {
                    textView.append("\n($holiday)")
                }
            }

            schedules.forEach { schedule ->
                if (schedule.monthlyDays?.contains(day) == true) {
                     textView.append("\n(Schedule)")
                }
            }

            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = 200
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            textView.layoutParams = params
            calendarGrid.addView(textView)
        }
    }
}
