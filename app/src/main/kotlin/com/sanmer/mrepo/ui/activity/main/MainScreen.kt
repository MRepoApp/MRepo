package com.sanmer.mrepo.ui.activity.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.ui.navigation.BottomNav
import com.sanmer.mrepo.ui.navigation.MainScreen
import com.sanmer.mrepo.ui.navigation.graph.modulesScreen
import com.sanmer.mrepo.ui.navigation.graph.repositoryScreen
import com.sanmer.mrepo.ui.navigation.graph.settingsScreen

@Composable
fun MainScreen(
    userData: UserData
) {
    val navController = rememberAnimatedNavController()

    Scaffold(
        bottomBar = {
            BottomNav(
                navController = navController,
                isRoot = userData.isRoot
            )
        }
    ) {
        AnimatedNavHost(
            modifier = Modifier.padding(bottom = it.calculateBottomPadding()),
            navController = navController,
            startDestination = MainScreen.Repository.route,
            enterTransition = { fadeIn(animationSpec = tween(400)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            repositoryScreen(
                navController = navController
            )
            modulesScreen(
                navController = navController
            )
            settingsScreen(
                navController = navController
            )
        }
    }
}
