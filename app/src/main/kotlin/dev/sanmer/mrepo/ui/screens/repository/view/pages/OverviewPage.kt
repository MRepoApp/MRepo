package dev.sanmer.mrepo.ui.screens.repository.view.pages

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.model.local.LocalModule
import dev.sanmer.mrepo.model.local.versionDisplay
import dev.sanmer.mrepo.model.online.OnlineModule
import dev.sanmer.mrepo.model.online.VersionItem
import dev.sanmer.mrepo.utils.extensions.toDateTime

@Composable
internal fun OverviewPage(
    online: OnlineModule,
    item: VersionItem?,
    local: LocalModule?,
    updatable: Boolean,
    setUpdatable: (Boolean) -> Unit,
    isProviderAlive: Boolean,
    onInstall: (Context, VersionItem) -> Unit
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
) {
    Column(
        modifier = Modifier
            .padding(all = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.view_module_description),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = online.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    HorizontalDivider(thickness = 0.9.dp)

    if (item != null) {
        CloudItem(
            item = item,
            isProviderAlive = isProviderAlive,
            onInstall = onInstall
        )

        HorizontalDivider(thickness = 0.9.dp)
    }

    if (local != null) {
        LocalItem(
            local = local,
            updatable = updatable,
            setUpdatable = setUpdatable
        )

        HorizontalDivider(thickness = 0.9.dp)
    }
}

@Composable
private fun CloudItem(
    item: VersionItem,
    isProviderAlive: Boolean,
    onInstall: (Context, VersionItem) -> Unit
) = Column(
    modifier = Modifier
        .padding(all = 16.dp)
        .fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    val context = LocalContext.current

    Text(
        text = stringResource(id = R.string.view_module_cloud),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ValueItem(
            key = stringResource(id = R.string.view_module_version),
            value = item.version,
            modifier = Modifier.weight(1f)
        )

        ElevatedAssistChip(
            enabled = isProviderAlive,
            onClick = { onInstall(context, item) },
            label = { Text(text = stringResource(id = R.string.module_install)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.device_mobile_down),
                    contentDescription = null,
                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                )
            }
        )
    }

    ValueItem(
        key = stringResource(id = R.string.view_module_last_updated),
        value = item.timestamp.toDateTime().toString()
    )
}

@Composable
private fun LocalItem(
    local: LocalModule,
    updatable: Boolean,
    setUpdatable: (Boolean) -> Unit
) = Column(
    modifier = Modifier
        .padding(all = 16.dp)
        .fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    Text(
        text = stringResource(id = R.string.view_module_local),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val versionDisplay by remember {
            derivedStateOf { local.versionDisplay }
        }

        ValueItem(
            key = stringResource(id = R.string.view_module_version),
            value = versionDisplay,
            modifier = Modifier.weight(1f)
        )

        ElevatedFilterChip(
            selected = updatable,
            onClick = { setUpdatable(!updatable) },
            label = {
                Text(
                    text = stringResource(id = if (updatable) {
                        R.string.view_module_update_notifying
                    } else {
                        R.string.view_module_update_dismissed
                    })
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = if (updatable) {
                        R.drawable.notification
                    } else {
                        R.drawable.notification_off
                    }),
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        )
    }

    if (local.lastUpdated != 0L) {
        ValueItem(
            key = stringResource(id = R.string.view_module_last_updated),
            value = local.lastUpdated.toDateTime().toString()
        )
    }
}

@Composable
private fun ValueItem(
    key: String,
    value: String?,
    modifier: Modifier = Modifier
) {
    if (value.isNullOrBlank()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = key,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}