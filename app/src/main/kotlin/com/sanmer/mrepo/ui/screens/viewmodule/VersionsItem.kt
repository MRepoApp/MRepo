package com.sanmer.mrepo.ui.screens.viewmodule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanmer.mrepo.R
import com.sanmer.mrepo.data.RepoManger
import com.sanmer.mrepo.data.database.entity.Repo
import com.sanmer.mrepo.data.json.UpdateItem
import com.sanmer.mrepo.data.json.versionDisplay
import com.sanmer.mrepo.provider.EnvProvider
import com.sanmer.mrepo.ui.component.ExpandableItem
import com.sanmer.mrepo.utils.expansion.toDate
import com.sanmer.mrepo.viewmodel.DetailViewModel

@Composable
fun VersionsItem(
    viewModel: DetailViewModel = viewModel()
) {
    var expanded by remember { mutableStateOf(true) }
    ExpandableItem(
        expanded = expanded,
        text = { Text(text = stringResource(id = R.string.view_module_versions)) },
        onExpandedChange = { expanded = it },
        trailingContent = {
            Badge(
                containerColor = MaterialTheme.colorScheme.primary
            ){
                Text(text = viewModel.versions.size.toString())
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            viewModel.versions.forEach {
                UpdateItem(value = it)
            }
        }
    }
}

@Composable
private fun UpdateItem(
    value: UpdateItem
) {
    var repo: Repo? by remember { mutableStateOf(null) }
    LaunchedEffect(value) {
        repo = RepoManger.getById(value.repoId)
    }

    var update by remember { mutableStateOf(false) }
    if (update) UpdateItemDialog(value = value) { update = false }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(15.dp),
        onClick = { update = true }
    ) {
        Row(
            modifier = Modifier
                .padding(all = 15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value.versionDisplay,
                    style = MaterialTheme.typography.bodyMedium,
                )

                repo?.let {
                    Text(
                        text = stringResource(id = R.string.view_module_provided, it.name),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = value.timestamp.toDate(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun UpdateItemDialog(
    viewModel: DetailViewModel = viewModel(),
    value: UpdateItem,
    onClose: () -> Unit
) = AlertDialog(
    onDismissRequest = onClose
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
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = value.versionDisplay,
                color = AlertDialogDefaults.titleContentColor,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                modifier = Modifier.padding(bottom = 24.dp),
                text = stringResource(id = R.string.modules_version_dialog_desc,
                    stringResource(id = R.string.module_install).toLowerCase(Locale.current)),
                color = AlertDialogDefaults.textContentColor,
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onClose
                ) {
                    Text(text = stringResource(id = R.string.dialog_cancel))
                }

                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = {
                        viewModel.downloader(context = context, item = value)
                        onClose()
                    }
                ) {
                    Text(text = stringResource(id = R.string.module_download))
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = {
                        viewModel.installer(context = context, item = value)
                        onClose()
                    },
                    enabled = EnvProvider.isRoot
                ) {
                    Text(text = stringResource(id = R.string.module_install))
                }
            }
        }
    }
}