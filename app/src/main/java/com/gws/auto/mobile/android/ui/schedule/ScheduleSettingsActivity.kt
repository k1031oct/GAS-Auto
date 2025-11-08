package com.gws.auto.mobile.android.ui.schedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme

class ScheduleSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GWSAutoForAndroidTheme {
                ScheduleSettingsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSettingsScreen() {
    val scheduleTypes = listOf("時間毎", "日毎", "週毎", "月毎")
    var selectedType by remember { mutableStateOf(scheduleTypes[0]) }
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
                .verticalScroll(rememberScrollState())
        ) {
            // Schedule Type Selector
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                 TextField(
                    value = selectedType,
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
                                selectedType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedType) {
                "時間毎" -> HourlySettings()
                "日毎" -> DailySettings()
                "週毎" -> WeeklySettings()
                "月毎" -> MonthlySettings()
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { /* TODO: Save schedule */ }, modifier = Modifier.fillMaxWidth()) {
                Text("保存")
            }
        }
    }
}

@Composable
fun HourlySettings() {
    var selectedHour by remember { mutableStateOf(1) }
    Column {
        Text("実行間隔: ${selectedHour}時間毎")
        Slider(
            value = selectedHour.toFloat(),
            onValueChange = { selectedHour = it.toInt() },
            valueRange = 1f..12f,
            steps = 10
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailySettings() {
    val timePickerState = rememberTimePickerState()
    TimePicker(state = timePickerState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklySettings() {
    val daysOfWeek = listOf("日", "月", "火", "水", "木", "金", "土")
    val selectedDays = remember { mutableStateListOf<String>() }
    val timePickerState = rememberTimePickerState()

    Column {
        Text("実行する曜日")
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            daysOfWeek.forEach { day ->
                FilterChip(
                    selected = day in selectedDays,
                    onClick = {
                        if (day in selectedDays) {
                            selectedDays.remove(day)
                        } else {
                            selectedDays.add(day)
                        }
                    },
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
fun MonthlySettings() {
    val daysInMonth = (1..31).toList()
    val selectedDays = remember { mutableStateListOf<Int>() }
    val timePickerState = rememberTimePickerState()

    Column {
        Text("実行する日")
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(daysInMonth.size) { index ->
                val day = daysInMonth[index]
                FilterChip(
                    selected = day in selectedDays,
                    onClick = {
                        if (day in selectedDays) {
                            selectedDays.remove(day)
                        } else {
                            selectedDays.add(day)
                        }
                    },
                    label = { Text(day.toString()) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TimePicker(state = timePickerState)
    }
}
