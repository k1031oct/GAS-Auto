package com.gws.auto.mobile.android.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme


@Composable
fun AppChart(modifier: Modifier = Modifier) {
    val model = CartesianChartModel(
        entrySeriesOf(2, 1, 4, 3, 5)
    )
    CartesianChart(
        modifier = modifier.height(250.dp),
        model = model,
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(),
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AppChartPreview() {
    GWSAutoForAndroidTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            AppChart()
        }
    }
}
