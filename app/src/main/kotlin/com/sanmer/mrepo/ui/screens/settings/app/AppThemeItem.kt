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
import com.sanmer.mrepo.ui.component.SettingNormalItem
import kotlinx.coroutines.launch

@Composable
fun AppThemeItem() {
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
        BottomSheet(bottomSheetState) { openBottomSheet = false }
    }
}


@Composable
private fun BottomSheet(
    state: SheetState,
    onClose: () -> Unit
) = ModalBottomSheet(
    onDismissRequest = onClose,
    sheetState = state,
    shape = RoundedCornerShape(15.dp),
    scrimColor = Color.Transparent // TODO: Wait for the windowInsets parameter to be set
) {
    TitleItem(text = stringResource(id = R.string.app_theme_palette))
    ThemePaletteItem()

    TitleItem(text = stringResource(id = R.string.app_theme_dark_theme))
    DarkModeItem()
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