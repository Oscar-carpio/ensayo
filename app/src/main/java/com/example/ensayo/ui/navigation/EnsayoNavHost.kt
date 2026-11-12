package com.example.ensayo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ensayo.ui.cancion.CancionScreen
import com.example.ensayo.ui.editor.EditorScreen
import com.example.ensayo.ui.repertorio.RepertorioScreen
import kotlinx.serialization.Serializable

@Serializable
object RepertorioRoute

@Serializable
data class CancionRoute(val id: String)

@Serializable
data class EditorRoute(val id: String? = null)

@Composable
fun EnsayoNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = RepertorioRoute,
        modifier = modifier
    ) {
        composable<RepertorioRoute> {
            RepertorioScreen(
                onSongClick = { id -> navController.navigate(CancionRoute(id)) },
                onAddClick = { navController.navigate(EditorRoute()) }
            )
        }
        composable<CancionRoute> {
            CancionScreen(
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(EditorRoute(id)) }
            )
        }
        composable<EditorRoute> {
            EditorScreen(onDone = { navController.popBackStack() })
        }
    }
}
