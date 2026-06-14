package com.musicstudiosuite

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.musicstudiosuite.metronome.MetronomeScreen
import com.musicstudiosuite.tuner.TunerScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Metronome : Screen("metronome", "Metronome", Icons.Default.Schedule)
    object Tuner : Screen("tuner", "Tuner", Icons.Default.MusicNote)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(Screen.Metronome, Screen.Tuner)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) {
                                        saveState = true
                                    }
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Metronome.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Metronome.route) {
                MetronomeScreen()
            }
            composable(Screen.Tuner.route) {
                TunerScreen()
            }
        }
    }
}
