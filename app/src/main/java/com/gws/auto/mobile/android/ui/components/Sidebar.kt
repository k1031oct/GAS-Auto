package com.gws.auto.mobile.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme
import kotlinx.coroutines.launch

@Composable
fun AppSidebar(
    drawerState: DrawerState,
    isMobile: Boolean,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    if (isMobile) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = { ModalDrawerSheet { drawerContent() } },
            content = content
        )
    } else {
        PermanentNavigationDrawer(
            drawerContent = { PermanentDrawerSheet(Modifier.width(256.dp)) { drawerContent() } },
            content = content
        )
    }
}

@Composable
fun SidebarHeader(content: @Composable () -> Unit) {
    Column(Modifier.padding(16.dp)) { content() }
}

@Composable
fun SidebarContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxHeight().padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}

@Composable
fun SidebarFooter(content: @Composable () -> Unit) {
    Column(Modifier.padding(16.dp)) { content() }
}

@Composable
fun SidebarMenuItem(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(text) },
        icon = { Icon(icon, contentDescription = text) },
        selected = isSelected,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AppSidebarPreview() {
    GWSAutoForAndroidTheme {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val configuration = LocalConfiguration.current
        val isMobile = configuration.screenWidthDp < 600
        var selectedItem by remember { mutableStateOf("Home") }

        val items = listOf(
            "Home" to Icons.Default.Home,
            "Messages" to Icons.Default.Email,
            "Settings" to Icons.Default.Settings
        )

        AppSidebar(
            drawerState = drawerState,
            isMobile = isMobile,
            drawerContent = {
                SidebarHeader {
                    AppAvatar(imageUrl = null, fallbackText = "GWS")
                }
                SidebarContent {
                    items.forEach { (name, icon) ->
                        SidebarMenuItem(
                            text = name,
                            icon = icon,
                            isSelected = selectedItem == name,
                            onClick = {
                                selectedItem = name
                                scope.launch { drawerState.close() }
                             }
                        )
                    }
                }
                SidebarFooter {
                    Text("Version 1.0.0")
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("GWS Auto") },
                        navigationIcon = {
                            if (isMobile) {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                                }
                            }
                        }
                    )
                }
            ) {
                Surface(modifier = Modifier.padding(it).padding(16.dp)) {
                    Text("Main application content goes here. Selected: $selectedItem")
                }
            }
        }
    }
}
