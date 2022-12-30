package com.sanmer.mrepo.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.sanmer.mrepo.ui.animate.SlideIn
import com.sanmer.mrepo.ui.animate.SlideOut
import com.sanmer.mrepo.ui.page.apptheme.AppThemeScreen
import com.sanmer.mrepo.ui.page.settings.SettingsScreen

sealed class SettingsGraph(val route: String) {
    object Settings : SettingsGraph("settings")
    object AppTheme : SettingsGraph("appTheme")
}

fun NavGraphBuilder.settingsGraph(
    navController: NavController
) {
    navigation(
        startDestination = SettingsGraph.Settings.route,
        route = MainGraph.Settings.route
    ) {
        composable(
            route = SettingsGraph.Settings.route,
            enterTransition = {
                when (initialState.destination.route) {
                    SettingsGraph.AppTheme.route -> SlideIn.rightToLeft
                    else -> null
                }
            },
            exitTransition = {
                when (initialState.destination.route) {
                    SettingsGraph.AppTheme.route -> SlideOut.leftToRight
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
            enterTransition = { SlideIn.rightToLeft },
            exitTransition = { SlideOut.leftToRight }
        ) {
            AppThemeScreen(
                navController = navController
            )
        }
    }
}