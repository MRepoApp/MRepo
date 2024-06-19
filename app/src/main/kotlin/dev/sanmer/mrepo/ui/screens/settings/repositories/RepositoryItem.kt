package dev.sanmer.mrepo.ui.screens.settings.repositories

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.database.entity.RepoEntity
import dev.sanmer.mrepo.ui.component.LabelItem
import dev.sanmer.mrepo.ui.utils.expandedShape
import dev.sanmer.mrepo.utils.extensions.shareText
import dev.sanmer.mrepo.utils.extensions.toDateTime

@Composable
fun RepositoryItem(
    repo: RepoEntity,
    toggle: (Boolean) -> Unit,
    update: () -> Unit,
    delete: () -> Unit,
) = Surface(
    shape = RoundedCornerShape(15.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp,
    onClick = { toggle(!repo.disable) },
) {
    Column(
        modifier = Modifier
            .padding(all = 15.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = painterResource(id = if (repo.disable) {
                    R.drawable.circle_x_filled
                } else {
                    R.drawable.circle_check_filled
                }),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .alpha(if (repo.disable) 0.5f else 1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = repo.name,
                    style = MaterialTheme.typography.titleSmall
                        .copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stringResource(id = R.string.module_update_at, repo.timestamp.toDateTime()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            LabelItem(
                text = stringResource(id = R.string.repo_modules, repo.size),
                upperCase = false
            )
        }

        Row(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))

            var open by remember { mutableStateOf(false) }
            if (open) {
                BottomSheet(
                    repo = repo,
                    onDelete = delete,
                    onClose = { open = false }
                )
            }

            ButtonItem(
                icon = R.drawable.at,
                onClick = { open = true }
            )

            ButtonItem(
                icon = R.drawable.cloud_download,
                label = R.string.repo_options_update,
                onClick = update
            )
        }
    }
}

@Composable
private fun BottomSheet(
    repo: RepoEntity,
    onDelete: () -> Unit,
    onClose: () ->  Unit
) = ModalBottomSheet(
    onDismissRequest = onClose,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    shape = BottomSheetDefaults.expandedShape(15.dp),
    windowInsets = WindowInsets.navigationBars
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(bottom = 18.dp)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = repo.name,
                    style = MaterialTheme.typography.titleSmall
                        .copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stringResource(id = R.string.module_update_at, repo.timestamp.toDateTime()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            LabelItem(
                text = stringResource(id = R.string.repo_modules, repo.size),
                upperCase = false
            )
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = RoundedCornerShape(15.dp),
            value = repo.url,
            onValueChange = {},
            readOnly = true,
            singleLine = true
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            ButtonItem(
                icon = R.drawable.share,
                onClick = { context.shareText(repo.url) }
            )

            ButtonItem(
                icon = R.drawable.trash,
                label = R.string.repo_options_delete,
                onClick = onDelete
            )
        }
    }
}

@Composable
private fun ButtonItem(
    @DrawableRes icon: Int,
    @StringRes label: Int? = null,
    onClick: () -> Unit
) = if (label != null) {
    FilledTonalButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = icon),
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = stringResource(id = label)
        )
    }
} else {
    FilledTonalIconButton(
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = icon),
            contentDescription = null
        )
    }
}