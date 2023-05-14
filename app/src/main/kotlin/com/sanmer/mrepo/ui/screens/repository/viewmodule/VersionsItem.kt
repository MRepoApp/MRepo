package com.sanmer.mrepo.ui.screens.repository.viewmodule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanmer.mrepo.R
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.model.json.ModuleUpdateItem
import com.sanmer.mrepo.model.json.versionDisplay
import com.sanmer.mrepo.ui.component.ExpandableItem
import com.sanmer.mrepo.utils.expansion.toDate
import com.sanmer.mrepo.viewmodel.DetailViewModel

@Composable
fun VersionsItem(
    isRoot: Boolean,
    viewModel: DetailViewModel = hiltViewModel()
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
                UpdateItem(
                    value = it,
                    isRoot = isRoot
                )
            }
        }
    }
}

@Composable
private fun UpdateItem(
    value: ModuleUpdateItem,
    isRoot: Boolean,
    viewModel: DetailViewModel = hiltViewModel()
) {
    var repo: Repo? by remember { mutableStateOf(null) }
    LaunchedEffect(value) {
        repo = viewModel.getRepoByUrl(value.repoUrl)
    }

    var update by remember { mutableStateOf(false) }
    if (update) UpdateItemDialog(value = value, isRoot = isRoot) { update = false }

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
                modifier = Modifier.weight(1f),
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
    value: ModuleUpdateItem,
    isRoot: Boolean,
    viewModel: DetailViewModel = hiltViewModel(),
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
                text = stringResource(id = R.string.view_module_version_dialog_desc),
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
                        viewModel.downloader(
                            context = context,
                            item = value
                        )
                        onClose()
                    }
                ) {
                    Text(text = stringResource(id = R.string.module_download))
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = {
                        viewModel.downloader(
                            context = context,
                            item = value,
                            install = true
                        )
                        onClose()
                    },
                    enabled = isRoot
                ) {
                    Text(text = stringResource(id = R.string.module_install))
                }
            }
        }
    }
}