package com.sanmer.mrepo.ui.screens.settings.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.datastore.DarkMode
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.datastore.isDarkMode
import com.sanmer.mrepo.ui.component.SettingNormalItem
import kotlinx.coroutines.launch

@Composable
fun AppThemeItem(
    userData: UserData,
    onThemeColorChange: (Int) -> Unit,
    onDarkModeChange: (DarkMode) -> Unit
) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val scope = rememberCoroutineScope()
    BackHandler(bottomSheetState.isVisible) {
        scope.launch {
            bottomSheetState.hide()
        }
    }

    SettingNormalItem(
        iconRes = R.drawable.color_swatch_outline,
        text = stringResource(id = R.string.settings_app_theme),
        subText = stringResource(id = R.string.settings_app_theme_desc),
        onClick = { openBottomSheet = true }
    )

    if (openBottomSheet) {
        BottomSheet(
            state = bottomSheetState,
            onClose = { openBottomSheet = false },
            themeColor = userData.themeColor,
            darkMode = userData.darkMode,
            isDarkMode = userData.isDarkMode(),
            onThemeColorChange =onThemeColorChange,
            onDarkModeChange = onDarkModeChange
        )
    }
}


@Composable
private fun BottomSheet(
    state: SheetState,
    onClose: () -> Unit,
    themeColor: Int,
    darkMode: DarkMode,
    isDarkMode: Boolean,
    onThemeColorChange: (Int) -> Unit,
    onDarkModeChange: (DarkMode) -> Unit,
) = ModalBottomSheet(
    onDismissRequest = onClose,
    sheetState = state,
    shape = RoundedCornerShape(15.dp),
    scrimColor = Color.Transparent // TODO: Wait for the windowInsets parameter to be set
) {
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
    color = MaterialTheme.colorScheme.primary,
    modifier = Modifier.padding(start = 18.dp, top = 18.dp)
)