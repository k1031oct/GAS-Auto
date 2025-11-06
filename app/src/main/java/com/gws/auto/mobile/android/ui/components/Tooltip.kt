package com.gws.auto.mobile.android.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTooltip(
    tooltipText: String,
    content: @Composable () -> Unit
) {
    TooltipBox(
        positionProvider = rememberTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(tooltipText)
            }
        },
        state = rememberTooltipState()
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun AppTooltipPreview() {
    GWSAutoForAndroidTheme {
        Surface(modifier = Modifier.padding(32.dp)) {
            AppTooltip(tooltipText = "Add to library") {
                IconButton(onClick = { /* Do something */ }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add to library")
                }
            }
        }
    }
}
