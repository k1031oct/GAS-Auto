package com.gws.auto.mobile.android.ui.schedule

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.gws.auto.mobile.android.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar

class ScheduleFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private val holidays = mutableMapOf<Int, MutableList<String>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        fetchHolidays()

        return view
    }

    private fun fetchHolidays() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val country = prefs.getString("country", "US")
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val url = URL("https://date.nager.at/api/v3/PublicHolidays/$year/$country")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                val jsonArray = JSONArray(response)
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

        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

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

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        var firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // Sunday is 0
        if (firstDayOfWeekPref == "Monday") {
            firstDayOfWeek = (firstDayOfWeek + 6) % 7 // Monday is 0
        }

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

            if (holidays.containsKey(i)) {
                for (holiday in holidays[i]!!) {
                    textView.append("\n($holiday)")
                }
            }

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated called")
    }
}
