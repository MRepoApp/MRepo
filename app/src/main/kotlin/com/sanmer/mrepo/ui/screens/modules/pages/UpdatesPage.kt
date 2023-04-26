package com.sanmer.mrepo.ui.screens.modules.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.app.isSucceeded
import com.sanmer.mrepo.database.entity.Repo
import com.sanmer.mrepo.model.module.OnlineModule
import com.sanmer.mrepo.ui.component.ModuleCard
import com.sanmer.mrepo.ui.component.PageIndicator
import com.sanmer.mrepo.ui.navigation.graph.ModulesGraph.View.toRoute
import com.sanmer.mrepo.ui.utils.fabPadding
import com.sanmer.mrepo.ui.utils.navigatePopUpTo
import com.sanmer.mrepo.viewmodel.ModulesViewModel

@Composable
fun UpdatesPage(
    viewModel: ModulesViewModel = hiltViewModel(),
    navController: NavController
) {
    val list = viewModel.updatableValue.sortedBy { it.name }

    if (list.isEmpty()) {
        PageIndicator(
            icon = R.drawable.directbox_receive_outline,
            text = if (viewModel.isSearch) R.string.modules_page_search_empty else R.string.modules_page_updates_empty
        )
    } else {
        ModulesList(
            list = list,
            navController = navController
        )
    }
}

@Composable
private fun ModulesList(
    list: List<OnlineModule>,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = fabPadding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(
            items = list,
            key = { it.id }
        ) { module ->
            OnlineModuleItem(module = module) {
                navController.navigatePopUpTo(module.id.toRoute())
            }
        }
    }
}

@Composable
private fun OnlineModuleItem(
    viewModel: ModulesViewModel = hiltViewModel(),
    module: OnlineModule,
    onView: () -> Unit
) {
    val suState by viewModel.suState.collectAsStateWithLifecycle()
    val progress by viewModel.getProgress(module).collectAsStateWithLifecycle(0f)

    var repo: Repo? by remember { mutableStateOf(null) }
    viewModel.getRepoByUrl(module.repoUrl) { repo = it }

    var update by remember { mutableStateOf(false) }
    if (update) UpdateDialog(value = module, onView = onView) { update = false }

    ModuleCard(
        name = module.name,
        version = module.version,
        author = module.author,
        description = module.description,
        progress = progress,
        message = {
            repo?.let {
                Text(
                    text = stringResource(id = R.string.view_module_provided, it.name),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        buttons = {
            TextButton(
                onClick = { update = true },
                enabled = suState.isSucceeded
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
    )
}

@Composable
private fun UpdateDialog(
    viewModel: ModulesViewModel = hiltViewModel(),
    value: OnlineModule,
    onView: () -> Unit,
    onClose: () -> Unit
) = AlertDialog(
    onDismissRequest = onClose
) {
    val context = LocalContext.current

    Surface(
        shape = RoundedCornerShape(25.dp),
        color = AlertDialogDefaults.containerColor,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(
            modifier = Modifier.padding(all = 24.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = value.versionDisplay,
                color = AlertDialogDefaults.titleContentColor,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                modifier = Modifier.padding(bottom = 24.dp),
                text = stringResource(id = R.string.modules_version_dialog_desc,
                    stringResource(id = R.string.module_update).toLowerCase(Locale.current)),
                color = AlertDialogDefaults.textContentColor,
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        onView()
                        onClose()
                    }
                ) {
                    Text(text = stringResource(id = R.string.module_view))
                }

                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = {
                        viewModel.downloader(
                            context = context,
                            module = value
                        )
                        onClose()
                    }
                ) {
                    Text(text = stringResource(id = R.string.module_download))
                }

                TextButton(
                    onClick = {
                        viewModel.downloader(
                            context = context,
                            module = value,
                            install = true
                        )
                        onClose()
                    }
                ) {
                    Text(text = stringResource(id = R.string.module_update))
                }
            }
        }
    }
}
