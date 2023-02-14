package com.sanmer.mrepo.ui.screens.modules.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
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
import com.sanmer.mrepo.app.Status
import com.sanmer.mrepo.data.json.OnlineModule
import com.sanmer.mrepo.ui.component.ModuleCard
import com.sanmer.mrepo.ui.component.PageIndicator
import com.sanmer.mrepo.viewmodel.ModulesViewModel

@Composable
fun UpdatesPage(
    viewModel: ModulesViewModel = viewModel()
) {
    val list = viewModel.getUpdatable()

    if (list.isEmpty()) {
        PageIndicator(
            icon = R.drawable.directbox_receive_outline,
            text = if (viewModel.isSearch) R.string.modules_page_search_empty else R.string.modules_page_updates_empty
        )
    } else {
        ModulesList(
            list = list
        )
    }
}

@Composable
private fun ModulesList(
    list: List<OnlineModule>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(all = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(
            items = list,
            key = { it.id }
        ) { module ->
            OnlineModuleItem(module = module)
        }
    }
}

@Composable
private fun OnlineModuleItem(
    viewModel: ModulesViewModel = viewModel(),
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
        TextButton(
            onClick = {
                viewModel.installer(
                    context = context,
                    module = module
                )
            },
            enabled = Status.Provider.isSucceeded
        ) {
            Text(
                modifier = Modifier
                    .padding(end = 6.dp),
                text = stringResource(id = R.string.module_update)
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
