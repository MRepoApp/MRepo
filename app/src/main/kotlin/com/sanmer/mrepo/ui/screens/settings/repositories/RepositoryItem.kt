package com.sanmer.mrepo.ui.screens.settings.repositories

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.ui.component.Checkbox
import com.sanmer.mrepo.ui.component.DropdownMenu
import com.sanmer.mrepo.utils.expansion.shareText
import com.sanmer.mrepo.utils.expansion.toDateTime

private enum class Menu(
    @StringRes val label: Int,
    @DrawableRes val icon: Int
) {
    Update(
        label = R.string.repo_options_update,
        icon = R.drawable.import_outline
    ),

    Share(
        label = R.string.repo_options_share,
        icon = R.drawable.send_outline
    ),

    Delete(
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
fun RepositoryItem(
    repo: Repo,
    deleteRepo: (Repo) -> Unit,
    updateRepo: (Repo) -> Unit,
    getRepoUpdate: (Repo, (Throwable) -> Unit) -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    var delete by remember { mutableStateOf(false) }
    if (delete) {
        DeleteDialog(
            repo = repo,
            onClose = { delete = false },
            onConfirm = { deleteRepo(repo) }
        )
    }

    var failure by remember { mutableStateOf(false) }
    var message: String? by remember { mutableStateOf(null) }
    if (failure) {
        FailureDialog(
            repo = repo,
            message = message,
            onClose = {
                failure = false
                message = null
            }
        )
    }

    val onUpdate: () -> Unit = {
        getRepoUpdate(repo) {
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
                onChange = { updateRepo(repo.copy(enable = it)) },
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
) = Row(
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
        checked = repo.enable,
        onCheckedChange = null
    )

    Spacer(modifier = Modifier.width(16.dp))
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = repo.name,
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = stringResource(id = R.string.repo_metadata, repo.size),
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
    repo: Repo,
    onClose: () -> Unit,
    onConfirm: () -> Unit
) = AlertDialog(
    shape = RoundedCornerShape(20.dp),
    onDismissRequest = onClose,
    title = { Text(text = stringResource(id = R.string.dialog_attention)) },
    text = {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.repo_delete_dialog_desc1, repo.name))

            Spacer(modifier = Modifier.height(30.dp))
            Text(text = stringResource(id = R.string.repo_delete_dialog_desc2))
        }
    },
    confirmButton = {
        TextButton(
            onClick = {
                onConfirm()
                onClose()
            }
        ) {
            Text(text = stringResource(id = R.string.repo_options_delete))
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

@Composable
fun FailureDialog(
    repo: Repo,
    message: String?,
    onClose: () -> Unit
) = AlertDialog(
    shape = RoundedCornerShape(20.dp),
    onDismissRequest = onClose,
    title = { Text(text = repo.name) },
    text = {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.repo_update_dialog_failure, message.toString()),
                maxLines = 5,
            )
        }
    },
    confirmButton = {
        TextButton(
            onClick = onClose
        ) {
            Text(text = stringResource(id = R.string.dialog_ok))
        }
    }
)