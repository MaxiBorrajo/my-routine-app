package com.tuusuario.myroutineapp.ui.screens.notas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tuusuario.myroutineapp.data.entities.Nota
import com.tuusuario.myroutineapp.data.entities.Rutina
import com.tuusuario.myroutineapp.data.entities.Ejercicio
import com.tuusuario.myroutineapp.ui.screens.rutinas.RutinasViewModel
import com.tuusuario.myroutineapp.ui.screens.ejercicios.EjerciciosViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotasScreen(
    viewModel: NotasViewModel = hiltViewModel(),
    rutinasViewModel: RutinasViewModel = hiltViewModel(),
    ejerciciosViewModel: EjerciciosViewModel = hiltViewModel(),
    onRutinaClick: (Long) -> Unit = {},
    onEjercicioClick: (Long) -> Unit = {}
) {
    val notas by viewModel.notas.collectAsState()
    val rutinas by rutinasViewModel.rutinas.collectAsState()
    val ejercicios by ejerciciosViewModel.ejercicios.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var nuevaNota by remember { mutableStateOf("") }
    var selectedRutinaId by remember { mutableStateOf<Long?>(null) }
    var selectedEjercicioId by remember { mutableStateOf<Long?>(null) }
    var filtroRutinaId by remember { mutableStateOf<Long?>(null) }
    var filtroEjercicioId by remember { mutableStateOf<Long?>(null) }
    var notaAEliminar by remember { mutableStateOf<Nota?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Notas") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva nota")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(modifier = Modifier.padding(8.dp)) {
                DropdownSelectorRutina(rutinas, filtroRutinaId) { filtroRutinaId = it }
                Spacer(modifier = Modifier.width(8.dp))
                DropdownSelectorEjercicio(ejercicios, filtroEjercicioId) { filtroEjercicioId = it }
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(notas.filter {
                    (filtroRutinaId == null || it.rutinaId == filtroRutinaId) &&
                    (filtroEjercicioId == null || it.ejercicioId == filtroEjercicioId)
                }) { nota ->
                    NotaItem(
                        nota = nota,
                        rutinas = rutinas,
                        ejercicios = ejercicios,
                        onDelete = { notaAEliminar = nota },
                        onRutinaClick = onRutinaClick,
                        onEjercicioClick = onEjercicioClick
                    )
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nueva nota") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nuevaNota,
                        onValueChange = { nuevaNota = it },
                        label = { Text("Nota") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DropdownSelectorRutina(rutinas, selectedRutinaId) { selectedRutinaId = it }
                    Spacer(modifier = Modifier.height(8.dp))
                    DropdownSelectorEjercicio(ejercicios, selectedEjercicioId) { selectedEjercicioId = it }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (nuevaNota.isNotBlank()) {
                        viewModel.guardarNota(
                            Nota(
                                fecha = System.currentTimeMillis(),
                                texto = nuevaNota,
                                rutinaId = selectedRutinaId,
                                ejercicioId = selectedEjercicioId
                            )
                        )
                        nuevaNota = ""
                        selectedRutinaId = null
                        selectedEjercicioId = null
                        showDialog = false
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    if (notaAEliminar != null) {
        AlertDialog(
            onDismissRequest = { notaAEliminar = null },
            title = { Text("Eliminar nota") },
            text = { Text("¿Estás seguro de que deseas eliminar esta nota?") },
            confirmButton = {
                Button(onClick = {
                    notaAEliminar?.let { viewModel.eliminarNota(it) }
                    notaAEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                Button(onClick = { notaAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun DropdownSelectorRutina(rutinas: List<Rutina>, selectedId: Long?, onSelect: (Long?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selectedRutina = rutinas.find { it.id == selectedId }
    Box {
        Button(onClick = { expanded = true }) {
            Text(selectedRutina?.nombre ?: "Asociar a rutina (opcional)")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(onClick = { onSelect(null); expanded = false }) {
                Text("Sin asociación")
            }
            rutinas.forEach { rutina ->
                DropdownMenuItem(onClick = { onSelect(rutina.id); expanded = false }) {
                    Text(rutina.nombre)
                }
            }
        }
    }
}

@Composable
fun DropdownSelectorEjercicio(ejercicios: List<Ejercicio>, selectedId: Long?, onSelect: (Long?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selectedEjercicio = ejercicios.find { it.id == selectedId }
    Box {
        Button(onClick = { expanded = true }) {
            Text(selectedEjercicio?.nombre ?: "Asociar a ejercicio (opcional)")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(onClick = { onSelect(null); expanded = false }) {
                Text("Sin asociación")
            }
            ejercicios.forEach { ejercicio ->
                DropdownMenuItem(onClick = { onSelect(ejercicio.id); expanded = false }) {
                    Text(ejercicio.nombre)
                }
            }
        }
    }
}

@Composable
fun NotaItem(
    nota: Nota,
    rutinas: List<Rutina>,
    ejercicios: List<Ejercicio>,
    onDelete: () -> Unit,
    onRutinaClick: (Long) -> Unit,
    onEjercicioClick: (Long) -> Unit
) {
    val rutina = nota.rutinaId?.let { rutinas.find { r -> r.id == it } }
    val ejercicio = nota.ejercicioId?.let { ejercicios.find { e -> e.id == it } }
    val fechaLegible = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(nota.fecha))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(nota.texto, style = MaterialTheme.typography.body1)
                Text("Fecha: $fechaLegible", style = MaterialTheme.typography.caption)
                if (rutina != null) {
                    TextButton(onClick = { onRutinaClick(rutina.id) }) {
                        Text("Rutina: ${rutina.nombre}", style = MaterialTheme.typography.caption)
                    }
                }
                if (ejercicio != null) {
                    TextButton(onClick = { onEjercicioClick(ejercicio.id) }) {
                        Text("Ejercicio: ${ejercicio.nombre}", style = MaterialTheme.typography.caption)
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar nota")
            }
        }
    }
}
