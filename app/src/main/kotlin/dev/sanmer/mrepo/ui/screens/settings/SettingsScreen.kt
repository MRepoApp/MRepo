package dev.sanmer.mrepo.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.mrepo.BuildConfig
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.datastore.model.WorkingMode
import dev.sanmer.mrepo.datastore.model.WorkingMode.Companion.isNonRoot
import dev.sanmer.mrepo.datastore.model.WorkingMode.Companion.isRoot
import dev.sanmer.mrepo.ui.component.SettingNormalItem
import dev.sanmer.mrepo.ui.component.TopAppBarTitle
import dev.sanmer.mrepo.ui.navigation.graphs.SettingsScreen
import dev.sanmer.mrepo.ui.providable.LocalUserPreferences
import dev.sanmer.mrepo.ui.screens.settings.items.NonRootItem
import dev.sanmer.mrepo.ui.screens.settings.items.RootItem
import dev.sanmer.mrepo.ui.utils.navigateSingleTopTo
import dev.sanmer.mrepo.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userPreferences = LocalUserPreferences.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            when {
                userPreferences.workingMode.isRoot -> RootItem(
                    isAlive = viewModel.isProviderAlive,
                    version = viewModel.version
                )
                userPreferences.workingMode.isNonRoot -> NonRootItem()
            }

            SettingNormalItem(
                icon = R.drawable.launcher_outline,
                title = stringResource(id = R.string.settings_app),
                desc = stringResource(id = R.string.settings_app_desc),
                onClick = { navController.navigateSingleTopTo(SettingsScreen.App.route) }
            )

            SettingNormalItem(
                icon = R.drawable.git_pull_request,
                title = stringResource(id = R.string.settings_repo),
                desc = stringResource(id = R.string.settings_repo_desc),
                onClick = { navController.navigateSingleTopTo(SettingsScreen.Repositories.route) }
            )

            SettingNormalItem(
                icon = R.drawable.command,
                title = stringResource(id = R.string.setup_mode),
                desc = stringResource(id = when (userPreferences.workingMode) {
                    WorkingMode.Superuser -> R.string.setup_root_title
                    WorkingMode.Shizuku -> R.string.setup_shizuku_title
                    WorkingMode.None -> R.string.setup_non_root_title
                    else -> R.string.settings_root_none
                }),
                onClick = { navController.navigateSingleTopTo(SettingsScreen.WorkingMode.route) }
            )

            SettingNormalItem(
                icon = R.drawable.award,
                title = stringResource(id = R.string.settings_about),
                desc = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                onClick = { navController.navigateSingleTopTo(SettingsScreen.About.route) }
            )
        }
    }
}

@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { TopAppBarTitle(text = stringResource(id = R.string.page_settings)) },
    scrollBehavior = scrollBehavior
)