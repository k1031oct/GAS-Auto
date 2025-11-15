package com.gws.auto.mobile.android.ui.wizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.gws.auto.mobile.android.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThemeFragment : Fragment() {

    private val viewModel: WizardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val theme by viewModel.theme.collectAsState()
                ThemeScreen(selectedTheme = theme, onThemeSelected = { viewModel.setTheme(it) })
            }
        }
    }
}

@Composable
fun ThemeScreen(selectedTheme: String, onThemeSelected: (String) -> Unit) {
    val themes = listOf("System", "Light", "Dark")

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = stringResource(R.string.wizard_theme_title), style = MaterialTheme.typography.headlineSmall)
        Text(text = stringResource(R.string.wizard_theme_subtitle), style = MaterialTheme.typography.bodyLarge)

        Column(modifier = Modifier.padding(top = 16.dp)) {
            themes.forEach { theme ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onThemeSelected(theme) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (theme == selectedTheme),
                        onClick = { onThemeSelected(theme) }
                    )
                    Text(
                        text = theme,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
