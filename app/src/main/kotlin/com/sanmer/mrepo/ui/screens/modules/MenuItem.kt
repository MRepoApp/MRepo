package com.sanmer.mrepo.ui.screens.modules

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.provider.local.LocalLoader
import com.sanmer.mrepo.provider.repo.RepoLoader
import com.sanmer.mrepo.ui.component.DropdownMenu

private sealed class Menu(
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
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
            onClose = onClose
        )
    }
}

@Composable
private fun MenuItem(
    context: Context = LocalContext.current,
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
            Menu.Cloud -> {
                RepoLoader.getRepoAll(context)
            }
            Menu.Local -> {
                LocalLoader.getLocalAll(context)
            }
        }
        onClose()
    }
)