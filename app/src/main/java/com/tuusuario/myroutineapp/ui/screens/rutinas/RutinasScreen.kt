package com.tuusuario.myroutineapp.ui.screens.rutinas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tuusuario.myroutineapp.data.entities.Rutina

@Composable
fun RutinasScreen(
    viewModel: RutinasViewModel,
    onRutinaClick: (Rutina) -> Unit,
    onNuevaRutina: () -> Unit,
    onPlayRutina: (Rutina) -> Unit,
    onEjercicios: () -> Unit,
    onMetricas: () -> Unit,
    onNotas: () -> Unit,
    onConfiguracion: () -> Unit
) {
    val rutinas by viewModel.rutinas.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Rutinas") },
                actions = {
                    IconButton(onClick = onEjercicios) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = "Ejercicios")
                    }
                    IconButton(onClick = onMetricas) {
                        Icon(Icons.Default.BarChart, contentDescription = "Métricas")
                    }
                    IconButton(onClick = onNotas) {
                        Icon(Icons.Default.StickyNote2, contentDescription = "Notas")
                    }
                    IconButton(onClick = onConfiguracion) {
                        Icon(Icons.Default.Settings, contentDescription = "Configuración")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevaRutina) {
                Icon(Icons.Default.Add, contentDescription = "Nueva rutina")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(rutinas) { rutina ->
                RutinaItem(
                    rutina = rutina,
                    onClick = { onRutinaClick(rutina) },
                    onDelete = { viewModel.eliminarRutina(rutina) },
                    onPlay = { onPlayRutina(rutina) }
                )
            }
        }
    }
}

@Composable
fun RutinaItem(
    rutina: Rutina,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(rutina.nombre, style = MaterialTheme.typography.h6)
                rutina.notas?.let {
                    Text(it, style = MaterialTheme.typography.body2)
                }
            }
            IconButton(onClick = onPlay) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}
