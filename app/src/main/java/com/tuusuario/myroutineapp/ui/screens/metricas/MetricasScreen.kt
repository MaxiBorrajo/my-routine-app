package com.tuusuario.myroutineapp.ui.screens.metricas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
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
    viewModel: MetricasViewModel = hiltViewModel()
) {
    val rutinas by viewModel.rutinas.collectAsState(initial = emptyList())
    var selectedEjercicioId by remember { mutableStateOf<Long?>(null) }
    var progreso by remember { mutableStateOf<List<Progreso>>(emptyList()) }
    val notas by viewModel.notas.collectAsState(initial = emptyList())
    val evolucionPorGrupo by viewModel.evolucionPorGrupo.collectAsState(initial = emptyMap())
    val rutinasList by viewModel.rutinas.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Métricas", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Tiempo total de entrenamiento: ${rutinas.size} rutinas completadas")
        Spacer(modifier = Modifier.height(16.dp))
        // Selección de ejercicio para ver progreso
        if (rutinas.isNotEmpty()) {
            val ejercicios = rutinas.flatMap { it.nombre.split(",") }.distinct()
            DropdownMenuEjercicios(ejercicios, selectedEjercicioId) { id ->
                selectedEjercicioId = id
            }
        }
        selectedEjercicioId?.let { ejercicioId ->
            val progresoFlow = viewModel.progresoPorEjercicio(ejercicioId)
            LaunchedEffect(ejercicioId) {
                progresoFlow.collect { progreso = it }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Progreso histórico del ejercicio", style = MaterialTheme.typography.h6)
            LazyColumn {
                items(progreso) { p ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Fecha: ${p.fecha}")
                            p.peso?.let { Text("Peso: $it kg") }
                            p.repeticiones?.let { Text("Reps: $it") }
                            p.tiempo?.let { Text("Tiempo: $it s") }
                            p.distancia?.let { Text("Distancia: $it km") }
                            p.notas?.let { Text("Notas: $it") }
                        }
                    }
                }
            }
        }
        // Evolución por grupo muscular
        Spacer(modifier = Modifier.height(16.dp))
        Text("Evolución por grupo muscular", style = MaterialTheme.typography.h6)
        if (evolucionPorGrupo.isNotEmpty()) {
            evolucionPorGrupo.forEach { (grupo, progresos) ->
                Text("$grupo: ${progresos.size} registros")
                val totalPeso = progresos.sumOf { it.peso?.toDouble() ?: 0.0 }
                val totalReps = progresos.sumOf { it.repeticiones ?: 0 }
                val totalTiempo = progresos.sumOf { it.tiempo ?: 0 }
                Text("  Peso total: $totalPeso kg, Reps: $totalReps, Tiempo: $totalTiempo s")
            }
        } else {
            Text("No hay datos de grupos musculares.")
        }
        // Estadísticas de intensidad y duración
        Spacer(modifier = Modifier.height(16.dp))
        Text("Estadísticas de rutinas", style = MaterialTheme.typography.h6)
        if (rutinasList.isNotEmpty()) {
            val intensidades = rutinasList.map { it.intensidad }
            val duraciones = rutinasList.map { it.duracion ?: 0 }
            val intensidadProm = intensidades.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: "-"
            val duracionProm = if (duraciones.isNotEmpty()) duraciones.average().toInt() else 0
            Text("Intensidad más frecuente: $intensidadProm")
            Text("Duración promedio: $duracionProm s")
        } else {
            Text("No hay rutinas registradas.")
        }
        // Diario personal (notas)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Diario personal (notas)", style = MaterialTheme.typography.h6)
        if (notas.isNotEmpty()) {
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                items(notas) { nota ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        elevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Fecha: ${nota.fecha}")
                            Text(nota.texto)
                            nota.rutinaId?.let { Text("Rutina asociada: $it") }
                            nota.ejercicioId?.let { Text("Ejercicio asociado: $it") }
                        }
                    }
                }
            }
        } else {
            Text("No hay notas registradas.")
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
