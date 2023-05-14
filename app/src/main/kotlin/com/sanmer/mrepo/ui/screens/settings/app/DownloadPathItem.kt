package com.sanmer.mrepo.ui.screens.settings.app

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.ui.component.SettingNormalItem
import java.io.File

@Composable
fun DownloadPathItem(
    downloadPath: File,
    onChange: (String) -> Unit
) {
    val path = downloadPath.absolutePath
    var edit by remember { mutableStateOf(false) }
    if (edit) {
        EditDialog(
            path = path,
            onClose = { edit = false },
            onConfirm = { if (it != path) onChange(it) }
        )
    }

    SettingNormalItem(
        iconRes = R.drawable.cube_scan_outline,
        text = stringResource(id = R.string.settings_download_path),
        subText = path,
        onClick = { edit = true }
    )
}

@Composable
private fun EditDialog(
    path : String,
    onClose: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val prefix = Const.DIR_PUBLIC_DOWNLOADS.absolutePath
    var value by remember { mutableStateOf(
        path.replace(prefix, "").let {
            return@let if (it.isNotBlank()) {
                it.substring(1)
            } else {
                it
            }
        }
    ) }

    AlertDialog(
        shape = RoundedCornerShape(20.dp),
        onDismissRequest = onClose,
        title = { Text(text = stringResource(id = R.string.settings_download_path)) },
        text = {
            OutlinedTextField(
                modifier = Modifier,
                textStyle = MaterialTheme.typography.bodyLarge,
                value = value,
                onValueChange = { value = it },
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                label = { Text(text = "$prefix/") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm("$prefix/${value.trim()}")
                    onClose()
                }
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
        }
    )
}
