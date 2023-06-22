package com.sanmer.mrepo.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sanmer.mrepo.BuildConfig
import com.sanmer.mrepo.R
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.ui.activity.log.LogActivity
import com.sanmer.mrepo.ui.component.SettingNormalItem
import com.sanmer.mrepo.ui.navigation.graphs.SettingsScreen
import com.sanmer.mrepo.ui.navigation.navigateToRepository
import com.sanmer.mrepo.ui.screens.settings.items.NonRootItem
import com.sanmer.mrepo.ui.screens.settings.items.RootItem
import com.sanmer.mrepo.ui.utils.navigatePopUpTo
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val suState by viewModel.suState.collectAsStateWithLifecycle()
    val userData by viewModel.userData.collectAsStateWithLifecycle(UserData.default())

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler { navController.navigateToRepository() }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                isRoot = userData.isRoot,
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            when {
                userData.isRoot -> RootItem(
                    suState = suState,
                    apiVersion = viewModel.apiVersion
                )
                userData.isNonRoot -> NonRootItem()
            }

            SettingNormalItem(
                iconRes = R.drawable.health_outline,
                text = stringResource(id = R.string.settings_log_viewer),
                subText = stringResource(id = R.string.settings_log_viewer_desc),
                onClick = {
                    LogActivity.start(context)
                }
            )

            SettingNormalItem(
                iconRes = R.drawable.hierarchy_outline,
                text = stringResource(id = R.string.settings_repo),
                subText = stringResource(id = R.string.settings_repo_desc),
                onClick = {
                    navController.navigatePopUpTo(SettingsScreen.Repositories.route)
                }
            )

            SettingNormalItem(
                iconRes = R.drawable.layer_outline,
                text = stringResource(id = R.string.settings_app),
                subText = stringResource(id = R.string.settings_app_desc),
                onClick = {
                    navController.navigatePopUpTo(SettingsScreen.App.route)
                }
            )

            SettingNormalItem(
                iconRes = R.drawable.main_component_outline,
                text = stringResource(id = R.string.settings_mode),
                subText = if (userData.isRoot) {
                    stringResource(id = R.string.settings_mode_root)
                } else {
                    stringResource(id = R.string.settings_mode_non_root)
                },
                onClick = {
                    navController.navigatePopUpTo(SettingsScreen.WorkingMode.route)
                }
            )

            SettingNormalItem(
                iconRes = R.drawable.ic_logo,
                text = stringResource(id = R.string.settings_about),
                subText = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                onClick = {
                    navController.navigatePopUpTo(SettingsScreen.About.route)
                }
            )
        }
    }
}

@Composable
private fun TopBar(
    isRoot: Boolean,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = {
        Text(text = stringResource(id = R.string.page_settings))
    },
    actions = {
        var expanded by remember { mutableStateOf(false) }

        IconButton(
            onClick = { expanded = true },
            enabled = isRoot
        ) {
            Icon(
                painter = painterResource(id = R.drawable.refresh_outline),
                contentDescription = null
            )

            SettingsMenu(
                expanded = expanded,
                onClose = { expanded = false }
            )
        }
    },
    scrollBehavior = scrollBehavior
)