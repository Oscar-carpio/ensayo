package com.example.ensayo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ensayo.ui.repertorio.RepertorioScreen
import kotlinx.serialization.Serializable

@Serializable
object RepertorioRoute

@Composable
fun EnsayoNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = RepertorioRoute,
        modifier = modifier
    ) {
        composable<RepertorioRoute> { RepertorioScreen() }
    }
}
