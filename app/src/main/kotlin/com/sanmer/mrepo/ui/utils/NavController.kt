package com.sanmer.mrepo.ui.utils

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptionsBuilder

fun NavController.navigateSingleTopTo(
    route: String,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    this.navigate(
        route = route,
    ) {
        launchSingleTop = true
        restoreState = true
        builder(this)
    }
}

fun NavController.navigatePopUpTo(
    route: String
) {
    navigateSingleTopTo(
        route = route,
    ) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
    }
}

fun NavController.navigateBack() {
    val route = currentBackStackEntry?.destination?.parent?.route
    if (route.isNullOrEmpty()) {
        navigateUp()
    } else {
        navigatePopUpTo(route)
    }
}