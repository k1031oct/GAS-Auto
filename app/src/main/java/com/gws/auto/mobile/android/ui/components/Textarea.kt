package com.gws.auto.mobile.android.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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

@Composable
fun GwsTextarea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 80.dp), // min-h-[80px]
        singleLine = false,
        enabled = enabled,
        textStyle = MaterialTheme.typography.bodyLarge, // text-base
        placeholder = placeholder?.let { 
            { 
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // placeholder:text-muted-foreground
                )
            }
        },
        shape = MaterialTheme.shapes.medium, // rounded-md
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary, // focus-visible:ring-ring
            unfocusedBorderColor = MaterialTheme.colorScheme.outline, // border-input
            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f) // disabled:opacity-50
        )
    )
}

@Preview(showBackground = true)
@Composable
fun GwsTextareaPreview() {
    GWSAutoForAndroidTheme {
        var text by remember { mutableStateOf("") }
        Surface(modifier = Modifier.padding(16.dp)) {
            Column {
                GwsTextarea(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = "Enter your comment..."
                )
            }
        }
    }
}
