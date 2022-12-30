package com.sanmer.mrepo.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.sanmer.mrepo.ui.page.home.HomeScreen

sealed class HomeGraph(val route: String) {
    object Home : HomeGraph("home")
}

fun NavGraphBuilder.homeGraph(
    navController: NavController
) {
    navigation(
        startDestination = HomeGraph.Home.route,
        route = MainGraph.Home.route,
        enterTransition = { scaleIn(animationSpec = tween(600)) },
        exitTransition = { scaleOut(animationSpec = tween(600)) }
    ) {
        composable(HomeGraph.Home.route) {
            HomeScreen(
                navController = navController
            )
        }
    }
}