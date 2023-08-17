package com.sanmer.mrepo.ui.screens.settings.workingmode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.sanmer.mrepo.R
import com.sanmer.mrepo.datastore.WorkingMode
import com.sanmer.mrepo.ui.component.NavigateUpTopBar
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.viewmodel.SettingsViewModel

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
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WorkingModeItem(
                title = stringResource(id = R.string.setup_root_title),
                desc1 = stringResource(id = R.string.setup_root_desc1),
                desc2 = stringResource(id = R.string.setup_root_desc2),
                selected = userPreferences.isRoot,
                onClick = {
                    viewModel.setWorkingMode(WorkingMode.MODE_ROOT)
                }
            )

            WorkingModeItem(
                title = stringResource(id = R.string.setup_non_root_title),
                desc1 = stringResource(id = R.string.setup_non_root_desc1),
                desc2 = stringResource(id = R.string.setup_non_root_desc2),
                selected = userPreferences.isNonRoot,
                onClick = {
                    viewModel.setWorkingMode(WorkingMode.MODE_NON_ROOT)
                }
            )
        }
    }
}

@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavController
) = NavigateUpTopBar(
    title = stringResource(id = R.string.settings_mode),
    scrollBehavior = scrollBehavior,
    navController = navController
)