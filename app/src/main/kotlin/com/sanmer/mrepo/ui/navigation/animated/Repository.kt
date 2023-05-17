package com.sanmer.mrepo.ui.navigation.animated

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.sanmer.mrepo.model.module.OnlineModule
import com.sanmer.mrepo.ui.animate.slideInLeftToRight
import com.sanmer.mrepo.ui.animate.slideInRightToLeft
import com.sanmer.mrepo.ui.animate.slideOutLeftToRight
import com.sanmer.mrepo.ui.animate.slideOutRightToLeft
import com.sanmer.mrepo.ui.navigation.MainScreen
import com.sanmer.mrepo.ui.screens.repository.RepositoryScreen
import com.sanmer.mrepo.ui.screens.repository.viewmodule.ViewModuleScreen

enum class RepositoryScreen(val route: String) {
    Home("Repository"),
    View("View/{moduleId}")
}

fun createViewRoute(module: OnlineModule) = "View/${module.id}"

private val subScreens = listOf(
    RepositoryScreen.View.route
)

fun NavGraphBuilder.repositoryScreen(
    navController: NavController
) = navigation(
    startDestination = RepositoryScreen.Home.route,
    route = MainScreen.Repository.route
) {
    composable(
        route = RepositoryScreen.Home.route,
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
        RepositoryScreen(
            navController = navController
        )
    }

    composable(
        route = RepositoryScreen.View.route,
        arguments = listOf(navArgument("moduleId") { type = NavType.StringType }),
        enterTransition = { slideInLeftToRight() },
        exitTransition = { slideOutRightToLeft() }
    ) {
        ViewModuleScreen(
            navController = navController
        )
    }
}