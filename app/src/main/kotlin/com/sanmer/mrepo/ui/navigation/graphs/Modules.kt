package com.sanmer.mrepo.ui.navigation.graphs

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
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
                slideInRightToLeft() + fadeIn()
            } else {
                fadeIn()
            }
        },
        exitTransition = {
            if (targetState.destination.route in subScreens) {
                slideOutLeftToRight() + fadeOut()
            } else {
                fadeOut()
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
        enterTransition = { slideInLeftToRight() + fadeIn() },
        exitTransition = { slideOutRightToLeft() + fadeOut() }
    ) {
        ViewModuleScreen(
            navController = navController
        )
    }
}