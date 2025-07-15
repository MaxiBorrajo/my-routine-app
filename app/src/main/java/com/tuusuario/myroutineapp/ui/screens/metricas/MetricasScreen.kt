package com.tuusuario.myroutineapp.ui.screens.metricas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tuusuario.myroutineapp.data.entities.Progreso
import com.tuusuario.myroutineapp.data.entities.Rutina
import com.tuusuario.myroutineapp.ui.screens.metricas.MetricasViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.tuusuario.myroutineapp.data.entities.Nota

@Composable
fun MetricasScreen(
    onConfiguracion: () -> Unit,
    viewModel: MetricasViewModel = hiltViewModel()
) {
    val progreso by viewModel.progreso.collectAsState()
    val rutinas by viewModel.rutinas.collectAsState()
    val ejercicios by viewModel.ejercicios.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Métricas") },
                actions = {
                    IconButton(onClick = onConfiguracion) {
                        Icon(Icons.Default.Settings, contentDescription = "Configuración")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Resumen general
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Resumen General",
                        style = MaterialTheme.typography.h6
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total de rutinas: ${rutinas.size}")
                    Text("Total de ejercicios: ${ejercicios.size}")
                    Text("Registros de progreso: ${progreso.size}")
                }
            }

            // Métricas por grupo muscular
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Ejercicios por Grupo Muscular",
                        style = MaterialTheme.typography.h6
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val gruposMusculares = ejercicios.groupBy { it.grupoMuscular }
                    gruposMusculares.forEach { (grupo, ejerciciosGrupo) ->
                        Text("$grupo: ${ejerciciosGrupo.size} ejercicios")
                    }
                }
            }

            // Progreso reciente
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Progreso Reciente",
                        style = MaterialTheme.typography.h6
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val progresoReciente = progreso.take(5)
                    if (progresoReciente.isNotEmpty()) {
                        progresoReciente.forEach { p ->
                            val ejercicio = ejercicios.find { it.id == p.ejercicioId }
                            Text(
                                text = "${ejercicio?.nombre ?: "Ejercicio"} - ${p.repeticiones ?: p.tiempo ?: "-"}",
                                style = MaterialTheme.typography.body2
                            )
                        }
                    } else {
                        Text("No hay registros de progreso recientes")
                    }
                }
            }

            // Estadísticas de intensidad
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Estadísticas de Intensidad",
                        style = MaterialTheme.typography.h6
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val intensidades = ejercicios.groupBy { it.intensidad }
                    intensidades.forEach { (intensidad, ejerciciosIntensidad) ->
                        Text("$intensidad: ${ejerciciosIntensidad.size} ejercicios")
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuEjercicios(
    ejercicios: List<String>,
    selectedId: Long?,
    onSelect: (Long) -> Unit
) {
    // Placeholder: aquí deberías mapear los nombres a IDs reales de ejercicios
    // Por simplicidad, se usa el índice como ID
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }
    Box {
        Button(onClick = { expanded = true }) {
            Text(ejercicios.getOrNull(selectedIndex) ?: "Seleccionar ejercicio")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ejercicios.forEachIndexed { index, nombre ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                    onSelect(index.toLong())
                }) {
                    Text(nombre)
                }
            }
        }
    }
}
