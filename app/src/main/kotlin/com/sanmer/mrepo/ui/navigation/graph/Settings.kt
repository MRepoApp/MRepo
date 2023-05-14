package com.sanmer.mrepo.ui.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.sanmer.mrepo.ui.animate.slideInLeftToRight
import com.sanmer.mrepo.ui.animate.slideInRightToLeft
import com.sanmer.mrepo.ui.animate.slideOutLeftToRight
import com.sanmer.mrepo.ui.animate.slideOutRightToLeft
import com.sanmer.mrepo.ui.navigation.MainGraph
import com.sanmer.mrepo.ui.screens.settings.SettingsScreen
import com.sanmer.mrepo.ui.screens.settings.about.AboutScreen
import com.sanmer.mrepo.ui.screens.settings.app.AppScreen
import com.sanmer.mrepo.ui.screens.settings.repositories.RepositoriesScreen
import com.sanmer.mrepo.ui.screens.settings.workingmode.WorkingModeScreen

enum class SettingsGraph(val route: String) {
    Settings("Settings"),

    Repo("Repositories"),

    App("App"),

    WorkingMode("WorkingMode"),

    About("About")
}

private val subScreens = listOf(
    SettingsGraph.Repo.route,
    SettingsGraph.App.route,
    SettingsGraph.WorkingMode.route,
    SettingsGraph.About.route
)

fun NavGraphBuilder.settingsGraph(
    navController: NavController
) = navigation(
    startDestination = SettingsGraph.Settings.route,
    route = MainGraph.Settings.route
) {
    composable(
        route = SettingsGraph.Settings.route,
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
        route = SettingsGraph.Repo.route,
        enterTransition = { slideInRightToLeft() },
        exitTransition = { slideOutLeftToRight() }
    ) {
        RepositoriesScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsGraph.App.route,
        enterTransition = { slideInRightToLeft() },
        exitTransition = { slideOutLeftToRight() }
    ) {
        AppScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsGraph.WorkingMode.route,
        enterTransition = { slideInRightToLeft() },
        exitTransition = { slideOutLeftToRight() }
    ) {
        WorkingModeScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsGraph.About.route,
        enterTransition = { slideInRightToLeft() },
        exitTransition = { slideOutLeftToRight() }
    ) {
        AboutScreen(
            navController = navController
        )
    }
}