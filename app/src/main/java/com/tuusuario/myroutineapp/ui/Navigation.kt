package com.tuusuario.myroutineapp.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tuusuario.myroutineapp.ui.screens.rutinas.RutinasScreen
import com.tuusuario.myroutineapp.ui.screens.rutinas.RutinaDetailScreen
import com.tuusuario.myroutineapp.ui.screens.ejercicios.EjerciciosScreen
import com.tuusuario.myroutineapp.ui.screens.ejercicios.EjercicioDetailScreen
import com.tuusuario.myroutineapp.ui.screens.metricas.MetricasScreen
import com.tuusuario.myroutineapp.ui.screens.notas.NotasScreen
import com.tuusuario.myroutineapp.ui.screens.inicio.InicioScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = "inicio") {
        composable("inicio") {
            InicioScreen(
                onRutinas = { navController.navigate("rutinas") },
                onEjercicios = { navController.navigate("ejercicios") },
                onMetricas = { navController.navigate("metricas") },
                onNotas = { navController.navigate("notas") },
                onPlayRutina = { rutinaId -> navController.navigate("playRutina/$rutinaId") }
            )
        }
        composable("metricas") {
            MetricasScreen()
        }
        composable("rutinas") {
            val viewModel = hiltViewModel<com.tuusuario.myroutineapp.ui.screens.rutinas.RutinasViewModel>()
            RutinasScreen(
                viewModel = viewModel,
                onRutinaClick = { rutina ->
                    navController.navigate("rutinaDetalle/${rutina.id}")
                },
                onNuevaRutina = {
                    navController.navigate("rutinaDetalle/0")
                },
                onPlayRutina = { rutina ->
                    navController.navigate("playRutina/${rutina.id}")
                },
                onEjercicios = {
                    navController.navigate("ejercicios")
                },
                onMetricas = {
                    navController.navigate("metricas")
                },
                onNotas = {
                    navController.navigate("notas")
                }
            )
        }
        composable("rutinaDetalle/{rutinaId}") { backStackEntry ->
            val rutinaId = backStackEntry.arguments?.getString("rutinaId")?.toLongOrNull() ?: 0L
            RutinaDetailScreen(rutinaId = rutinaId, navController = navController)
        }
        composable("playRutina/{rutinaId}") { backStackEntry ->
            val rutinaId = backStackEntry.arguments?.getString("rutinaId")?.toLongOrNull() ?: 0L
            com.tuusuario.myroutineapp.ui.screens.play.PlayRutinaScreen(rutinaId = rutinaId, navController = navController)
        }
        composable("ejercicios") {
            val viewModel = hiltViewModel<com.tuusuario.myroutineapp.ui.screens.ejercicios.EjerciciosViewModel>()
            EjerciciosScreen(
                viewModel = viewModel,
                onEjercicioClick = { ejercicio ->
                    navController.navigate("ejercicioDetalle/${ejercicio.id}")
                },
                onNuevoEjercicio = {
                    navController.navigate("ejercicioDetalle/0")
                }
            )
        }
        composable("ejercicioDetalle/{ejercicioId}") { backStackEntry ->
            val ejercicioId = backStackEntry.arguments?.getString("ejercicioId")?.toLongOrNull() ?: 0L
            EjercicioDetailScreen(ejercicioId = ejercicioId, navController = navController)
        }
        composable("notas") {
            NotasScreen(
                onRutinaClick = { rutinaId ->
                    navController.navigate("rutinaDetalle/$rutinaId")
                },
                onEjercicioClick = { ejercicioId ->
                    navController.navigate("ejercicioDetalle/$ejercicioId")
                }
            )
        }
    }
}
