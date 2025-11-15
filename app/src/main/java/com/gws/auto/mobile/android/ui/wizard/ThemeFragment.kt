package com.gws.auto.mobile.android.ui.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.ui.theme.*

class ThemeFragment : Fragment() {

    private val viewModel: WizardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val theme by viewModel.theme.collectAsState()
                val highlightColor by viewModel.highlightColor.collectAsState()

                GWSAutoForAndroidTheme(theme = theme, highlightColor = highlightColor) {
                    Surface {
                        ThemeSettingsPage(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeSettingsPage(viewModel: WizardViewModel) {
    val currentTheme by viewModel.theme.collectAsState()
    val currentHighlight by viewModel.highlightColor.collectAsState()
    val isDark = when (currentTheme) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemInDarkTheme()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.wizard_theme_title),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.wizard_theme_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))

        Text("Theme", style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            ThemeRadioButton("Light", currentTheme) { viewModel.setTheme("Light") }
            ThemeRadioButton("Dark", currentTheme) { viewModel.setTheme("Dark") }
            ThemeRadioButton("System", currentTheme) { viewModel.setTheme("System") }
        }

        Spacer(Modifier.height(24.dp))

        Text("Highlight Color", style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            HighlightColorChip("default", if (isDark) md_theme_dark_primary else md_theme_light_primary, currentHighlight) { viewModel.setHighlightColor("default") }
            Spacer(Modifier.width(16.dp))
            HighlightColorChip("forest", if (isDark) forest_theme_dark_primary else forest_theme_light_primary, currentHighlight) { viewModel.setHighlightColor("forest") }
            Spacer(Modifier.width(16.dp))
            HighlightColorChip("ocean", if (isDark) ocean_theme_dark_primary else ocean_theme_light_primary, currentHighlight) { viewModel.setHighlightColor("ocean") }
            Spacer(Modifier.width(16.dp))
            HighlightColorChip("sakura", if (isDark) sakura_theme_dark_primary else sakura_theme_light_primary, currentHighlight) { viewModel.setHighlightColor("sakura") }
        }
    }
}

@Composable
private fun ThemeRadioButton(text: String, selectedTheme: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = (text == selectedTheme),
            onClick = onClick
        )
        Text(text = text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
private fun HighlightColorChip(colorName: String, color: Color, selectedHighlight: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .then(
                if (colorName == selectedHighlight) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                } else Modifier
            )
    )
}
