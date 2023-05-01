package com.sanmer.mrepo.ui.screens.modules

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.component.DropdownMenu
import com.sanmer.mrepo.viewmodel.ModulesViewModel

private sealed class Menu(
    @StringRes val label: Int,
    @DrawableRes val icon: Int
) {
    object Cloud : Menu(
        label = R.string.modules_menu_cloud,
        icon = R.drawable.cloud_change_outline
    )
    object Local : Menu(
        label = R.string.modules_menu_local,
        icon = R.drawable.rotate_outline
    )
}

private val options = listOf(
    Menu.Cloud,
    Menu.Local
)

@Composable
fun MenuItem(
    expanded: Boolean,
    pagerState: PagerState,
    viewModel: ModulesViewModel = hiltViewModel(),
    onClose: () -> Unit
) = DropdownMenu(
    expanded = expanded,
    onDismissRequest = onClose,
    offset = DpOffset(0.dp, 5.dp),
    shape = RoundedCornerShape(15.dp)
) {
    DropdownMenuItem(
        text = { Text(text = stringResource(id = R.string.menu_scroll_top)) },
        onClick = {
            viewModel.scrollToTop(pagerState.currentPage)
            onClose()
        }
    )

    DropdownMenuItem(
        text = { Text(text = stringResource(id = R.string.menu_scroll_bottom)) },
        onClick = {
            viewModel.scrollToBottom(pagerState.currentPage)
            onClose()
        }
    )
}