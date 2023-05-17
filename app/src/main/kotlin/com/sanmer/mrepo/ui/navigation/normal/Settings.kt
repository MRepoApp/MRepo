package com.sanmer.mrepo.ui.navigation.normal

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.sanmer.mrepo.ui.navigation.MainScreen
import com.sanmer.mrepo.ui.navigation.animated.SettingsScreen
import com.sanmer.mrepo.ui.screens.settings.SettingsScreen
import com.sanmer.mrepo.ui.screens.settings.about.AboutScreen
import com.sanmer.mrepo.ui.screens.settings.app.AppScreen
import com.sanmer.mrepo.ui.screens.settings.repositories.RepositoriesScreen
import com.sanmer.mrepo.ui.screens.settings.workingmode.WorkingModeScreen

fun NavGraphBuilder.settingsScreen(
    navController: NavController
) = navigation(
    startDestination = SettingsScreen.Home.route,
    route = MainScreen.Settings.route
) {
    composable(
        route = SettingsScreen.Home.route
    ) {
        SettingsScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsScreen.Repositories.route
    ) {
        RepositoriesScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsScreen.App.route
    ) {
        AppScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsScreen.WorkingMode.route
    ) {
        WorkingModeScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsScreen.About.route
    ) {
        AboutScreen(
            navController = navController
        )
    }
}