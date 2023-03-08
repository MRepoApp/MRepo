package com.sanmer.mrepo.ui.navigation.graph

import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.sanmer.mrepo.ui.navigation.MainGraph
import com.sanmer.mrepo.ui.screens.home.HomeScreen

sealed class HomeGraph(val route: String) {
    object Home : HomeGraph("home")
}

fun NavGraphBuilder.homeGraph(
    //navController: NavController
) = navigation(
    startDestination = HomeGraph.Home.route,
    route = MainGraph.Home.route
) {
    composable(HomeGraph.Home.route) {
        HomeScreen()
    }
}