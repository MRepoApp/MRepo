package com.sanmer.mrepo.ui.screens.modules

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanmer.mrepo.R
import com.sanmer.mrepo.datastore.UserData
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
    userData: UserData,
    expanded: Boolean,
    onClose: () -> Unit
) = DropdownMenu(
    expanded = expanded,
    onDismissRequest = onClose,
    offset = DpOffset(0.dp, 5.dp),
    shape = RoundedCornerShape(15.dp)
) {
    options.forEach {
        MenuItem(
            value = it,
            userData = userData,
            onClose = onClose
        )
    }
}

@Composable
private fun MenuItem(
    viewModel: ModulesViewModel = hiltViewModel(),
    userData: UserData,
    value: Menu,
    onClose: () -> Unit
) = DropdownMenuItem(
    leadingIcon = {
        Icon(
            modifier = Modifier.size(22.dp),
            painter = painterResource(id = value.icon),
            contentDescription = null
        )
    },
    text = { Text(text = stringResource(id = value.label)) },
    onClick = {
        when (value) {
            Menu.Cloud -> viewModel.getOnlineAll()
            Menu.Local -> viewModel.getLocalAll()
        }
        onClose()
    },
    enabled = if (value is Menu.Local) userData.isRoot else true
)