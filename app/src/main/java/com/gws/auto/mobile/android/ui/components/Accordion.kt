package com.gws.auto.mobile.android.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme

@Composable
fun Accordion(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        content()
    }
}

@Composable
fun AccordionItem(
    title: String,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "AccordionArrow")

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand or collapse",
                modifier = Modifier.rotate(rotationAngle)
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp)) {
                content()
            }
        }
        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
fun AccordionPreview() {
    GWSAutoForAndroidTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Accordion {
                AccordionItem(title = "Section 1") {
                    Text("This is the content of section 1.")
                }
                AccordionItem(title = "Section 2") {
                    Text("This is the content of section 2. It can be a bit longer to see how it wraps.")
                }
            }
        }
    }
}
