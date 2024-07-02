package dev.sanmer.mrepo.ui.screens.repository

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.datastore.model.Homepage
import dev.sanmer.mrepo.datastore.model.Option
import dev.sanmer.mrepo.datastore.model.RepositoryMenu
import dev.sanmer.mrepo.ui.component.MenuChip
import dev.sanmer.mrepo.ui.component.Segment
import dev.sanmer.mrepo.ui.component.SegmentedButtons
import dev.sanmer.mrepo.ui.component.SegmentedButtonsDefaults
import dev.sanmer.mrepo.ui.providable.LocalUserPreferences
import dev.sanmer.mrepo.ui.utils.expandedShape
import dev.sanmer.mrepo.viewmodel.RepositoryViewModel

private val options = listOf(
    Option.Name to R.string.menu_sort_option_name,
    Option.UpdatedTime to R.string.menu_sort_option_updated
)

@Composable
internal fun RepositoryMenu(
    setMenu: (RepositoryMenu) -> Unit,
    setHomepage: () -> Unit,
) {
    val userPreferences = LocalUserPreferences.current
    var open by remember { mutableStateOf(false) }

    IconButton(
        onClick = { open = true }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.menu_2),
            contentDescription = null
        )

        if (open) {
            BottomSheet(
                onClose = { open = false },
                menu = userPreferences.repositoryMenu,
                setMenu = setMenu,
                isHomepage = userPreferences.homepage == Homepage.Repository,
                setHomepage = setHomepage
            )
        }
    }
}

@Composable
private fun BottomSheet(
    onClose: () -> Unit,
    menu: RepositoryMenu,
    setMenu: (RepositoryMenu) -> Unit,
    isHomepage: Boolean,
    setHomepage: () -> Unit
) = ModalBottomSheet(
    onDismissRequest = onClose,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    shape = BottomSheetDefaults.expandedShape(15.dp),
    windowInsets = WindowInsets.navigationBars,
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 0.dp
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
            tonalElevation = 6.dp,
            border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.outline)
        ) {
            ModuleItem(
                module = RepositoryViewModel.ModuleWrapper.example(),
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
                selected = menu.pinUpdatable,
                onClick = { setMenu(menu.copy(pinUpdatable = !menu.pinUpdatable)) },
                label = { Text(text = stringResource(id = R.string.menu_pin_updatable)) }
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

            MenuChip(
                selected = isHomepage,
                onClick = setHomepage,
                label = { Text(text = stringResource(id = R.string.menu_set_homepage)) }
            )
        }
    }
}