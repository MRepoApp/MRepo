package com.sanmer.mrepo.ui.utils

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptionsBuilder

fun NavController.navigateSingleTopTo(
    route: String,
    builder: NavOptionsBuilder.() -> Unit = {}
) = navigate(
    route = route
) {
    launchSingleTop = true
    restoreState = true
    builder()
}

fun NavController.navigatePopUpTo(
    route: String,
    saveState: Boolean = true
) = navigateSingleTopTo(
    route = route
) {
    popUpTo(graph.findStartDestination().id) {
        this.saveState = saveState
    }
}

fun NavController.navigateBack() {
    val route = currentBackStackEntry?.destination?.parent?.route
    if (route == null) {
        navigateUp()
    } else {
        navigatePopUpTo(route, false)
    }
}