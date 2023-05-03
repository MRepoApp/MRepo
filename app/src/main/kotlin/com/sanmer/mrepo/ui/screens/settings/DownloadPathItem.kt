package com.sanmer.mrepo.ui.screens.settings

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
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.ui.component.SettingNormalItem

@Composable
fun DownloadPathItem(
    userData: UserData,
    onChange: (String) -> Unit
) {
    val path = userData.downloadPath.absolutePath
    var edit by remember { mutableStateOf(false) }
    if (edit) {
        EditDialog(
            title = stringResource(id = R.string.settings_download_path),
            text = path,
            onClose = { edit = false },
            onConfirm = {
                if (it != path) {
                    onChange(it)
                }
            }
        )
    }

    SettingNormalItem(
        iconRes = R.drawable.cube_scan_outline,
        text = stringResource(id = R.string.settings_download_path),
        subText = path,
        onClick = {
            edit = true
        }
    )
}

@Composable
private fun EditDialog(
    onClose: () -> Unit,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit = {},
    title: String,
    text: String
) {
    var value by remember { mutableStateOf(text) }

    AlertDialog(
        shape = RoundedCornerShape(25.dp),
        onDismissRequest = onClose,
        title = { Text(text = title) },
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
                supportingText = {
                    Text(text = stringResource(id = R.string.dialog_empty_default))
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(value)
                    onClose()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.dialog_ok)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancel()
                    onClose()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.dialog_cancel)
                )
            }
        }
    )
}
