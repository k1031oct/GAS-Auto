package com.gws.auto.mobile.android.ui.schedule

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.data.model.Schedule
import com.gws.auto.mobile.android.domain.model.Holiday
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(
    viewModel: ScheduleViewModel
) {
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    val holidays by viewModel.holidays.collectAsState()
    val schedules by viewModel.schedules.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    val currentVisibleMonth = YearMonth.now().plusMonths((pagerState.currentPage - (Int.MAX_VALUE / 2)).toLong())

    // When the viewmodel's date changes (e.g. from button click), scroll the pager
    LaunchedEffect(viewModel.currentDate.collectAsState().value) {
        val targetPage = (Int.MAX_VALUE / 2) + (viewModel.currentDate.value.year * 12 + viewModel.currentDate.value.monthValue) - (YearMonth.now().year * 12 + YearMonth.now().monthValue)
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    BackHandler(enabled = selectedDate != null) {
        selectedDate = null
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { viewModel.moveToPreviousMonth() }) { Text("<") }
            Text(
                text = currentVisibleMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
                style = MaterialTheme.typography.headlineSmall
            )
            Button(onClick = { viewModel.moveToNextMonth() }) { Text(">") }
        }

        // Calendar
        VerticalPager(
            state = pagerState,
            modifier = Modifier.height(350.dp) // Fixed height to ensure list is visible
        ) { page ->
            val month = YearMonth.now().plusMonths((page - (Int.MAX_VALUE / 2)).toLong())
            MonthView(
                yearMonth = month,
                holidays = holidays,
                schedules = schedules,
                onDateClick = { date -> selectedDate = date }
            )
        }

        // List Header
        val listHeader = if (selectedDate != null) {
            stringResource(R.string.timeline_for_date, selectedDate!!.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
        } else {
            stringResource(R.string.all_schedules_title)
        }
        Text(
            text = listHeader,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        // List Area
        Box(modifier = Modifier.weight(1f)) {
            if (selectedDate == null) {
                AllSchedulesList(schedules = schedules)
            } else {
                TimelineList(
                    date = selectedDate!!,
                    holidays = holidays,
                    schedules = schedules
                )
            }
        }
    }
}

@Composable
fun MonthView(
    yearMonth: YearMonth,
    holidays: List<Holiday>,
    schedules: List<Schedule>,
    onDateClick: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val startDayPref = prefs.getString("first_day_of_week", "Sunday")
    val isSundayFirst = startDayPref == "Sunday"

    val daysOfWeek = if (isSundayFirst) {
        listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)
    } else {
        listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
    }

    val firstDayOfMonth = yearMonth.atDay(1)
    val startOffset = if (isSundayFirst) {
        firstDayOfMonth.dayOfWeek.value % 7
    } else {
        if (firstDayOfMonth.dayOfWeek == DayOfWeek.SUNDAY) 6 else firstDayOfMonth.dayOfWeek.value - 1
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        items(daysOfWeek) { day ->
            Text(text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall)
        }

        items(startOffset) { /* Empty cells */ }

        items(yearMonth.lengthOfMonth()) { dayIndex ->
            val dayOfMonth = dayIndex + 1
            val date = yearMonth.atDay(dayOfMonth)
            val isToday = date == LocalDate.now()
            val isHoliday = holidays.any { it.date == date }
            val hasSchedule = schedules.any {
                when (it.scheduleType) {
                    "daily" -> true
                    "weekly" -> it.weeklyDays?.contains(date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())) == true
                    "monthly" -> it.monthlyDays?.contains(date.dayOfMonth) == true
                    else -> false
                }
            }

            DayCell(
                day = dayOfMonth.toString(),
                isToday = isToday,
                isHoliday = isHoliday,
                hasSchedule = hasSchedule,
                modifier = Modifier.clickable { onDateClick(date) }
            )
        }
    }
}

@Composable
fun DayCell(day: String, isToday: Boolean, isHoliday: Boolean, hasSchedule: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(CircleShape)
                .background(if (isToday) MaterialTheme.colorScheme.primaryContainer else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day,
                textAlign = TextAlign.Center,
                color = if (isHoliday) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
        if (hasSchedule) {
            Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary))
        }
    }
}

// ... (AllSchedulesList, ScheduleListItem, TimelineList, TimelineListItem implementations remain the same)

// These need to be top-level or in a separate file to be used by the preview and the fragment.
@Composable
fun AllSchedulesList(schedules: List<Schedule>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(schedules) { schedule ->
            ScheduleListItem(schedule = schedule)
        }
    }
}

@Composable
fun ScheduleListItem(schedule: Schedule) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Workflow: ${schedule.workflowId}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Type: ${schedule.scheduleType}", style = MaterialTheme.typography.bodyMedium)
            }
            Text(text = schedule.time ?: "", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun TimelineList(date: LocalDate, holidays: List<Holiday>, schedules: List<Schedule>) {
    val holidaysForDay = holidays.filter { it.date == date }
    val schedulesForDay = schedules.filter {
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

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(timelineItems) { item ->
            TimelineListItem(item = item)
        }
    }
}

@Composable
fun TimelineListItem(item: Any) {
    val time: String
    val title: String

    when (item) {
        is Schedule -> {
            time = item.time ?: "N/A"
            title = "Workflow: ${item.workflowId}"
        }
        is Holiday -> {
            time = "All Day"
            title = item.name
        }
        else -> {
            time = ""
            title = "Unknown event"
        }
    }

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(text = time, modifier = Modifier.width(80.dp), style = MaterialTheme.typography.bodyMedium)
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
    }
}

// Helper to get string resource in Composable
@Composable
fun stringResource(id: Int, vararg formatArgs: Any): String {
    return LocalContext.current.resources.getString(id, *formatArgs)
}
