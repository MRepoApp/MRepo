package com.sanmer.mrepo.ui.navigation.animated

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.sanmer.mrepo.model.module.LocalModule
import com.sanmer.mrepo.ui.animate.slideInLeftToRight
import com.sanmer.mrepo.ui.animate.slideInRightToLeft
import com.sanmer.mrepo.ui.animate.slideOutLeftToRight
import com.sanmer.mrepo.ui.animate.slideOutRightToLeft
import com.sanmer.mrepo.ui.navigation.MainScreen
import com.sanmer.mrepo.ui.screens.modules.ModulesScreen
import com.sanmer.mrepo.ui.screens.repository.viewmodule.ViewModuleScreen

enum class ModulesScreen(val route: String) {
    Home("Modules"),
    View("View/{moduleId}")
}

fun createViewRoute(module: LocalModule) = "View/${module.id}"

private val subScreens = listOf(
    ModulesScreen.View.route
)

fun NavGraphBuilder.modulesScreen(
    navController: NavController
) = navigation(
    startDestination = ModulesScreen.Home.route,
    route = MainScreen.Modules.route
) {
    composable(
        route = ModulesScreen.Home.route,
        enterTransition = {
            if (initialState.destination.route in subScreens) {
                slideInRightToLeft()
            } else {
                null
            }
        },
        exitTransition = {
            if (initialState.destination.route in subScreens) {
                slideOutLeftToRight()
            } else {
                null
            }
        }
    ) {
        ModulesScreen(
            navController = navController
        )
    }

    composable(
        route = ModulesScreen.View.route,
        arguments = listOf(navArgument("moduleId") { type = NavType.StringType }),
        enterTransition = { slideInLeftToRight() },
        exitTransition = { slideOutRightToLeft() }
    ) {
        ViewModuleScreen(
            navController = navController
        )
    }
}