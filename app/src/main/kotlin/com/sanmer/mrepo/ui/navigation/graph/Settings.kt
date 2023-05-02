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
import com.sanmer.mrepo.ui.screens.apptheme.AppThemeScreen
import com.sanmer.mrepo.ui.screens.repository.RepositoryScreen
import com.sanmer.mrepo.ui.screens.settings.SettingsScreen

sealed class SettingsGraph(val route: String) {
    object Settings : SettingsGraph("settings")
    object AppTheme : SettingsGraph("appTheme")
    object Repo : SettingsGraph("repo")
}

fun NavGraphBuilder.settingsGraph(
    navController: NavController
) = navigation(
    startDestination = SettingsGraph.Settings.route,
    route = MainGraph.Settings.route
) {
    composable(
        route = SettingsGraph.Settings.route,
        enterTransition = {
            when (initialState.destination.route) {
                SettingsGraph.AppTheme.route,
                SettingsGraph.Repo.route -> slideInLeftToRight()
                else -> null
            }
        },
        exitTransition = {
            when (initialState.destination.route) {
                SettingsGraph.AppTheme.route,
                SettingsGraph.Repo.route -> slideOutRightToLeft()
                else -> null
            }
        }
    ) {
        SettingsScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsGraph.AppTheme.route,
        enterTransition = { slideInRightToLeft() },
        exitTransition = { slideOutLeftToRight() }
    ) {
        AppThemeScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsGraph.Repo.route,
        enterTransition = { slideInRightToLeft() },
        exitTransition = { slideOutLeftToRight() }
    ) {
        RepositoryScreen(
            navController = navController
        )
    }
}