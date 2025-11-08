package com.gws.auto.mobile.android.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GwsTabs(
    tabs: List<String>,
    modifier: Modifier = Modifier,
    onTabSelected: (Int) -> Unit = {},
    content: @Composable (Int) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(modifier = modifier) {
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surfaceVariant, // bg-muted
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant, // text-muted-foreground
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        onTabSelected(index)
                    },
                    text = { Text(text = title, style = MaterialTheme.typography.bodyMedium) },
                    selectedContentColor = MaterialTheme.colorScheme.onSurface, // data-[state=active]:text-foreground
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant // text-muted-foreground
                )
            }
        }

        // Content for the selected tab
        Box(modifier = Modifier.padding(top = 8.dp)) { // mt-2
            content(selectedTabIndex)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GwsTabsPreview() {
    GWSAutoForAndroidTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            val tabTitles = listOf("Account", "Password", "Notifications")

            GwsTabs(tabs = tabTitles) {
                // This is a simple example. In a real app, you would have different
                // composables for each tab's content.
                when (it) {
                    0 -> Text("This is the Account tab content.", modifier = Modifier.padding(16.dp))
                    1 -> Text("This is the Password tab content.", modifier = Modifier.padding(16.dp))
                    2 -> Text("This is the Notifications tab content.", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}
