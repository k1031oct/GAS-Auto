package com.gws.auto.mobile.android.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChart
import com.patrykandpatrick.vico.compose.chart.layout.fullWidth
import com.patrykandpatrick.vico.compose.chart.line.lineLayer
import com.patrykandpatrick.vico.core.model.CartesianChartModel
import com.patrykandpatrick.vico.core.model.LineCartesianLayerModel

@Composable
fun AppChart(modifier: Modifier = Modifier) {
    val model = CartesianChartModel(
        LineCartesianLayerModel.build {
            series(2, 1, 4, 3, 5)
        }
    )

    CartesianChart(
        chartModel = model,
        modifier = modifier.height(250.dp),
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(),
        chartLayout = CartesianChart.ChartLayout.fullWidth()
    ) {
        lineLayer()
    }
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
