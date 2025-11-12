package com.gws.auto.mobile.android.ui.schedule

import android.content.Intent
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.data.model.Schedule
import com.gws.auto.mobile.android.domain.model.Holiday
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
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
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val context = LocalContext.current

    val currentVisibleMonth by remember {
        derivedStateOf {
            YearMonth.now().plusMonths((pagerState.currentPage - (Int.MAX_VALUE / 2)).toLong())
        }
    }

    LaunchedEffect(viewModel.currentDate.collectAsState().value) {
        val targetPage = (Int.MAX_VALUE / 2) + ChronoUnit.MONTHS.between(YearMonth.now(), viewModel.currentDate.value)
        if (pagerState.currentPage != targetPage.toInt()) {
            pagerState.animateScrollToPage(targetPage.toInt())
        }
    }

    BackHandler(enabled = selectedDate != null) {
        selectedDate = null
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                context.startActivity(Intent(context, ScheduleSettingsActivity::class.java))
            }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_schedule))
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
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
                modifier = Modifier.weight(1f)
            ) { page ->
                val month = YearMonth.now().plusMonths((page - (Int.MAX_VALUE / 2)).toLong())
                MonthView(
                    yearMonth = month,
                    holidays = holidays,
                    schedules = schedules,
                    onDateClick = { date -> selectedDate = date }
                )
            }

            if (selectedDate != null) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.timeline_for_date, selectedDate!!.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    TimelineList(
                        date = selectedDate!!,
                        holidays = holidays,
                        schedules = schedules
                    )
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.all_schedules_title),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    AllSchedulesList(schedules = schedules)
                }
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
    val daysOfWeek = listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)
    val firstDayOfMonth = yearMonth.atDay(1)
    val startOffset = firstDayOfMonth.dayOfWeek.value % 7

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        items(daysOfWeek) { day ->
            Text(text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall)
        }

        items(startOffset) {}

        items(yearMonth.lengthOfMonth()) { dayIndex ->
            val dayOfMonth = dayIndex + 1
            val date = yearMonth.atDay(dayOfMonth)
            val schedulesForDay = schedules.filter {
                when (it.scheduleType) {
                    "daily" -> true
                    "weekly" -> it.weeklyDays?.contains(date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())) == true
                    "monthly" -> it.monthlyDays?.contains(date.dayOfMonth) == true
                    else -> false
                }
            }
            val holidaysForDay = holidays.filter { it.date == date }

            DayCell(
                date = date,
                schedules = schedulesForDay,
                holidays = holidaysForDay,
                modifier = Modifier.clickable { onDateClick(date) }
            )
        }
    }
}

@Composable
fun DayCell(
    date: LocalDate,
    schedules: List<Schedule>,
    holidays: List<Holiday>,
    modifier: Modifier = Modifier
) {
    val isToday = date == LocalDate.now()

    Column(
        modifier = modifier
            .height(120.dp) // <-- Expanded height
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            textAlign = TextAlign.Center,
            modifier = if (isToday) Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer) else Modifier,
            color = if (holidays.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Display more items
        holidays.forEach {
            ScheduleItemText(it.name)
        }

        schedules.forEach {
            ScheduleItemText(it.workflowId)
        }
    }
}

@Composable
fun ScheduleItemText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

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
