package dev.sanmer.mrepo.ui.screens.repository

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.model.online.OnlineModule
import dev.sanmer.mrepo.ui.component.LabelItem
import dev.sanmer.mrepo.ui.component.Logo
import dev.sanmer.mrepo.ui.providable.LocalUserPreferences
import dev.sanmer.mrepo.utils.extensions.toDate
import dev.sanmer.mrepo.viewmodel.RepositoryViewModel.OnlineState

@Composable
fun ModuleItem(
    module: OnlineModule,
    state: OnlineState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true
) = Surface(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),
    enabled = enabled,
    shape = RoundedCornerShape(10.dp)
) {
    val userPreferences = LocalUserPreferences.current
    val menu = userPreferences.repositoryMenu
    val hasLabel = (state.hasLicense && menu.showLicense)
            || state.installed

    Row(
        modifier = Modifier.padding(all = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (menu.showIcon) {
            Logo(
                icon = R.drawable.box,
                modifier = Modifier.size(40.dp),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )

            Spacer(modifier = Modifier.width(10.dp))
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = module.name,
                style = MaterialTheme.typography.titleSmall
                    .copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = module.author,
                style = MaterialTheme.typography.bodyMedium.copy(),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = module.versionDisplay,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            if (menu.showUpdatedTime) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(
                        id = R.string.module_update_at,
                        state.lastUpdated.toDate()
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            if (hasLabel) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (menu.showLicense && module.track.hasLicense) {
                        LabelItem(text = module.track.license)
                    }

                    when {
                        state.updatable ->
                            LabelItem(
                                text = stringResource(id = R.string.module_new),
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )

                        state.installed ->
                            LabelItem(text = stringResource(id = R.string.module_installed))
                    }
                }
            }
        }
    }
}