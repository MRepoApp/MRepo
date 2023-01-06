package com.sanmer.mrepo.ui.page.modules

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.runtime.Status
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.OnlineModule
import com.sanmer.mrepo.data.module.State
import com.sanmer.mrepo.ui.component.ModuleCard
import com.sanmer.mrepo.ui.component.StateIndicator
import com.sanmer.mrepo.viewmodel.ModulesViewModel

@Composable
fun LocalModuleItem(
    viewModel: ModulesViewModel = viewModel(),
    module: LocalModule
) {
    val uiState = viewModel.getModuleUIState(module)

    ModuleCard(
        name = module.name,
        version = module.version,
        author = module.author,
        description = module.description,
        alpha = uiState.alpha,
        decoration = uiState.decoration,
        switch = {
            Switch(
                checked = module.state == State.ENABLE,
                onCheckedChange = {
                    if (Status.FileSystem.isSucceeded) {
                        uiState.onChecked(it)
                    }
                }
            )
        },
        cover = {
            when (module.state) {
                State.REMOVE -> {
                    StateIndicator(R.drawable.trash_outline)
                }
                State.UPDATE  -> {
                    StateIndicator(R.drawable.import_outline)
                }
                State.ZYGISK_UNLOADED,
                State.RIRU_DISABLE,
                State.ZYGISK_DISABLE -> {
                    StateIndicator(R.drawable.danger_outline)
                }
                else -> {}
            }
        }
    ) {
        TextButton(
            onClick = uiState.onClick,
            enabled = Status.FileSystem.isSucceeded
        ) {
            Text(
                modifier = Modifier
                    .padding(end = 6.dp),
                text = stringResource(id = if (module.state == State.REMOVE) {
                    R.string.module_restore
                }else {
                    R.string.module_remove
                })
            )
            Icon(
                modifier = Modifier
                    .size(22.dp),
                painter = painterResource(id = if (module.state == State.REMOVE) {
                    R.drawable.refresh_outline
                } else {
                    R.drawable.trash_outline
                }),
                contentDescription = null
            )
        }
    }
}

@Composable
fun OnlineModuleItem(
    viewModel: ModulesViewModel = viewModel(),
    update: Boolean,
    module: OnlineModule
) {
    val owner = LocalLifecycleOwner.current
    val context = LocalContext.current

    var progress by remember { mutableStateOf(0f) }
    viewModel.observeProgress(owner, module) { progress = it }

    ModuleCard(
        name = module.name,
        version = module.version,
        author = module.author,
        description = module.description,
        progress = progress
    ) {
        if (!update)  {
            TextButton(
                onClick = {
                    viewModel.downloader(
                        context = context,
                        module = module
                    )
                },
                enabled = Status.FileSystem.isSucceeded
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 6.dp),
                    text = stringResource(id = R.string.module_download)
                )
                Icon(
                    modifier = Modifier
                        .size(22.dp),
                    painter = painterResource(id = R.drawable.link_outline),
                    contentDescription = null
                )
            }
        }

        TextButton(
            onClick = {
                viewModel.installer(
                    context = context,
                    module = module
                )
            },
            enabled = Status.FileSystem.isSucceeded
        ) {
            Text(
                modifier = Modifier
                    .padding(end = 6.dp),
                text = stringResource(id =
                if (update) {
                    R.string.module_update
                } else {
                    R.string.module_install
                })
            )
            Icon(
                modifier = Modifier
                    .size(22.dp),
                painter = painterResource(id = R.drawable.import_outline),
                contentDescription = null
            )
        }
    }
}
