package com.gws.auto.mobile.android.ui.schedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleSettingsActivity : ComponentActivity() {
    private val viewModel: ScheduleSettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GWSAutoForAndroidTheme {
                ScheduleSettingsScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSettingsScreen(viewModel: ScheduleSettingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val scheduleTypes = listOf("時間毎", "日毎", "週毎", "月毎")
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("予約実行の設定") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = uiState.scheduleType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("繰り返し") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    scheduleTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                viewModel.onScheduleTypeChange(type)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (uiState.scheduleType) {
                    "時間毎" -> HourlySettings(
                        interval = uiState.hourlyInterval,
                        onIntervalChange = { viewModel.setHourlyInterval(it) }
                    )
                    "日毎" -> DailySettings(
                        time = uiState.dailyTime,
                        onTimeChange = { viewModel.setDailyTime(it) }
                    )
                    "週毎" -> WeeklySettings(
                        selectedDays = uiState.weeklyDays,
                        onDayToggle = { viewModel.toggleWeeklyDay(it) },
                        time = uiState.weeklyTime,
                        onTimeChange = { viewModel.setWeeklyTime(it) }
                    )
                    "月毎" -> MonthlySettings(
                        selectedDays = uiState.monthlyDays,
                        onDayToggle = { viewModel.toggleMonthlyDay(it) },
                        time = uiState.monthlyTime,
                        onTimeChange = { viewModel.setMonthlyTime(it) }
                    )
                }
            }

            Button(onClick = { viewModel.saveSchedule() }, modifier = Modifier.fillMaxWidth()) {
                Text("保存")
            }
        }
    }
}

@Composable
fun HourlySettings(interval: Int, onIntervalChange: (Int) -> Unit) {
    Column {
        Text("実行間隔: ${interval}時間毎")
        Slider(
            value = interval.toFloat(),
            onValueChange = { onIntervalChange(it.toInt()) },
            valueRange = 1f..12f,
            steps = 10
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailySettings(time: java.time.LocalTime, onTimeChange: (java.time.LocalTime) -> Unit) {
    val timePickerState = rememberTimePickerState(initialHour = time.hour, initialMinute = time.minute)
    // Update ViewModel when state changes
    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        onTimeChange(java.time.LocalTime.of(timePickerState.hour, timePickerState.minute))
    }
    TimePicker(state = timePickerState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklySettings(
    selectedDays: Set<String>,
    onDayToggle: (String) -> Unit,
    time: java.time.LocalTime,
    onTimeChange: (java.time.LocalTime) -> Unit
) {
    val daysOfWeek = listOf("日", "月", "火", "水", "木", "金", "土")
    val timePickerState = rememberTimePickerState(initialHour = time.hour, initialMinute = time.minute)
    // Update ViewModel when state changes
    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        onTimeChange(java.time.LocalTime.of(timePickerState.hour, timePickerState.minute))
    }

    Column {
        Text("実行する曜日")
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            daysOfWeek.forEach { day ->
                FilterChip(
                    selected = day in selectedDays,
                    onClick = { onDayToggle(day) },
                    label = { Text(day) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TimePicker(state = timePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlySettings(
    selectedDays: Set<Int>,
    onDayToggle: (Int) -> Unit,
    time: java.time.LocalTime,
    onTimeChange: (java.time.LocalTime) -> Unit
) {
    val daysInMonth = (1..31).toList()
    val timePickerState = rememberTimePickerState(initialHour = time.hour, initialMinute = time.minute)
    // Update ViewModel when state changes
    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        onTimeChange(java.time.LocalTime.of(timePickerState.hour, timePickerState.minute))
    }

    Column(modifier = Modifier.fillMaxHeight()) {
        Text("実行する日")
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(daysInMonth.size) { index ->
                val day = daysInMonth[index]
                FilterChip(
                    selected = day in selectedDays,
                    onClick = { onDayToggle(day) },
                    label = { Text(day.toString()) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TimePicker(state = timePickerState)
    }
}
