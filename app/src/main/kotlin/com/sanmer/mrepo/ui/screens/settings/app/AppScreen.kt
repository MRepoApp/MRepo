package com.sanmer.mrepo.ui.screens.settings.app

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.ui.component.NavigateUpTopBar
import com.sanmer.mrepo.ui.component.SettingSwitchItem
import com.sanmer.mrepo.ui.utils.navigateBack
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.viewmodel.SettingsViewModel

@Composable
fun AppScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userData by viewModel.userData.collectAsStateWithLifecycle(UserData.default())
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler { navController.navigateBack() }

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
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AppThemeItem(
                userData = userData,
                onThemeColorChange = {
                    viewModel.setThemeColor(it)
                },
                onDarkModeChange = {
                    viewModel.setDarkTheme(it)
                }
            )

            DownloadPathItem(
                userData = userData,
                onChange = {
                    viewModel.setDownloadPath(it)
                }
            )

            SettingSwitchItem(
                iconRes = R.drawable.box_remove_outline,
                text = stringResource(id = R.string.settings_delete_zip),
                subText = stringResource(id = R.string.settings_delete_zip_desc),
                checked = userData.deleteZipFile,
                onChange = {
                    viewModel.setDeleteZipFile(it)
                }
            )

            SettingSwitchItem(
                iconRes = R.drawable.convertshape_outline,
                text = stringResource(id = R.string.settings_navigation_animation),
                subText = stringResource(id = R.string.settings_navigation_animation_desc),
                checked = userData.enableNavigationAnimation,
                onChange = {
                    viewModel.setEnableNavigationAnimation(it)
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
    title = R.string.settings_app,
    scrollBehavior = scrollBehavior,
    navController = navController
)