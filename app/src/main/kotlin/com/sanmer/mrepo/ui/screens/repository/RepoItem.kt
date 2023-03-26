package com.sanmer.mrepo.ui.screens.repository

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanmer.mrepo.R
import com.sanmer.mrepo.data.database.entity.Repo
import com.sanmer.mrepo.ui.component.Checkbox
import com.sanmer.mrepo.ui.component.DropdownMenu
import com.sanmer.mrepo.utils.expansion.shareText
import com.sanmer.mrepo.utils.expansion.toDateTime
import com.sanmer.mrepo.viewmodel.RepositoryViewModel

private sealed class Menu(
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
) {
    object Update : Menu(
        label = R.string.repo_options_update,
        icon = R.drawable.import_outline
    )
    object Share : Menu(
        label = R.string.repo_options_share,
        icon = R.drawable.send_outline
    )
    object Delete : Menu(
        label = R.string.repo_options_delete,
        icon = R.drawable.trash_outline
    )
}

private val options = listOf(
    Menu.Update,
    Menu.Share,
    Menu.Delete
)

@Composable
fun RepoItem(
    viewModel: RepositoryViewModel = viewModel(),
    repo: Repo,
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    var delete by remember { mutableStateOf(false) }
    if (delete) {
        DeleteDialog(
            onClose = { delete = false },
            onConfirm = {
                viewModel.delete(repo)
            },
            repo = repo
        )
    }

    var failure by remember { mutableStateOf(false) }
    var message: String? by remember { mutableStateOf(null) }
    if (failure) {
        FailureDialog(
            onClose = {
                failure = false
                message = null
            },
            repo = repo,
            message = message
        )
    }

    val onUpdate: () -> Unit = {
        viewModel.getUpdate(repo) {
            failure = true
            message = it.message
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        offset = DpOffset(12.dp, 12.dp),
        shape = RoundedCornerShape(15.dp),
        contentAlignment = Alignment.TopEnd,
        surface = {
            RepoItem(
                repo = repo,
                onChange = {
                    repo.isEnable = it
                    viewModel.update(repo)
                },
                onLongClick = { expanded = true },
                onIconClick = { expanded = true }
            )
        }
    ) {
        options.forEach { option ->
            MenuItem(
                value = option,
                onClose = { expanded = false },
                onDelete = { delete = true },
                onShare = { context.shareText(repo.url) },
                onUpdate = onUpdate
            )
        }
    }
}

@Composable
private fun RepoItem(
    repo: Repo,
    onChange: (Boolean) -> Unit,
    onLongClick: () -> Unit,
    onIconClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .combinedClickable(
                onClick = { onChange(!repo.enable) },
                onLongClick = onLongClick,
                role = Role.Checkbox
            )
            .padding(all = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = repo.isEnable,
            onCheckedChange = null
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = repo.name,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = stringResource(id = R.string.repo_modules, repo.size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = stringResource(id = R.string.repo_last_update, repo.timestamp.toDateTime()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        IconButton(
            onClick = onIconClick
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun MenuItem(
    value: Menu,
    onClose: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onUpdate: () -> Unit
) = DropdownMenuItem(
    leadingIcon = {
        Icon(
            modifier = Modifier.size(22.dp),
            painter = painterResource(id = value.icon),
            contentDescription = null
        )
    },
    text = { Text(text = stringResource(id = value.label)) },
    onClick = {
        when (value) {
            Menu.Delete -> onDelete()
            Menu.Share -> onShare()
            Menu.Update -> onUpdate()
        }
        onClose()
    }
)

@Composable
private fun DeleteDialog(
    onClose: () -> Unit,
    onConfirm: () -> Unit,
    repo: Repo,
) = AlertDialog(
    shape = RoundedCornerShape(25.dp),
    onDismissRequest = onClose,
    title = { Text(text = stringResource(id = R.string.dialog_title_attention)) },
    text = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.repo_options_delete_dialog_desc1, repo.name))

            Spacer(modifier = Modifier.height(30.dp))
            Text(text = stringResource(id = R.string.repo_options_delete_dialog_desc2))
        }
    },
    confirmButton = {
        TextButton(
            onClick = {
                onConfirm()
                onClose()
            }
        ) {
            Text(
                text = stringResource(id = R.string.repo_options_delete)
            )
        }
    },
    dismissButton = {
        TextButton(
            onClick = onClose
        ) {
            Text(
                text = stringResource(id = R.string.dialog_cancel)
            )
        }
    }
)

@Composable
fun FailureDialog(
    onClose: () -> Unit,
    repo: Repo,
    message: String?
) = AlertDialog(
    shape = RoundedCornerShape(25.dp),
    onDismissRequest = onClose,
    title = { Text(text = repo.name) },
    text = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.repo_update_dialog_failure, message.toString()))
        }
    },
    confirmButton = {
        TextButton(
            onClick = {
                onClose()
            }
        ) {
            Text(
                text = stringResource(id = R.string.dialog_ok)
            )
        }
    }
)