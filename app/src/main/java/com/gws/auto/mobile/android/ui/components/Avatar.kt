package com.gws.auto.mobile.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme

@Composable
fun AppAvatar(
    imageUrl: String?,
    fallbackText: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = "Avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    AppAvatarFallback(text = fallbackText)
                },
                error = {
                    AppAvatarFallback(text = fallbackText)
                }
            )
        } else {
            AppAvatarFallback(text = fallbackText)
        }
    }
}

@Composable
private fun AppAvatarFallback(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.take(2).uppercase(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppAvatarPreview() {
    GWSAutoForAndroidTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            AppAvatar(imageUrl = null, fallbackText = "AB")
        }
    }
}
