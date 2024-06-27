package dev.sanmer.mrepo.ui.screens.settings.workingmode

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.mrepo.R
import dev.sanmer.mrepo.datastore.model.WorkingMode
import dev.sanmer.mrepo.ui.component.NavigateUpTopBar
import dev.sanmer.mrepo.ui.providable.LocalUserPreferences
import dev.sanmer.mrepo.viewmodel.SettingsViewModel

@Composable
fun WorkingModeScreen(
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
                .padding(vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WorkingModeItem(
                title = stringResource(id = R.string.setup_root_title),
                desc = stringResource(id = R.string.setup_root_desc),
                selected = userPreferences.workingMode == WorkingMode.Superuser,
                onClick = { viewModel.setWorkingMode(WorkingMode.Superuser) }
            )

            WorkingModeItem(
                title = stringResource(id = R.string.setup_shizuku_title),
                desc = stringResource(id = R.string.setup_shizuku_desc),
                selected = userPreferences.workingMode == WorkingMode.Shizuku,
                onClick = { viewModel.setWorkingMode(WorkingMode.Shizuku) }
            )

            WorkingModeItem(
                title = stringResource(id = R.string.setup_non_root_title),
                desc = stringResource(id = R.string.setup_non_root_desc),
                selected = userPreferences.workingMode == WorkingMode.None,
                onClick = { viewModel.setWorkingMode(WorkingMode.None) }
            )
        }
    }
}

@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavController
) = NavigateUpTopBar(
    title = stringResource(id = R.string.setup_mode),
    scrollBehavior = scrollBehavior,
    navController = navController
)