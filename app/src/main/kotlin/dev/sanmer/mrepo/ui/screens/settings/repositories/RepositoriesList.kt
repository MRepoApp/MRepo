package dev.sanmer.mrepo.ui.screens.settings.repositories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.database.entity.online.RepoEntity

@Composable
internal fun RepositoriesList(
    list: List<RepoEntity>,
    state: LazyListState,
    insert: (RepoEntity) -> Unit,
    delete: (RepoEntity) -> Unit,
    update: (RepoEntity) -> Unit
) = LazyColumn(
    state = state,
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(10.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
) {
    items(
        items = list,
        key = { it.url }
    ) { repo ->
        RepositoryItem(
            repo = repo,
            onToggle = { insert(it) },
            onUpdate = update,
            onDelete = delete,
        )
    }
}

@Composable
private fun RepositoryItem(
    repo: RepoEntity,
    onToggle: (RepoEntity) -> Unit,
    onUpdate: (RepoEntity) -> Unit,
    onDelete: (RepoEntity) -> Unit,
) {
    var delete by remember { mutableStateOf(false) }
    if (delete) DeleteDialog(
        repo = repo,
        onClose = { delete = false },
        onConfirm = { onDelete(repo) }
    )

    RepositoryItem(
        repo = repo,
        toggle = { onToggle(repo.copy(disable = it)) },
        onUpdate = { onUpdate(repo) },
        onDelete = { delete = true }
    )
}

@Composable
private fun DeleteDialog(
    repo: RepoEntity,
    onClose: () -> Unit,
    onConfirm: () -> Unit
) = AlertDialog(
    shape = RoundedCornerShape(20.dp),
    onDismissRequest = onClose,
    title = { Text(text = repo.name) },
    text = { Text(text = stringResource(id = R.string.repo_delete_dialog_desc)) },
    confirmButton = {
        TextButton(
            onClick = {
                onConfirm()
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