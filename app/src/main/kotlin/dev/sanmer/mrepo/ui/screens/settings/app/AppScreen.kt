package dev.sanmer.mrepo.ui.screens.settings.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.datastore.model.WorkingMode.Companion.isRoot
import dev.sanmer.mrepo.ui.component.NavigateUpTopBar
import dev.sanmer.mrepo.ui.component.SettingSwitchItem
import dev.sanmer.mrepo.ui.providable.LocalUserPreferences
import dev.sanmer.mrepo.ui.screens.settings.app.items.AppThemeItem
import dev.sanmer.mrepo.ui.screens.settings.app.items.DownloadPathItem
import dev.sanmer.mrepo.viewmodel.SettingsViewModel

@Composable
fun AppScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userPreferences = LocalUserPreferences.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior,
                navController = navController
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AppThemeItem(
                themeColor = userPreferences.themeColor,
                darkMode = userPreferences.darkMode,
                isDarkMode = userPreferences.isDarkMode(),
                onThemeColorChange = viewModel::setThemeColor,
                onDarkModeChange = viewModel::setDarkTheme
            )

            DownloadPathItem(
                downloadPath = userPreferences.downloadPath,
                onChange = viewModel::setDownloadPath
            )

            SettingSwitchItem(
                icon = R.drawable.file_type_zip,
                title = stringResource(id = R.string.settings_delete_zip),
                desc = stringResource(id = R.string.settings_delete_zip_desc),
                checked = userPreferences.deleteZipFile,
                onChange = viewModel::setDeleteZipFile,
                enabled = userPreferences.workingMode.isRoot
            )
        }
    }
}

@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavController
) = NavigateUpTopBar(
    title = stringResource(id = R.string.settings_app),
    scrollBehavior = scrollBehavior,
    navController = navController
)