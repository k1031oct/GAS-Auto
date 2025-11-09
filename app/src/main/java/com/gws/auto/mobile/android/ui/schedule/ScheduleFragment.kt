package com.gws.auto.mobile.android.ui.schedule

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.api.services.calendar.model.Event
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.data.model.Schedule
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private val holidays = mutableMapOf<Int, MutableList<String>>()
    private var schedules: List<Schedule> = emptyList()
    private var calendarEvents: List<Event> = emptyList()
    private val viewModel: ScheduleViewModel by viewModels()
    private val calendar = Calendar.getInstance()
    private var _binding: View? = null
    private lateinit var gestureDetector: GestureDetector
    private lateinit var allSchedulesAdapter: ScheduleListAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        _binding = view

        fetchAllData()

        view.findViewById<FloatingActionButton>(R.id.fab_add_schedule).setOnClickListener {
            startActivity(Intent(activity, ScheduleSettingsActivity::class.java))
        }

        view.findViewById<Button>(R.id.prev_month_button).setOnClickListener {
            previousMonth()
        }

        view.findViewById<Button>(R.id.next_month_button).setOnClickListener {
            nextMonth()
        }

        setupGestureDetector(view)

        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGestureDetector(view: View) {
        gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onFling(
                e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float
            ): Boolean {
                if (e1 == null) return false
                val diffX = e2.x - e1.x
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) previousMonth() else nextMonth()
                    return true
                }
                return false
            }
        })
        view.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
    }

    private fun previousMonth() {
        calendar.add(Calendar.MONTH, -1)
        fetchAllData()
        hideTimelineAndShowAllSchedules()
    }

    private fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
        fetchAllData()
        hideTimelineAndShowAllSchedules()
    }

    private fun fetchAllData() {
        lifecycleScope.launch {
            fetchHolidays()
            viewModel.fetchCalendarEvents(calendar)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated called")

        val timelineRecyclerView = view.findViewById<RecyclerView>(R.id.timeline_recycler_view)
        timelineRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val allSchedulesRecyclerView = view.findViewById<RecyclerView>(R.id.all_schedules_recycler_view)
        allSchedulesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        allSchedulesAdapter = ScheduleListAdapter { schedule ->
            // TODO: Implement click action for all schedules list
            Timber.d("Clicked schedule: ${schedule.workflowId}")
        }
        allSchedulesRecyclerView.adapter = allSchedulesAdapter

        // Combine observers to reduce redundant UI updates
        viewModel.schedules.observe(viewLifecycleOwner) { newSchedules ->
            schedules = newSchedules
            allSchedulesAdapter.submitList(newSchedules)
            setupCalendar()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.calendarEvents.collect { newEvents ->
                    calendarEvents = newEvents
                    setupCalendar()
                }
            }
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
            } catch (e: Exception) {
                Timber.e(e, "Error fetching holidays")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupCalendar() {
        val view = _binding ?: return
        val calendarGrid = view.findViewById<GridLayout>(R.id.calendar_grid)
        calendarGrid.removeAllViews()

        val timelineRecyclerView = view.findViewById<RecyclerView>(R.id.timeline_recycler_view)
        val allSchedulesRecyclerView = view.findViewById<RecyclerView>(R.id.all_schedules_recycler_view)

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
        daysOfWeek.forEachIndexed { index, day ->
            val textView = TextView(requireContext()).apply {
                text = day
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    columnSpec = GridLayout.spec(index, 1f)
                }
            }
            calendarGrid.addView(textView)
        }

        val tempCalendar = calendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        var firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK)
        if (firstDayOfWeekPref == "Monday") {
            firstDayOfWeek = if (firstDayOfWeek == Calendar.SUNDAY) 7 else firstDayOfWeek - 1
        }
        val startOffset = firstDayOfWeek - 1

        val daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val today = Calendar.getInstance()
        val isCurrentMonth = calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)

        // Add empty cells
        repeat(startOffset) {
            val textView = TextView(requireContext()).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 200
                    columnSpec = GridLayout.spec(it, 1f)
                }
            }
            calendarGrid.addView(textView)
        }

        for (day in 1..daysInMonth) {
            val textView = TextView(requireContext()).apply {
                text = day.toString()
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 300
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                }
            }

            if (isCurrentMonth && day == today.get(Calendar.DAY_OF_MONTH)) {
                textView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlight_color))
            }

            holidays[day]?.forEach { holiday ->
                textView.append("\n($holiday)")
            }

            val eventsForDay = calendarEvents.filter {
                val eventCal = Calendar.getInstance()
                val eventDate = it.start?.dateTime ?: it.start?.date
                eventDate?.let { date -> eventCal.timeInMillis = date.value }
                eventCal.get(Calendar.DAY_OF_MONTH) == day
            }
            eventsForDay.forEach { event ->
                textView.append("\n- ${event.summary}")
            }

            val schedulesForDay = schedules.filter {
                it.monthlyDays?.contains(day) == true || it.scheduleType == "daily"
            }
            if (schedulesForDay.isNotEmpty()) {
                textView.append("\n(Schedule)")
            }

            val allItemsForDay = (schedulesForDay + eventsForDay).sortedBy { item ->
                when (item) {
                    is Schedule -> LocalTime.parse(item.time ?: "00:00")
                    is Event -> {
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = (item.start?.dateTime ?: item.start?.date)?.value ?: 0
                        LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
                    }
                    else -> LocalTime.MAX
                }
            }

            textView.setOnClickListener {
                timelineRecyclerView.adapter = TimelineAdapter(allItemsForDay)
                timelineRecyclerView.isVisible = allItemsForDay.isNotEmpty()
                allSchedulesRecyclerView.isVisible = !timelineRecyclerView.isVisible
            }

            calendarGrid.addView(textView)
        }

        if (!timelineRecyclerView.isVisible) {
            allSchedulesRecyclerView.isVisible = true
        }
    }

    private fun hideTimelineAndShowAllSchedules() {
        val view = _binding ?: return
        view.findViewById<RecyclerView>(R.id.timeline_recycler_view).isVisible = false
        view.findViewById<RecyclerView>(R.id.all_schedules_recycler_view).isVisible = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}