package com.example.ensayo.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ensayo.ui.cancion.CancionScreen
import com.example.ensayo.ui.directo.DirectoScreen
import com.example.ensayo.ui.editor.EditorScreen
import com.example.ensayo.ui.repertorio.RepertorioScreen
import com.example.ensayo.ui.setlist.SetlistDetailScreen
import com.example.ensayo.ui.setlists.SetlistsScreen
import kotlinx.serialization.Serializable

@Serializable
object RepertorioRoute

@Serializable
object SetlistsRoute

@Serializable
data class SetlistDetailRoute(val id: String)

@Serializable
data class CancionRoute(val id: String)

@Serializable
data class EditorRoute(val id: String? = null)

@Serializable
data class DirectoRoute(val id: String, val semitones: Int = 0, val setlistId: String? = null)

@Composable
fun EnsayoNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val onRepertorio = destination?.hasRoute(RepertorioRoute::class) == true
    val onSetlists = destination?.hasRoute(SetlistsRoute::class) == true

    Scaffold(
        modifier = modifier,
        bottomBar = {
            // La barra solo se ve en las pantallas principales, no en ficha, editor ni directo
            if (onRepertorio || onSetlists) {
                NavigationBar {
                    NavigationBarItem(
                        selected = onRepertorio,
                        onClick = { navController.navigateToTab(RepertorioRoute) },
                        icon = { Text("🎵") },
                        label = { Text("Repertorio") }
                    )
                    NavigationBarItem(
                        selected = onSetlists,
                        onClick = { navController.navigateToTab(SetlistsRoute) },
                        icon = { Text("📋") },
                        label = { Text("Setlists") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = RepertorioRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<RepertorioRoute> {
                RepertorioScreen(
                    onSongClick = { id -> navController.navigate(CancionRoute(id)) },
                    onAddClick = { navController.navigate(EditorRoute()) }
                )
            }
            composable<SetlistsRoute> {
                SetlistsScreen(
                    onSetlistClick = { id -> navController.navigate(SetlistDetailRoute(id)) }
                )
            }
            composable<SetlistDetailRoute> {
                SetlistDetailScreen(
                    onBack = { navController.popBackStack() },
                    onSongClick = { id -> navController.navigate(CancionRoute(id)) },
                    onLiveMode = { setlistId, songId ->
                        navController.navigate(DirectoRoute(songId, setlistId = setlistId))
                    }
                )
            }
            composable<CancionRoute> {
                CancionScreen(
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(EditorRoute(id)) },
                    onLiveMode = { id, semitones ->
                        navController.navigate(DirectoRoute(id, semitones))
                    }
                )
            }
            composable<EditorRoute> {
                EditorScreen(onDone = { navController.popBackStack() })
            }
            composable<DirectoRoute> {
                DirectoScreen(onExit = { navController.popBackStack() })
            }
        }
    }
}

private fun NavHostController.navigateToTab(route: Any) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
