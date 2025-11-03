package com.gws.auto.mobile.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme

enum class AlertVariant {
    Default,
    Destructive
}

@Composable
fun AppAlert(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    variant: AlertVariant = AlertVariant.Default,
    icon: ImageVector? = Icons.Default.Info,
) {
    val backgroundColor = when (variant) {
        AlertVariant.Default -> MaterialTheme.colorScheme.surfaceVariant
        AlertVariant.Destructive -> MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = when (variant) {
        AlertVariant.Default -> MaterialTheme.colorScheme.onSurfaceVariant
        AlertVariant.Destructive -> MaterialTheme.colorScheme.onErrorContainer
    }
    val borderColor = when (variant) {
        AlertVariant.Default -> MaterialTheme.colorScheme.outline
        AlertVariant.Destructive -> MaterialTheme.colorScheme.error
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = backgroundColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null, // Decorative icon
                    modifier = Modifier.size(20.dp).align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AppAlertPreview() {
    GWSAutoForAndroidTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AppAlert(
                title = "Heads up!",
                description = "You can add an icon to your alert."
            )

            AppAlert(
                variant = AlertVariant.Destructive,
                title = "Error",
                description = "This is a destructive alert."
            )
        }
    }
}
