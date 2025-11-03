package com.gws.auto.mobile.android.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme

@Composable
fun AppSeparator(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = MaterialTheme.colorScheme.outline
    )
}

@Preview(showBackground = true)
@Composable
fun AppSeparatorPreview() {
    GWSAutoForAndroidTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column {
                Text("Radix Primitives")
                AppSeparator(modifier = Modifier.padding(vertical = 16.dp))
                Text("An open-source UI component library.")
            }
        }
    }
}
