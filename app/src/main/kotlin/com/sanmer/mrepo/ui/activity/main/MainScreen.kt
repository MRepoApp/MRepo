package com.sanmer.mrepo.ui.activity.main

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.sanmer.mrepo.datastore.UserData
import com.sanmer.mrepo.ui.navigation.MainScreen
import com.sanmer.mrepo.ui.utils.navigatePopUpTo
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

@Composable
private fun BottomNav(
    navController: NavController,
    isRoot: Boolean
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val mainScreens by remember(isRoot) {
        derivedStateOf {
            if (isRoot) {
                listOf(MainScreen.Repository, MainScreen.Modules, MainScreen.Settings)
            } else {
                listOf(MainScreen.Repository, MainScreen.Settings)
            }
        }
    }

    NavigationBar(
        modifier = Modifier.imePadding()
    ) {
        mainScreens.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = if (selected) {
                            screen.iconSelected
                        } else {
                            screen.icon
                        }),
                        contentDescription = null,
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = screen.label),
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                alwaysShowLabel = true,
                selected = selected,
                onClick = { if (!selected) navController.navigatePopUpTo(screen.route) }
            )
        }
    }
}