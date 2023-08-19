package com.sanmer.mrepo.ui.screens.settings.repositories

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.ui.component.LabelItem
import com.sanmer.mrepo.utils.extensions.toDateTime

@Composable
fun RepositoryItem(
    repo: Repo,
    toggle: (Boolean) -> Unit,
    update: () -> Unit,
    delete: () -> Unit,
) = Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp,
    onClick = { toggle(!repo.enable) },
) {
    val (alpha, textDecoration) = when {
        !repo.isCompatible -> 0.5f to TextDecoration.LineThrough
        !repo.enable -> 0.5f to TextDecoration.None
        else -> 1f to TextDecoration.None
    }

    Column(
        modifier = Modifier
            .padding(all = 12.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Crossfade(
                targetState = repo.enable,
                label = "RepositoryItem"
            ) {
                if (it) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(id = R.drawable.tick_circle_bold),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                } else {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(id = R.drawable.close_circle_bold),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .alpha(alpha),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = repo.name,
                    style = MaterialTheme.typography.titleSmall
                        .copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = textDecoration
                )

                Text(
                    text = stringResource(id = R.string.module_update_at,
                        repo.metadata.timestamp.toDateTime()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    textDecoration = textDecoration
                )
            }

            if (repo.isCompatible) {
                LabelItem(
                    text = stringResource(id = R.string.repo_modules,
                        repo.metadata.size)
                )
            } else {
                LabelItem(
                    text = stringResource(id = R.string.repo_incompatible)
                        .toUpperCase(Locale.current),
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            }
        }

        Row(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))

            ButtonItem(
                icon = R.drawable.import_outline,
                label = R.string.repo_options_update,
                onClick = update
            )

            ButtonItem(
                icon = R.drawable.trash_outline,
                label = R.string.repo_options_delete,
                onClick = delete
            )
        }
    }
}

@Composable
private fun ButtonItem(
    @DrawableRes icon: Int,
    @StringRes label: Int,
    onClick: () -> Unit
) = FilledTonalButton(
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