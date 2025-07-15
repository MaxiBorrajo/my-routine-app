package com.tuusuario.myroutineapp.ui.screens.rutinas

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tuusuario.myroutineapp.data.entities.Rutina
import com.tuusuario.myroutineapp.domain.usecase.RutinaEjercicioUseCases
import androidx.lifecycle.viewmodel.compose.viewModelScope
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun RutinaDetailScreen(
    rutinaId: Long,
    navController: NavController,
    viewModel: RutinasViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    ejerciciosViewModel: EjerciciosViewModel = hiltViewModel(),
    rutinaEjercicioUseCases: RutinaEjercicioUseCases = hiltViewModel<RutinaEjercicioUseCases>()
) {
    val rutinas by viewModel.rutinas.collectAsState()
    val rutina = rutinas.find { it.id == rutinaId } ?: Rutina(nombre = "", notas = "", tiempoPreparacion = 0)

    var nombre by remember { mutableStateOf(rutina.nombre) }
    var notas by remember { mutableStateOf(rutina.notas ?: "") }
    var tiempoPreparacion by remember { mutableStateOf(rutina.tiempoPreparacion.toString()) }

    var editDialogIndex by remember { mutableStateOf<Int?>(null) }
    var editSeries by remember { mutableStateOf(3) }
    var editReps by remember { mutableStateOf(10) }
    var editTiempo by remember { mutableStateOf(0) }
    var editDescansoSeries by remember { mutableStateOf(60) }
    var editDescansoEjercicios by remember { mutableStateOf(90) }
    var editNotas by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (rutinaId == 0L) "Nueva Rutina" else "Editar Rutina") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val nuevaRutina = rutina.copy(
                    nombre = nombre,
                    notas = notas,
                    tiempoPreparacion = tiempoPreparacion.toIntOrNull() ?: 0
                )
                viewModel.guardarRutina(nuevaRutina)
                // Persistir ejercicios de la rutina
                androidx.lifecycle.viewmodel.compose.viewModelScope.launch {
                    ejerciciosEnRutina.forEach { re ->
                        rutinaEjercicioUseCases.insert(re.copy(rutinaId = nuevaRutina.id))
                    }
                }
                navController.popBackStack()
            }) {
                Icon(Icons.Default.Check, contentDescription = "Guardar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de la rutina") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = tiempoPreparacion,
                onValueChange = { tiempoPreparacion = it },
                label = { Text("Tiempo de preparación (segundos)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (editDialogIndex != null) {
        AlertDialog(
            onDismissRequest = { editDialogIndex = null },
            title = { Text("Editar parámetros del ejercicio") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editSeries.toString(),
                        onValueChange = { editSeries = it.toIntOrNull() ?: 1 },
                        label = { Text("Series") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editReps.toString(),
                        onValueChange = { editReps = it.toIntOrNull() ?: 0 },
                        label = { Text("Repeticiones") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editTiempo.toString(),
                        onValueChange = { editTiempo = it.toIntOrNull() ?: 0 },
                        label = { Text("Tiempo (segundos, opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDescansoSeries.toString(),
                        onValueChange = { editDescansoSeries = it.toIntOrNull() ?: 0 },
                        label = { Text("Descanso entre series (segundos)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDescansoEjercicios.toString(),
                        onValueChange = { editDescansoEjercicios = it.toIntOrNull() ?: 0 },
                        label = { Text("Descanso entre ejercicios (segundos)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editNotas,
                        onValueChange = { editNotas = it },
                        label = { Text("Notas") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    editDialogIndex?.let { idx ->
                        val old = ejerciciosEnRutina[idx]
                        ejerciciosEnRutina = ejerciciosEnRutina.toMutableList().also {
                            it[idx] = old.copy(
                                series = editSeries,
                                repeticiones = editReps,
                                tiempo = if (editTiempo > 0) editTiempo else null,
                                descansoEntreSeries = editDescansoSeries,
                                descansoEntreEjercicios = editDescansoEjercicios,
                                notas = editNotas.ifBlank { null }
                            )
                        }
                    }
                    editDialogIndex = null
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = { editDialogIndex = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
