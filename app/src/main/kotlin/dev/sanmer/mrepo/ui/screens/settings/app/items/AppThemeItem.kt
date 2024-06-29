package dev.sanmer.mrepo.ui.screens.settings.app.items

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.datastore.model.DarkMode
import dev.sanmer.mrepo.ui.component.SettingNormalItem
import dev.sanmer.mrepo.ui.utils.expandedShape

@Composable
internal fun AppThemeItem(
    themeColor: Int,
    darkMode: DarkMode,
    isDarkMode: Boolean,
    onThemeColorChange: (Int) -> Unit,
    onDarkModeChange: (DarkMode) -> Unit
) {
    var open by remember { mutableStateOf(false) }
    if (open) {
        BottomSheet(
            onClose = { open = false },
            themeColor = themeColor,
            darkMode = darkMode,
            isDarkMode = isDarkMode,
            onThemeColorChange = onThemeColorChange,
            onDarkModeChange = onDarkModeChange
        )
    }

    SettingNormalItem(
        icon = R.drawable.color_swatch,
        title = stringResource(id = R.string.settings_app_theme),
        desc = stringResource(id = R.string.settings_app_theme_desc),
        onClick = { open = true }
    )
}

@Composable
private fun BottomSheet(
    onClose: () -> Unit,
    themeColor: Int,
    darkMode: DarkMode,
    isDarkMode: Boolean,
    onThemeColorChange: (Int) -> Unit,
    onDarkModeChange: (DarkMode) -> Unit,
) = ModalBottomSheet(
    onDismissRequest = onClose,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    shape = BottomSheetDefaults.expandedShape(15.dp),
    windowInsets = WindowInsets.navigationBars,
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 0.dp
) {
    Text(
        text = stringResource(id = R.string.settings_app_theme),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    TitleItem(text = stringResource(id = R.string.app_theme_palette))
    ThemePaletteItem(
        themeColor = themeColor,
        isDarkMode = isDarkMode,
        onChange = onThemeColorChange
    )

    TitleItem(text = stringResource(id = R.string.app_theme_dark_theme))
    DarkModeItem(
        darkMode = darkMode,
        onChange = onDarkModeChange
    )
}

@Composable
private fun TitleItem(
    text: String
) = Text(
    text = text,
    style = MaterialTheme.typography.titleSmall,
    modifier = Modifier.padding(start = 18.dp, top = 18.dp)
)