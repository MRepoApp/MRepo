package com.sanmer.mrepo.ui.navigation.graphs

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.sanmer.mrepo.model.online.OnlineModule
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
                slideInRightToLeft() + fadeIn()
            } else {
               fadeIn()
            }
        },
        exitTransition = {
            if (targetState.destination.route in subScreens) {
                slideOutLeftToRight() +  fadeOut()
            } else {
                fadeOut()
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
        enterTransition = { slideInLeftToRight() + fadeIn() },
        exitTransition = { slideOutRightToLeft() + fadeOut() }
    ) {
        ViewModuleScreen(
            navController = navController
        )
    }
}