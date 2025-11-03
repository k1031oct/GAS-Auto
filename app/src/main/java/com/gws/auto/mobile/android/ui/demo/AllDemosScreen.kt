package com.gws.auto.mobile.android.ui.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.components.Accordion
import com.gws.auto.mobile.android.ui.components.AccordionItem
import com.gws.auto.mobile.android.ui.components.AlertVariant
import com.gws.auto.mobile.android.ui.components.AppAlert
import com.gws.auto.mobile.android.ui.components.AppAlertDialog
import com.gws.auto.mobile.android.ui.components.AppAvatar
import com.gws.auto.mobile.android.ui.components.AppBadge
import com.gws.auto.mobile.android.ui.components.AppButton
import com.gws.auto.mobile.android.ui.components.AppCalendar
import com.gws.auto.mobile.android.ui.components.AppCard
import com.gws.auto.mobile.android.ui.components.AppCarousel
// import com.gws.auto.mobile.android.ui.components.AppChart
import com.gws.auto.mobile.android.ui.components.AppCheckbox
import com.gws.auto.mobile.android.ui.components.AppCollapsible
import com.gws.auto.mobile.android.ui.components.AppDialog
import com.gws.auto.mobile.android.ui.components.AppDropdownMenu
import com.gws.auto.mobile.android.ui.components.AppFormItem
import com.gws.auto.mobile.android.ui.components.AppFormLabel
import com.gws.auto.mobile.android.ui.components.AppInput
import com.gws.auto.mobile.android.ui.components.AppMenubar
import com.gws.auto.mobile.android.ui.components.AppPopover
import com.gws.auto.mobile.android.ui.components.AppRadioGroup
import com.gws.auto.mobile.android.ui.components.AppSelect
import com.gws.auto.mobile.android.ui.components.AppSeparator
import com.gws.auto.mobile.android.ui.components.AppSheet
import com.gws.auto.mobile.android.ui.components.AppSkeleton
import com.gws.auto.mobile.android.ui.components.AppSlider
import com.gws.auto.mobile.android.ui.components.BadgeVariant
import com.gws.auto.mobile.android.ui.components.ButtonVariant
import com.gws.auto.mobile.android.ui.components.CardHeader
import kotlinx.coroutines.launch

@Composable
fun AllDemosScreen() {
    var showAlertDialog by remember { mutableStateOf(false) }
    var showGenericDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    if (showAlertDialog) {
        AppAlertDialog(
            onDismissRequest = { showAlertDialog = false },
            onConfirmation = { showAlertDialog = false },
            dialogTitle = "Delete Item",
            dialogText = "Are you sure you want to delete this item? This action cannot be undone.",
            confirmButtonText = "Delete",
            dismissButtonText = "Cancel"
        )
    }

    if (showGenericDialog) {
        AppDialog(
            onDismissRequest = { showGenericDialog = false },
            dialogTitle = "Generic Dialog"
        ) {
            Column {
                Text("This is a generic dialog with custom content.")
                AppButton(onClick = { showGenericDialog = false }, text = "Close")
            }
        }
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AppSheet(
        drawerState = drawerState,
        sheetContent = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Sheet Content")
                Spacer(modifier = Modifier.height(16.dp))
                AppButton(onClick = { scope.launch { drawerState.close() } }, text = "Close Sheet")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val menus = mapOf(
                "File" to listOf("New", "Open", "Save"),
                "Edit" to listOf("Cut", "Copy", "Paste")
            )
            AppMenubar(menus = menus, onMenuItemClick = { menu, item ->
                println("Clicked $item from $menu")
            })

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                AppButton(onClick = { scope.launch { drawerState.open() } }, text = "Open Sheet")
                Spacer(modifier = Modifier.height(32.dp))
                AppButton(onClick = { }, text = "Default Button")
                Spacer(modifier = Modifier.height(16.dp))
                AppButton(
                    onClick = { showAlertDialog = true },
                    text = "Destructive",
                    variant = ButtonVariant.Destructive
                )
                Spacer(modifier = Modifier.height(16.dp))
                AppButton(onClick = { }, text = "Secondary", variant = ButtonVariant.Secondary)

                Spacer(modifier = Modifier.height(32.dp))

                var text by remember { mutableStateOf("") }
                AppInput(
                    value = text,
                    onValueChange = { newText -> text = newText },
                    label = "Email",
                    placeholder = "user@example.com"
                )

                Spacer(modifier = Modifier.height(32.dp))

                Accordion {
                    AccordionItem(title = "Section 1") {
                        Text("This is the content of section 1.")
                    }
                    AccordionItem(title = "Section 2") {
                        Text("This is the content of section 2. It can be a bit longer to see how it wraps.")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                AppAlert(
                    title = "Default Alert",
                    description = "This is a default alert message."
                )
                Spacer(modifier = Modifier.height(16.dp))
                AppAlert(
                    variant = AlertVariant.Destructive,
                    title = "Destructive Alert",
                    description = "This is a destructive alert message."
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppAvatar(imageUrl = "https://i.pravatar.cc/150?u=a042581f4e29026704d", fallbackText = "JD")
                    Spacer(modifier = Modifier.width(16.dp))
                    AppAvatar(imageUrl = null, fallbackText = "GWS")
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppBadge(text = "Default")
                    AppBadge(text = "Secondary", variant = BadgeVariant.Secondary)
                    AppBadge(text = "Destructive", variant = BadgeVariant.Destructive)
                    AppBadge(text = "Outline", variant = BadgeVariant.Outline)
                }

                Spacer(modifier = Modifier.height(32.dp))

                var selectedDate by remember { mutableStateOf<Long?>(null) }
                AppCalendar(onDateSelected = { newDate -> selectedDate = newDate })
                Text("Selected Date: ${selectedDate ?: "None"}")

                Spacer(modifier = Modifier.height(32.dp))

                AppCard(
                    header = {
                        CardHeader(title = "Card Title", description = "This is the card description.")
                    },
                    content = {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Text("This is the main content of the card. You can put any Composable here.")
                        }
                    },
                    footer = {
                        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                            AppButton(onClick = { }, text = "Action")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                val carouselItems = listOf(Color.Red, Color.Green, Color.Blue)
                AppCarousel(items = carouselItems) {item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(item),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Item", style = MaterialTheme.typography.headlineMedium)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // AppChart()

                Spacer(modifier = Modifier.height(32.dp))

                var checkedState1 by remember { mutableStateOf(true) }
                var checkedState2 by remember { mutableStateOf(false) }
                var checkedState3 by remember { mutableStateOf(true) }

                Column(modifier = Modifier.fillMaxWidth()) {
                    AppCheckbox(
                        checked = checkedState1,
                        onCheckedChange = { isChecked -> checkedState1 = isChecked },
                        label = "Option 1"
                    )
                    AppCheckbox(
                        checked = checkedState2,
                        onCheckedChange = { isChecked -> checkedState2 = isChecked },
                        label = "Option 2"
                    )
                    AppCheckbox(
                        checked = checkedState3,
                        onCheckedChange = null, // Disabled checkbox
                        label = "Option 3 (Disabled)",
                        enabled = false
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                AppCollapsible(
                    trigger = { expanded ->
                        Text(
                            text = if (expanded) "Hide Collapsible Content" else "Show Collapsible Content",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                    },
                    content = {
                        Text("This is the content that can be collapsed or expanded.")
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                AppButton(onClick = { showGenericDialog = true }, text = "Show Generic Dialog")

                Spacer(modifier = Modifier.height(32.dp))

                AppDropdownMenu(
                    trigger = { AppButton(onClick = { }, text = "Open Dropdown") },
                    menuItems = listOf("Item 1", "Item 2", "Item 3"),
                    onMenuItemClick = { item -> println("Dropdown item clicked: $item") }
                )

                Spacer(modifier = Modifier.height(32.dp))

                var username by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                Column(modifier = Modifier.fillMaxWidth()) {
                    AppFormItem(
                        label = { AppFormLabel(text = "Username") },
                        content = { AppInput(value = username, onValueChange = { newUsername -> username = newUsername }) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AppFormItem(
                        label = { AppFormLabel(text = "Password") },
                        content = { AppInput(value = password, onValueChange = { newPassword -> password = newPassword }) },
                        errorMessage = if (password.length < 6 && password.isNotEmpty()) "Password must be at least 6 characters." else null
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AppButton(onClick = { /* Handle form submission */ }, text = "Submit")
                }

                Spacer(modifier = Modifier.height(32.dp))

                AppPopover(
                    trigger = { AppButton(onClick = { }, text = "Open Popover") },
                    content = {
                        Column {
                            Text("This is the popover content.")
                            AppButton(onClick = { }, text = "Action in Popover")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                val radioOptions = listOf("Option 1", "Option 2", "Option 3")
                var selectedRadio by remember { mutableStateOf(radioOptions[0]) }
                AppRadioGroup(
                    options = radioOptions,
                    selectedOption = selectedRadio,
                    onOptionSelected = { newSelection -> selectedRadio = newSelection }
                )

                Spacer(modifier = Modifier.height(32.dp))

                AppSeparator()

                Spacer(modifier = Modifier.height(32.dp))

                val selectOptions = listOf("Option A", "Option B", "Option C")
                var selectedOption by remember { mutableStateOf(selectOptions[0]) }
                AppSelect(
                    options = selectOptions,
                    selectedOption = selectedOption,
                    onOptionSelected = { newSelection -> selectedOption = newSelection },
                    label = "Select an option"
                )

                Spacer(modifier = Modifier.height(32.dp))

                AppSkeleton(modifier = Modifier.height(100.dp).fillMaxWidth())

                Spacer(modifier = Modifier.height(32.dp))

                var sliderValue by remember { mutableStateOf(0.5f) }
                Text(text = "Slider Value: ${sliderValue}")
                AppSlider(
                    value = sliderValue,
                    onValueChange = { newValue -> sliderValue = newValue }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
