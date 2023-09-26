package com.sanmer.mrepo.ui.screens.settings.app.items

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Const
import com.sanmer.mrepo.app.utils.MediaStoreUtils.getAbsoluteFileForUri
import com.sanmer.mrepo.ui.component.SettingNormalItem
import com.sanmer.mrepo.ui.component.TextFieldDialog
import com.sanmer.mrepo.utils.extensions.toFile
import java.io.File

@Composable
fun DownloadPathItem(
    downloadPath: File,
    onChange: (File) -> Unit
) {
    var edit by remember { mutableStateOf(false) }
    if (edit) OpenDocumentTreeDialog(
        path = downloadPath,
        onClose = { edit = false },
        onConfirm = { if (it != downloadPath) onChange(it) }
    )

    SettingNormalItem(
        icon = R.drawable.files,
        text = stringResource(id = R.string.settings_download_path),
        subText = downloadPath.absolutePath,
        onClick = { edit = true }
    )
}

@Composable
private fun OpenDocumentTreeDialog(
    path : File,
    onClose: () -> Unit,
    onConfirm: (File) -> Unit
) {
    val context = LocalContext.current
    var newPath by remember { mutableStateOf(path) }

    var parent by remember {
        mutableStateOf(Const.DIR_PUBLIC_DOWNLOADS.absolutePath)
    }
    var name by remember {
        mutableStateOf(newPath.toRelativeString(parent.toFile()))
    }

    val interactionSource = remember { MutableInteractionSource() }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        context.contentResolver.takePersistableUriPermission(uri, flags)

        newPath = getAbsoluteFileForUri(context, uri)
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

    TextFieldDialog(
        shape = RoundedCornerShape(20.dp),
        onDismissRequest = onClose,
        title = { Text(text = stringResource(id = R.string.settings_download_path)) },
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
        },
        launchKeyboard = false
    ) {
        OutlinedTextField(
            textStyle = MaterialTheme.typography.bodyLarge,
            value = name,
            onValueChange = {},
            shape = RoundedCornerShape(15.dp),
            label = { Text(text = "$parent/") },
            singleLine = true,
            readOnly = true,
            interactionSource = interactionSource
        )
    }
}
