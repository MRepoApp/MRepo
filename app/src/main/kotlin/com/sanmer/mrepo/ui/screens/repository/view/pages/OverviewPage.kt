package com.sanmer.mrepo.ui.screens.repository.view.pages

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.model.local.LocalModule
import com.sanmer.mrepo.model.local.versionDisplay
import com.sanmer.mrepo.model.online.OnlineModule
import com.sanmer.mrepo.model.online.VersionItem
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.utils.extensions.toDateTime

@Composable
fun OverviewPage(
    online: OnlineModule,
    item: VersionItem?,
    local: LocalModule?,
    isProviderAlive: Boolean,
    notifyUpdates: Boolean,
    setUpdatesTag: (Boolean) -> Unit,
    onInstall: (VersionItem) -> Unit
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
            notifyUpdates = notifyUpdates,
            setUpdatesTag = setUpdatesTag
        )

        HorizontalDivider(thickness = 0.9.dp)
    }
}

@Composable
private fun CloudItem(
    item: VersionItem,
    isProviderAlive: Boolean,
    onInstall: (VersionItem) -> Unit
) = Column(
    modifier = Modifier
        .padding(all = 16.dp)
        .fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    val userPreferences = LocalUserPreferences.current

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
            value = item.versionDisplay,
            modifier = Modifier.weight(1f)
        )

        ElevatedAssistChip(
            enabled = userPreferences.isRoot && isProviderAlive,
            onClick = { onInstall(item) },
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
        value = item.timestamp.toDateTime()
    )
}

@Composable
private fun LocalItem(
    local: LocalModule,
    notifyUpdates: Boolean,
    setUpdatesTag: (Boolean) -> Unit
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
        ValueItem(
            key = stringResource(id = R.string.view_module_version),
            value = local.versionDisplay,
            modifier = Modifier.weight(1f)
        )

        ElevatedFilterChip(
            selected = notifyUpdates,
            onClick = { setUpdatesTag(!notifyUpdates) },
            label = {
                Text(
                    text = stringResource(id = if (notifyUpdates) {
                        R.string.view_module_update_ignore
                    } else {
                        R.string.view_module_update_notify
                    })
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = if (notifyUpdates) {
                        R.drawable.target_off
                    } else {
                        R.drawable.target
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
            value = local.lastUpdated.toDateTime()
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