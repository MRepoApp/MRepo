package com.sanmer.mrepo.ui.screens.modules.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.data.module.LocalModule
import com.sanmer.mrepo.data.module.State
import com.sanmer.mrepo.ui.component.ModuleCard
import com.sanmer.mrepo.ui.component.PageIndicator
import com.sanmer.mrepo.ui.component.stateIndicator
import com.sanmer.mrepo.ui.screens.modules.InstallItem
import com.sanmer.mrepo.viewmodel.ModulesViewModel

@Composable
fun InstalledPage(
    viewModel: ModulesViewModel = viewModel(),
) {
    val list = viewModel.getLocal()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ModulesList(
            list = list
        )

        if (list.isEmpty()) {
            PageIndicator(
                icon = R.drawable.mobile_outline,
                text = if (viewModel.isSearch) R.string.modules_page_search_empty else R.string.modules_page_installed_empty,
            )
        }
    }
}

@Composable
private fun ModulesList(
    viewModel: ModulesViewModel = viewModel(),
    list: List<LocalModule>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(all = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        if (!viewModel.isSearch) {
            item {
                InstallItem()
            }
        }

        if (list.isNotEmpty()) {
            items(
                items = list,
                key = { it.id }
            ) { module ->
                LocalModuleItem(module = module)
            }
        }
    }
}

@Composable
private fun LocalModuleItem(
    viewModel: ModulesViewModel = viewModel(),
    module: LocalModule
) {
    val state = viewModel.updateModuleState(module)

    ModuleCard(
        name = module.name,
        version = module.version,
        author = module.author,
        description = module.description,
        alpha = state.alpha,
        decoration = state.decoration,
        switch = {
            Switch(
                checked = module.state == State.ENABLE,
                onCheckedChange = {
                    if (Status.Provider.isSucceeded) {
                        state.onChecked(it)
                    }
                }
            )
        },
        indicator = when (module.state) {
            State.REMOVE -> {
                stateIndicator(R.drawable.trash_outline)
            }
            State.UPDATE  -> {
                stateIndicator(R.drawable.import_outline)
            }
            State.ZYGISK_UNLOADED,
            State.RIRU_DISABLE,
            State.ZYGISK_DISABLE -> {
                stateIndicator(R.drawable.danger_outline)
            }
            else -> null
        }
    ) {
        TextButton(
            onClick = state.onClick,
            enabled = Status.Provider.isSucceeded
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