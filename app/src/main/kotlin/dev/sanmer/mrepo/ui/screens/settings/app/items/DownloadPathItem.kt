package dev.sanmer.mrepo.ui.screens.settings.app.items

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.app.Const
import dev.sanmer.mrepo.ui.component.SettingNormalItem
import dev.sanmer.mrepo.ui.component.TextFieldDialog
import java.io.File

@Composable
internal fun DownloadPathItem(
    downloadPath: File,
    onChange: (File) -> Unit
) {
    var edit by remember { mutableStateOf(false) }
    if (edit) EditDialog(
        path = downloadPath,
        onClose = { edit = false },
        onConfirm = { if (it != downloadPath) onChange(it) }
    )

    SettingNormalItem(
        icon = R.drawable.files,
        title = stringResource(id = R.string.settings_download_path),
        desc = downloadPath.absolutePath,
        onClick = { edit = true }
    )
}

@Composable
private fun EditDialog(
    path : File,
    onClose: () -> Unit,
    onConfirm: (File) -> Unit
) {
    var name by remember {
        mutableStateOf(path.toRelativeString(Const.PUBLIC_DOWNLOADS))
    }

    TextFieldDialog(
        shape = RoundedCornerShape(20.dp),
        onDismissRequest = onClose,
        title = { Text(text = stringResource(id = R.string.settings_download_path)) },
        confirmButton = {
            TextButton(
                onClick = {
                    val new = File(Const.PUBLIC_DOWNLOADS, name.trim())
                    onConfirm(new)
                    onClose()
                },
            ) {
                Text(text = stringResource(id = R.string.dialog_ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onClose
            ) {
                Text(text = stringResource(id = R.string.dialog_cancel))
            }
        },
        launchKeyboard = false
    ) {
        OutlinedTextField(
            textStyle = MaterialTheme.typography.bodyLarge,
            value = name,
            onValueChange = { name = it },
            shape = RoundedCornerShape(15.dp),
            label = { Text(text = Const.PUBLIC_DOWNLOADS.absolutePath) },
            singleLine = true
        )
    }
}