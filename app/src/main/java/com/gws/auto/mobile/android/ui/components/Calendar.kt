package com.gws.auto.mobile.android.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCalendar(
    modifier: Modifier = Modifier,
    onDateSelected: (Long?) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(datePickerState.selectedDateMillis) {
        onDateSelected(datePickerState.selectedDateMillis)
    }

    DatePicker(
        state = datePickerState,
        modifier = modifier,
        showModeToggle = true,
        colors = DatePickerDefaults.colors(
            selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
            selectedDayContainerColor = MaterialTheme.colorScheme.primary,
            todayContentColor = MaterialTheme.colorScheme.primary,
            todayDateBorderColor = MaterialTheme.colorScheme.primary
        ),
    )
}

@Preview(showBackground = true)
@Composable
fun AppCalendarPreview() {
    GWSAutoForAndroidTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            AppCalendar { selectedDateMillis ->
                selectedDateMillis?.let {
                    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
                    println("Selected date: $formattedDate")
                }
            }
        }
    }
}
