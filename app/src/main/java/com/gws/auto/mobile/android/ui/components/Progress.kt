package com.gws.auto.mobile.android.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme
import kotlinx.coroutines.delay

@Composable
fun GwsProgress(
    progress: Float,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp) // h-4
            .clip(MaterialTheme.shapes.extraLarge), // rounded-full
        color = MaterialTheme.colorScheme.primary, // bg-primary
        trackColor = MaterialTheme.colorScheme.secondaryContainer, // bg-secondary
        strokeCap = StrokeCap.Round
    )
}

@Preview(showBackground = true)
@Composable
fun GwsProgressPreview() {
    GWSAutoForAndroidTheme {
        var currentProgress by remember { mutableStateOf(0.1f) }
        val animatedProgress by animateFloatAsState(
            targetValue = currentProgress,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
            label = "ProgressAnimation"
        )

        LaunchedEffect(Unit) {
            while (true) {
                delay(1000)
                currentProgress = if (currentProgress < 1f) currentProgress + 0.2f else 0.1f
            }
        }

        Surface(modifier = Modifier.padding(16.dp)) {
            Column {
                GwsProgress(progress = animatedProgress)
            }
        }
    }
}
