package com.sanmer.mrepo.ui.screens.settings

import android.content.Context
import android.os.PowerManager
import androidx.annotation.StringRes
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.compat.BuildCompat
import com.sanmer.mrepo.ui.component.DropdownMenu
import com.sanmer.mrepo.utils.Utils

private enum class Menu(
    @StringRes val label: Int,
    val reason: String,
) {
    Reboot(label = R.string.settings_menu_reboot, reason = ""),
    Userspace(label = R.string.settings_menu_reboot_userspace, reason = "userspace"),
    Recovery(label = R.string.settings_menu_reboot_recovery, reason = "recovery"),
    Bootloader(label = R.string.settings_menu_reboot_bootloader, reason = "bootloader"),
    Download(label = R.string.settings_menu_reboot_download, reason = "download"),
    EDL(label = R.string.settings_menu_reboot_edl, reason = "edl")
}

@Composable
fun SettingsMenu(
    expanded: Boolean,
    onClose: () -> Unit
) = DropdownMenu(
    expanded = expanded,
    onDismissRequest = onClose,
    offset = DpOffset(0.dp, 10.dp)
) {
    val powerManager = LocalContext.current.getSystemService(Context.POWER_SERVICE) as PowerManager?
    val hasUserspace by remember {
        derivedStateOf { BuildCompat.atLeastR && powerManager?.isRebootingUserspaceSupported == true }
    }

    MenuItem(value = Menu.Reboot, onClose = onClose)

    if (hasUserspace) {
        MenuItem(value = Menu.Userspace, onClose = onClose)
    }

    MenuItem(value = Menu.Recovery, onClose = onClose)
    MenuItem(value = Menu.Bootloader, onClose = onClose)
    MenuItem(value = Menu.Download, onClose = onClose)
    MenuItem(value = Menu.EDL, onClose = onClose)
}

@Composable
private fun MenuItem(
    value: Menu,
    onClose: () -> Unit
) = DropdownMenuItem(
    text = { Text(text = stringResource(id = value.label)) },
    onClick = {
        Utils.reboot(value.reason)
        onClose()
    }
)