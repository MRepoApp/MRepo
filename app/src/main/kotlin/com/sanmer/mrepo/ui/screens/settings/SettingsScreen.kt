package com.sanmer.mrepo.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import com.sanmer.mrepo.ui.activity.log.LogActivity
import com.sanmer.mrepo.ui.component.SettingNormalItem
import com.sanmer.mrepo.ui.component.TopAppBarTitle
import com.sanmer.mrepo.ui.navigation.graphs.SettingsScreen
import com.sanmer.mrepo.ui.providable.LocalUserPreferences
import com.sanmer.mrepo.ui.screens.settings.items.NonRootItem
import com.sanmer.mrepo.ui.screens.settings.items.RootItem
import com.sanmer.mrepo.ui.utils.navigateSingleTopTo
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userPreferences = LocalUserPreferences.current
    val context = LocalContext.current
    val suState by viewModel.suState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                isRoot = userPreferences.isRoot,
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
                userPreferences.isRoot -> RootItem(
                    suState = suState,
                    apiVersion = viewModel.apiVersion
                )
                userPreferences.isNonRoot -> NonRootItem()
            }

            SettingNormalItem(
                icon = R.drawable.health_outline,
                text = stringResource(id = R.string.settings_log_viewer),
                subText = stringResource(id = R.string.settings_log_viewer_desc),
                onClick = {
                    LogActivity.start(context)
                }
            )

            SettingNormalItem(
                icon = R.drawable.hierarchy_outline,
                text = stringResource(id = R.string.settings_repo),
                subText = stringResource(id = R.string.settings_repo_desc),
                onClick = {
                    navController.navigateSingleTopTo(SettingsScreen.Repositories.route)
                }
            )

            SettingNormalItem(
                icon = R.drawable.layer_outline,
                text = stringResource(id = R.string.settings_app),
                subText = stringResource(id = R.string.settings_app_desc),
                onClick = {
                    navController.navigateSingleTopTo(SettingsScreen.App.route)
                }
            )

            SettingNormalItem(
                icon = R.drawable.main_component_outline,
                text = stringResource(id = R.string.settings_mode),
                subText = if (userPreferences.isRoot) {
                    stringResource(id = R.string.settings_mode_root)
                } else {
                    stringResource(id = R.string.settings_mode_non_root)
                },
                onClick = {
                    navController.navigateSingleTopTo(SettingsScreen.WorkingMode.route)
                }
            )

            SettingNormalItem(
                icon = R.drawable.ic_logo,
                text = stringResource(id = R.string.settings_about),
                subText = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                onClick = {
                    navController.navigateSingleTopTo(SettingsScreen.About.route)
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
        TopAppBarTitle(text = stringResource(id = R.string.page_settings))
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