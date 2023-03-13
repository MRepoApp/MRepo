package com.sanmer.mrepo.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.R
import com.sanmer.mrepo.ui.component.LinearProgressIndicator
import com.sanmer.mrepo.ui.utils.MarkdownText
import com.sanmer.mrepo.viewmodel.HomeViewModel

@Composable
fun AppUpdateItem(
    viewModel: HomeViewModel = viewModel()
) = Surface(
    shape = RoundedCornerShape(20.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 2.dp,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
) {
    val owner = LocalLifecycleOwner.current
    var progress by remember { mutableStateOf(0f) }
    viewModel.observeProgress(owner) { progress = it }

    var update by remember { mutableStateOf(false) }
    if (update) UpdateDialog { update = false }

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(
                id = R.string.app_update_latest,
                "${viewModel.update?.version} (${viewModel.update?.versionCode})"
            ),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(
                id = R.string.app_update_installed,
                "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            ),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = stringResource(
                id = R.string.app_update_package,
                BuildConfig.APPLICATION_ID
            ),
            style = MaterialTheme.typography.bodyMedium,
        )

        Row(
            modifier = Modifier
                .padding(bottom = 15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (progress != 0f) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .padding(start=6.dp, end = 15.dp)
                        .weight(1f)
                        .height(6.dp)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Button(
                onClick = { update = true }
            ) {
                Text(text = stringResource(id = R.string.app_update_update))
            }
        }
    }
}

@Composable
fun UpdateDialog(
    viewModel: HomeViewModel = viewModel(),
    onClose: () -> Unit
) = AlertDialog(
    onDismissRequest = onClose,
) {
    val context = LocalContext.current
    Surface(
        shape = RoundedCornerShape(25.dp),
        color = AlertDialogDefaults.containerColor,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(
            modifier = Modifier.padding(all = 24.dp)
        ) {
            MarkdownText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                text = viewModel.changelog,
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = onClose
                ) {
                    Text(text = stringResource(id = R.string.dialog_cancel))
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = {
                        viewModel.installer(context = context)
                        onClose()
                    }
                ) {
                    Text(text = stringResource(id = R.string.app_update_update))
                }
            }
        }
    }
}