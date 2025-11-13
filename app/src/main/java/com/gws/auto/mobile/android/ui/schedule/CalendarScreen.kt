package com.gws.auto.mobile.android.ui.schedule

import android.content.Intent
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.data.model.Schedule
import com.gws.auto.mobile.android.domain.model.Holiday
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                context.startActivity(Intent(context, ScheduleSettingsActivity::class.java))
            }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_schedule))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { viewModel.moveToPreviousMonth() }) { Text(stringResource(id = R.string.calendar_previous_month_button)) }
                Text(
                    text = currentVisibleMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
                    style = MaterialTheme.typography.headlineSmall
                )
                Button(onClick = { viewModel.moveToNextMonth() }) { Text(stringResource(id = R.string.calendar_next_month_button)) }
            }

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)) {
                val daysOfWeek = listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)
                daysOfWeek.forEach { day ->
                    Text(
                        text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize() // Fills remaining space
            ) { page ->
                val month = YearMonth.now().plusMonths((page - (Int.MAX_VALUE / 2)).toLong())
                MonthView(
                    yearMonth = month,
                    holidays = holidays,
                    schedules = schedules,
                    onDateClick = {
                        selectedDate = it
                        scope.launch { sheetState.show() }
                    }
                )
            }
        }
    }

    if (sheetState.isVisible && selectedDate != null) {
        ModalBottomSheet(
            onDismissRequest = { scope.launch { selectedDate = null; sheetState.hide() } },
            sheetState = sheetState,
            modifier = Modifier.fillMaxHeight(0.9f) // Allow sheet to take up most of the screen
        ) {
            DayTimelineSheet(date = selectedDate!!, holidays = holidays, schedules = schedules)
        }
    }
}

@Composable
fun DayTimelineSheet(date: LocalDate, holidays: List<Holiday>, schedules: List<Schedule>) {
    val timelineHourHeight = 64.dp
    val hourTextWidth = 60.dp
    val eventColor = MaterialTheme.colorScheme.primary

    val schedulesForDay = remember(date, schedules) {
        schedules.mapNotNull { schedule ->
            val scheduledDate = when (schedule.scheduleType) {
                "daily" -> true
                "weekly" -> schedule.weeklyDays?.any { it.equals(date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH), ignoreCase = true) } ?: false
                "monthly" -> schedule.monthlyDays?.contains(date.dayOfMonth) ?: false
                else -> false
            }
            if (scheduledDate) {
                schedule.time?.let { LocalTime.parse(it) to schedule.workflowId }
            } else {
                null
            }
        }.sortedBy { it.first }
    }

    val holidaysForDay = remember(date, holidays) {
        holidays.filter { it.date == date }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
        )

        LazyColumn {
            items(holidaysForDay) { holiday ->
                Text(holiday.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            }

            item {
                HourTimeline(schedules = schedulesForDay, timelineHourHeight = timelineHourHeight, hourTextWidth = hourTextWidth, eventColor = eventColor)
            }
        }
    }
}

@Composable
private fun HourTimeline(schedules: List<Pair<LocalTime, String>>, timelineHourHeight: androidx.compose.ui.unit.Dp, hourTextWidth: androidx.compose.ui.unit.Dp, eventColor: Color) {
    val timelineColor = MaterialTheme.colorScheme.outlineVariant

    BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(timelineHourHeight * 24)) {
        val hourTextWidthPx = with(LocalDensity.current) { hourTextWidth.toPx() }

        // Draw hour lines and labels
        for (hour in 0..23) {
            Row(modifier = Modifier.height(timelineHourHeight).offset(y = (hour * timelineHourHeight.value).dp)) {
                Text(
                    text = String.format("%02d:00", hour),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(hourTextWidth).padding(end = 8.dp),
                    textAlign = TextAlign.End
                )
                Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(timelineColor))
            }
        }

        // Draw events
        schedules.forEach { (time, name) ->
            val yOffset = with(LocalDensity.current) {
                (time.hour * timelineHourHeight.toPx()) + (time.minute / 60f * timelineHourHeight.toPx())
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = yOffset.dp, x = hourTextWidth),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(eventColor))
                Spacer(modifier = Modifier.width(8.dp))
                Text(name, style = MaterialTheme.typography.bodyMedium, fontSize = 14.sp)
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
    val firstDayOfMonth = yearMonth.atDay(1)
    val startOffset = (firstDayOfMonth.dayOfWeek.value % 7).let { if (it == 0) 7 else it } - 1

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.padding(horizontal = 8.dp).fillMaxHeight(),
        userScrollEnabled = false
    ) {
        items(startOffset) { }

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
            .height(120.dp)
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

        holidays.forEach { ScheduleItemText(it.name) }
        schedules.forEach { ScheduleItemText(it.workflowId) }
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
