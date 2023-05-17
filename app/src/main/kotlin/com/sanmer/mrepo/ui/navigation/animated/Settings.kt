package com.sanmer.mrepo.ui.navigation.animated

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.sanmer.mrepo.ui.animate.slideInLeftToRight
import com.sanmer.mrepo.ui.animate.slideInRightToLeft
import com.sanmer.mrepo.ui.animate.slideOutLeftToRight
import com.sanmer.mrepo.ui.animate.slideOutRightToLeft
import com.sanmer.mrepo.ui.navigation.MainScreen
import com.sanmer.mrepo.ui.screens.settings.SettingsScreen
import com.sanmer.mrepo.ui.screens.settings.about.AboutScreen
import com.sanmer.mrepo.ui.screens.settings.app.AppScreen
import com.sanmer.mrepo.ui.screens.settings.repositories.RepositoriesScreen
import com.sanmer.mrepo.ui.screens.settings.workingmode.WorkingModeScreen

enum class SettingsScreen(val route: String) {
    Home("Settings"),
    Repositories("Repositories"),
    App("App"),
    WorkingMode("WorkingMode"),
    About("About")
}

private val subScreens = listOf(
    SettingsScreen.Repositories.route,
    SettingsScreen.App.route,
    SettingsScreen.WorkingMode.route,
    SettingsScreen.About.route
)

fun NavGraphBuilder.settingsScreen(
    navController: NavController
) = navigation(
    startDestination = SettingsScreen.Home.route,
    route = MainScreen.Settings.route
) {
    composable(
        route = SettingsScreen.Home.route,
        enterTransition = {
            if (initialState.destination.route in subScreens) {
                slideInLeftToRight()
            } else {
                null
            }
        },
        exitTransition = {
            if (initialState.destination.route in subScreens) {
                slideOutRightToLeft()
            } else {
                null
            }
        }
    ) {
        SettingsScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsScreen.Repositories.route,
        enterTransition = { slideInRightToLeft() },
        exitTransition = { slideOutLeftToRight() }
    ) {
        RepositoriesScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsScreen.App.route,
        enterTransition = { slideInRightToLeft() },
        exitTransition = { slideOutLeftToRight() }
    ) {
        AppScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsScreen.WorkingMode.route,
        enterTransition = { slideInRightToLeft() },
        exitTransition = { slideOutLeftToRight() }
    ) {
        WorkingModeScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsScreen.About.route,
        enterTransition = { slideInRightToLeft() },
        exitTransition = { slideOutLeftToRight() }
    ) {
        AboutScreen(
            navController = navController
        )
    }
}