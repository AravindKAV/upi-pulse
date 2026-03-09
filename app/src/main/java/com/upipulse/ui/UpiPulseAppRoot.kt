package com.upipulse.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.upipulse.ui.navigation.TopLevelDestination
import com.upipulse.ui.screens.dashboard.DashboardScreen
import com.upipulse.ui.screens.settings.SettingsScreen
import com.upipulse.ui.screens.timeline.TimelineScreen

@Composable
fun UpiPulseAppRoot() {
    val navController = rememberNavController()
    val destinations = TopLevelDestination.entries
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.DASHBOARD.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(TopLevelDestination.DASHBOARD.route) {
                DashboardScreen()
            }
            composable(TopLevelDestination.TIMELINE.route) {
                TimelineScreen()
            }
            composable(TopLevelDestination.SETTINGS.route) {
                SettingsScreen()
            }
        }
    }
}