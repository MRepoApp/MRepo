package com.sanmer.mrepo.ui.screens.repository

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.datastore.repository.Option
import com.sanmer.mrepo.datastore.repository.RepositoryMenuExt
import com.sanmer.mrepo.model.online.OnlineModule
import com.sanmer.mrepo.model.state.OnlineState
import com.sanmer.mrepo.ui.component.Segment
import com.sanmer.mrepo.ui.component.SegmentedButtons
import com.sanmer.mrepo.ui.component.SegmentedButtonsDefaults
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.ui.utils.expandedShape

val options = listOf(
    Option.NAME to R.string.menu_sort_option_name,
    Option.UPDATED_TIME to R.string.menu_sort_option_updated
)

@Composable
fun RepositoryMenu(
    setMenu: (RepositoryMenuExt) -> Unit
) {
    val userPreferences = LocalUserPreferences.current
    var open by rememberSaveable { mutableStateOf(false) }

    IconButton(
        onClick = { open = true }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.menu_outline),
            contentDescription = null
        )

        if (open) {
            BottomSheet(
                onClose = { open = false },
                menu = userPreferences.repositoryMenu,
                setMenu = setMenu
            )
        }
    }
}

@Composable
private fun BottomSheet(
    state: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onClose: () -> Unit,
    menu: RepositoryMenuExt,
    setMenu: (RepositoryMenuExt) -> Unit
) = ModalBottomSheet(
    onDismissRequest = onClose,
    sheetState = state,
    shape = BottomSheetDefaults.expandedShape(15.dp),
    windowInsets = WindowInsets.navigationBars
) {
    Text(
        text = stringResource(id = R.string.menu_advanced_menu),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    Column(
        modifier = Modifier.padding(all = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier.padding(bottom = 8.dp),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.outline)
        ) {
            ModuleItem(
                module = OnlineModule.example(),
                state = OnlineState.example(),
                enabled = false
            )
        }

        Text(
            text = stringResource(id = R.string.menu_sort_mode),
            style = MaterialTheme.typography.titleSmall
        )

        SegmentedButtons(
            border = SegmentedButtonsDefaults.border(
                color = MaterialTheme.colorScheme.secondary
            )
        ) {
            options.forEach { (option, label) ->
                Segment(
                    selected = option == menu.option,
                    onClick = { setMenu(menu.copy(option = option)) },
                    colors = SegmentedButtonsDefaults.buttonColor(
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        selectedContentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    icon = null
                ) {
                    Text(text = stringResource(id = label))
                }
            }
        }

        FlowRow(
            modifier = Modifier
                .fillMaxWidth(1f)
                .wrapContentHeight(align = Alignment.Top),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MenuChip(
                selected = menu.descending,
                onClick = { setMenu(menu.copy(descending = !menu.descending)) },
                label = { Text(text = stringResource(id = R.string.menu_descending)) }
            )

            MenuChip(
                selected = menu.pinInstalled,
                onClick = { setMenu(menu.copy(pinInstalled = !menu.pinInstalled)) },
                label = { Text(text = stringResource(id = R.string.menu_pin_installed)) }
            )

            MenuChip(
                selected = menu.showIcon,
                onClick = { setMenu(menu.copy(showIcon = !menu.showIcon)) },
                label = { Text(text = stringResource(id = R.string.menu_show_icon)) }
            )

            MenuChip(
                selected = menu.showLicense,
                onClick = { setMenu(menu.copy(showLicense = !menu.showLicense)) },
                label = { Text(text = stringResource(id = R.string.menu_show_license)) }
            )

            MenuChip(
                selected = menu.showUpdatedTime,
                onClick = { setMenu(menu.copy(showUpdatedTime = !menu.showUpdatedTime)) },
                label = { Text(text = stringResource(id = R.string.menu_show_updated)) }
            )
        }
    }
}

@Composable
fun MenuChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) = FilterChip(
    selected = selected,
    onClick = onClick,
    label = label,
    modifier = modifier.height(32.dp),
    enabled = enabled,
    leadingIcon = {
        if (!selected) {
            Point(size = 8.dp)
        }
    },
    trailingIcon = {
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = null,
                modifier = Modifier.size(FilterChipDefaults.IconSize)
            )
        }
    },
    shape = CircleShape,
    colors = FilterChipDefaults.filterChipColors(
        iconColor = MaterialTheme.colorScheme.secondary,
        selectedContainerColor = MaterialTheme.colorScheme.secondary,
        selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
        selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondary
    ),
    border = FilterChipDefaults.filterChipBorder(
        borderColor = MaterialTheme.colorScheme.secondary,
    )
)

@Composable
private fun Point(
    size: Dp,
    color: Color = LocalContentColor.current
) = Canvas(
    modifier = Modifier.size(size)
) {
    drawCircle(
        color = color,
        radius = this.size.width / 2,
        center = this.center
    )
}