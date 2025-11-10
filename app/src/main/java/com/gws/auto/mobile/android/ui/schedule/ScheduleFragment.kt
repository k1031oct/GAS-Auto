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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.data.model.Schedule
import com.gws.auto.mobile.android.databinding.FragmentScheduleBinding
import com.gws.auto.mobile.android.domain.model.Holiday
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ScheduleViewModel by viewModels()

    @Inject
    lateinit var prefs: SharedPreferences

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var scheduleListAdapter: ScheduleListAdapter
    private lateinit var gestureDetector: GestureDetector

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGestureDetector()
        setupCalendarRecyclerView()
        setupScheduleRecyclerView()
        setupViews()
        observeViewModel()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupCalendarRecyclerView() {
        calendarAdapter = CalendarAdapter { date ->
            showTimelineForDate(date)
        }
        binding.calendarRecyclerView.layoutManager = GridLayoutManager(context, 7)
        binding.calendarRecyclerView.adapter = calendarAdapter
        // Set the touch listener to enable swipe gestures
        binding.calendarRecyclerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            // Return false so that the RecyclerView can still handle clicks on items
            false
        }
    }

    private fun setupScheduleRecyclerView() {
        scheduleListAdapter = ScheduleListAdapter { schedule ->
            Timber.d("Schedule clicked: ${schedule.workflowId}")
        }
        binding.scheduleListRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.scheduleListRecyclerView.adapter = scheduleListAdapter
    }

    private fun setupViews() {
        binding.fabAddSchedule.setOnClickListener {
            startActivity(Intent(activity, ScheduleSettingsActivity::class.java))
        }

        binding.prevMonthButton.setOnClickListener {
            viewModel.moveToPreviousMonth()
            showAllSchedulesList()
        }

        binding.nextMonthButton.setOnClickListener {
            viewModel.moveToNextMonth()
            showAllSchedulesList()
        }
    }

    private fun observeViewModel() {
        viewModel.currentDate
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { date ->
                updateCalendar(date, viewModel.holidays.value)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.holidays
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { holidays ->
                updateCalendar(viewModel.currentDate.value, holidays)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.schedules
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { schedules ->
                scheduleListAdapter.submitList(schedules)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun updateCalendar(date: LocalDate, holidays: List<Holiday>) {
        binding.monthYearTextView.text = date.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))

        val calendarItems = mutableListOf<CalendarItem>()
        val yearMonth = YearMonth.from(date)
        val firstDayOfMonth = date.withDayOfMonth(1)
        val daysInMonth = yearMonth.lengthOfMonth()

        val startDayPref = prefs.getString("first_day_of_week", "Sunday")
        val isSundayFirst = startDayPref == "Sunday"

        val daysOfWeek = if (isSundayFirst) {
            listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)
        } else {
            listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        }
        daysOfWeek.forEach {
            calendarItems.add(CalendarItem(it.getDisplayName(TextStyle.SHORT, Locale.getDefault()), null))
        }

        val firstDayOfWeek = firstDayOfMonth.dayOfWeek
        val startOffset = if (isSundayFirst) {
            firstDayOfWeek.value % 7
        } else {
            if (firstDayOfWeek == DayOfWeek.SUNDAY) 6 else firstDayOfWeek.value - 1
        }

        for (i in 0 until startOffset) {
            calendarItems.add(CalendarItem("", null))
        }

        for (day in 1..daysInMonth) {
            calendarItems.add(CalendarItem(day.toString(), date.withDayOfMonth(day)))
        }

        calendarAdapter.updateData(calendarItems, holidays)
    }

    private fun showTimelineForDate(date: LocalDate) {
        val formattedDate = date.format(DateTimeFormatter.ofLocalizedDate(java.time.format.FormatStyle.MEDIUM))
        binding.listHeaderText.text = getString(R.string.timeline_for_date, formattedDate)
        binding.scheduleListRecyclerView.visibility = View.GONE
        binding.timelineRecyclerView.visibility = View.VISIBLE

        val holidaysForDay = viewModel.holidays.value.filter { it.date == date }
        val schedulesForDay = viewModel.schedules.value.filter {
            when (it.scheduleType) {
                "daily" -> true
                "weekly" -> it.weeklyDays?.contains(date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())) == true
                "monthly" -> it.monthlyDays?.contains(date.dayOfMonth) == true
                else -> false
            }
        }

        val timelineItems = (holidaysForDay + schedulesForDay).sortedBy {
            when (it) {
                is Schedule -> LocalTime.parse(it.time ?: "00:00")
                is Holiday -> LocalTime.MIN
                else -> LocalTime.MAX
            }
        }
        binding.timelineRecyclerView.adapter = TimelineAdapter(timelineItems)
    }

    private fun showAllSchedulesList() {
        binding.listHeaderText.text = getString(R.string.all_schedules_title)
        binding.timelineRecyclerView.visibility = View.GONE
        binding.scheduleListRecyclerView.visibility = View.VISIBLE
    }

    private fun setupGestureDetector() {
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            private val swipeThreshold = 100
            private val swipeVelocityThreshold = 100

            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 == null) return false
                val diffX = e2.x - e1.x
                if (kotlin.math.abs(diffX) > swipeThreshold && kotlin.math.abs(velocityX) > swipeVelocityThreshold) {
                    if (diffX > 0) {
                        viewModel.moveToPreviousMonth()
                    } else {
                        viewModel.moveToNextMonth()
                    }
                    showAllSchedulesList()
                    return true
                }
                return false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
