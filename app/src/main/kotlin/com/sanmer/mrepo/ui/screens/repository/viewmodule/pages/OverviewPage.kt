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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.event.isSucceeded
import com.sanmer.mrepo.viewmodel.ModuleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun OverviewPage(
    viewModel: ModuleViewModel = hiltViewModel()
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
            text = viewModel.online.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (viewModel.installed) {
        Divider(thickness = 0.9.dp)
        LocalItem()
    }

    Divider(thickness = 0.9.dp)
}

@Composable
private fun LocalItem(
    viewModel: ModuleViewModel = hiltViewModel()
) {
    val suState by viewModel.suState.collectAsStateWithLifecycle()

    Column(
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
            value = viewModel.local.versionDisplay
        )

        ValueItem(
            key = stringResource(id = R.string.view_module_module_directory),
            value = viewModel.modulePath
        )

        if (suState.isSucceeded) {
            val lastModified = viewModel.getLastModified()
            if (lastModified != null) {
                ValueItem(
                    key = stringResource(id = R.string.view_module_last_modified),
                    value = lastModified
                )
            }

            var dirSize: String? by remember { mutableStateOf(null) }
            LaunchedEffect(suState) {
                launch(Dispatchers.IO) {
                    dirSize = viewModel.getDirSize()
                }
            }

            if (dirSize != null) {
                ValueItem(
                    key = stringResource(id = R.string.view_module_dir_size),
                    value = dirSize!!
                )
            }
        }
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