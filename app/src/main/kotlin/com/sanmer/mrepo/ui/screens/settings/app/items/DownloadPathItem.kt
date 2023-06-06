package com.sanmer.mrepo.ui.screens.settings.app.items

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.utils.MediaStoreUtils.absoluteFile
import com.sanmer.mrepo.ui.component.SettingNormalItem
import com.sanmer.mrepo.utils.expansion.toFile
import java.io.File

@Composable
fun DownloadPathItem(
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
        iconRes = R.drawable.cube_scan_outline,
        text = stringResource(id = R.string.settings_download_path),
        subText = downloadPath.absolutePath,
        onClick = { edit = true }
    )
}

@Composable
private fun EditDialog(
    path : File,
    onClose: () -> Unit,
    onConfirm: (File) -> Unit
) {
    val context = LocalContext.current
    val hasDocumentTree = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).let {
        return@let it.resolveActivity(context.packageManager) != null
    }

    var newPath by remember { mutableStateOf(path) }
    var parent by remember { mutableStateOf(
        Const.DIR_PUBLIC_DOWNLOADS.absolutePath
    ) }

    var name by remember { mutableStateOf(
        newPath.toRelativeString(parent.toFile())
    ) }

    val interactionSource = remember { MutableInteractionSource() }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        context.contentResolver.takePersistableUriPermission(uri, flags)

        newPath = uri.absoluteFile ?: path
        parent = newPath.parent
        name = newPath.name
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                launcher.launch(null)
            }
        }
    }

    AlertDialog(
        shape = RoundedCornerShape(20.dp),
        onDismissRequest = onClose,
        title = { Text(text = stringResource(id = R.string.settings_download_path)) },
        text = {
            OutlinedTextField(
                modifier = Modifier.wrapContentHeight(),
                textStyle = MaterialTheme.typography.bodyLarge,
                value = name,
                onValueChange = {
                    newPath = "$parent/${it.trim()}".toFile()
                    name = it
                },
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                label = { Text(text = "$parent/") },
                singleLine = true,
                readOnly = hasDocumentTree,
                interactionSource = if (hasDocumentTree) {
                    interactionSource
                } else {
                    remember { MutableInteractionSource() }
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(newPath)
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
        }
    )
}
