package com.sanmer.mrepo.ui.screens.repository.viewmodule.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.model.module.LocalModule
import com.sanmer.mrepo.model.module.OnlineModule
import com.sanmer.mrepo.viewmodel.ModuleViewModel

@Composable
fun OverviewPage(
    online: OnlineModule,
    local: LocalModule,
    installed: Boolean,
    localModuleInfo: ModuleViewModel.LocalModuleInfo?
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

    if (installed && localModuleInfo != null) {
        Divider(thickness = 0.9.dp)
        LocalItem(
            local = local,
            moduleInfo = localModuleInfo
        )
    }

    Divider(thickness = 0.9.dp)
}

@Composable
private fun LocalItem(
    local: LocalModule,
    moduleInfo: ModuleViewModel.LocalModuleInfo
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

    ValueItem(
        key = stringResource(id = R.string.view_module_version),
        value = local.versionDisplay
    )

    ValueItem(
        key = stringResource(id = R.string.view_module_module_directory),
        value = moduleInfo.modulePath
    )

    if (moduleInfo.lastModified != null) {
        ValueItem(
            key = stringResource(id = R.string.view_module_last_modified),
            value = moduleInfo.lastModified
        )
    }

    if (moduleInfo.dirSize != null) {
        ValueItem(
            key = stringResource(id = R.string.view_module_dir_size),
            value = moduleInfo.dirSize
        )
    }
}

@Composable
private fun ValueItem(
    key: String,
    value: String,
)  = Column(
    modifier = Modifier.fillMaxWidth(),
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