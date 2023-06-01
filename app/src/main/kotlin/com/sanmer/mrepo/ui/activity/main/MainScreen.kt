package com.sanmer.mrepo.ui.activity.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.ui.navigation.BottomNav
import com.sanmer.mrepo.ui.navigation.MainScreen
import com.sanmer.mrepo.ui.navigation.animated.modulesScreen as animatedModulesScreen
import com.sanmer.mrepo.ui.navigation.animated.repositoryScreen as animatedRepositoryScreen
import com.sanmer.mrepo.ui.navigation.animated.settingsScreen as animatedSettingsScreen
import com.sanmer.mrepo.ui.navigation.normal.modulesScreen as normalModulesScreen
import com.sanmer.mrepo.ui.navigation.normal.repositoryScreen as normalRepositoryScreen
import com.sanmer.mrepo.ui.navigation.normal.settingsScreen as normalSettingsScreen

@Composable
fun AnimatedMainScreen(
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
            startDestination = MainScreen.Repository.route
        ) {
            animatedRepositoryScreen(
                navController = navController
            )
            animatedModulesScreen(
                navController = navController
            )
            animatedSettingsScreen(
                navController = navController
            )
        }
    }
}

@Composable
fun NormalMainScreen(
    userData: UserData
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNav(
                navController = navController,
                isRoot = userData.isRoot
            )
        }
    ) {
        NavHost(
            modifier = Modifier.padding(bottom = it.calculateBottomPadding()),
            navController = navController,
            startDestination = MainScreen.Repository.route
        ) {
            normalRepositoryScreen(
                navController = navController
            )
            normalModulesScreen(
                navController = navController
            )
            normalSettingsScreen(
                navController = navController
            )
        }
    }
}
