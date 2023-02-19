package com.sanmer.mrepo.ui.screens.home

import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.annotation.StringRes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.component.DropdownMenu
import com.sanmer.mrepo.utils.SvcPower

private sealed class Menu(
    @StringRes val label: Int,
    val reason: String,
) {
    object Reboot : Menu(
        label = R.string.menu_reboot,
        reason = ""
    )
    object Recovery : Menu(
        label = R.string.menu_reboot_recovery,
        reason = "recovery"
    )
    object Userspace : Menu(
        label = R.string.menu_reboot_userspace,
        reason = "userspace"
    )
    object Bootloader : Menu(
        label = R.string.menu_reboot_bootloader,
        reason = "bootloader"
    )
    object Download : Menu(
        label = R.string.menu_reboot_download,
        reason = "download"
    )
    object EDL : Menu(
        label = R.string.menu_reboot_edl,
        reason = "edl"
    )
}

private val options = mutableListOf(
    Menu.Reboot,
    Menu.Recovery,
    Menu.Bootloader,
    Menu.Download,
    Menu.EDL
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
    val powerManager = LocalContext.current.getSystemService(Context.POWER_SERVICE) as PowerManager?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
        && powerManager?.isRebootingUserspaceSupported == true
    ) {
        options.add(1, Menu.Userspace)
    }

    options.forEach {
        MenuItem(
            value = it,
            onClose = onClose
        )
    }
}

@Composable
private fun MenuItem(
    value: Menu,
    onClose: () -> Unit
) = DropdownMenuItem(
    text = { Text(text = stringResource(id = value.label)) },
    onClick = {
        SvcPower.reboot(value.reason)
        onClose()
    }
)