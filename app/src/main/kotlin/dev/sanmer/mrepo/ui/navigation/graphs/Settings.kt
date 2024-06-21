package dev.sanmer.mrepo.ui.navigation.graphs

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.sanmer.mrepo.ui.navigation.MainScreen
import dev.sanmer.mrepo.ui.screens.settings.SettingsScreen
import dev.sanmer.mrepo.ui.screens.settings.about.AboutScreen
import dev.sanmer.mrepo.ui.screens.settings.app.AppScreen
import dev.sanmer.mrepo.ui.screens.settings.repositories.RepositoriesScreen
import dev.sanmer.mrepo.ui.screens.settings.workingmode.WorkingModeScreen

enum class SettingsScreen(val route: String) {
    Home("Settings"),
    Repositories("Repositories"),
    App("App"),
    WorkingMode("WorkingMode"),
    About("About")
}

fun NavGraphBuilder.settingsScreen(
    navController: NavController
) = navigation(
    startDestination = SettingsScreen.Home.route,
    route = MainScreen.Settings.route
) {
    composable(
        route = SettingsScreen.Home.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        SettingsScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsScreen.Repositories.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        RepositoriesScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsScreen.App.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        AppScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsScreen.WorkingMode.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        WorkingModeScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsScreen.About.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        AboutScreen(
            navController = navController
        )
    }
}