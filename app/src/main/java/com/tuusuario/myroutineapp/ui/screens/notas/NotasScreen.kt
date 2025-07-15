package com.tuusuario.myroutineapp.ui.screens.notas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
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
    onRutinaClick: (Long) -> Unit,
    onEjercicioClick: (Long) -> Unit,
    onConfiguracion: () -> Unit,
    viewModel: NotasViewModel = hiltViewModel()
) {
    val notas by viewModel.notas.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var newNotaText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Notas") },
                actions = {
                    IconButton(onClick = onConfiguracion) {
                        Icon(Icons.Default.Settings, contentDescription = "Configuración")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva nota")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(notas) { nota ->
                NotaItem(
                    nota = nota,
                    onDelete = { viewModel.eliminarNota(nota) },
                    onRutinaClick = { nota.rutinaId?.let { onRutinaClick(it) } },
                    onEjercicioClick = { nota.ejercicioId?.let { onEjercicioClick(it) } }
                )
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Nueva Nota") },
                text = {
                    OutlinedTextField(
                        value = newNotaText,
                        onValueChange = { newNotaText = it },
                        label = { Text("Texto de la nota") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newNotaText.isNotEmpty()) {
                                viewModel.agregarNota(newNotaText)
                                newNotaText = ""
                                showAddDialog = false
                            }
                        }
                    ) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
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
    onDelete: () -> Unit,
    onRutinaClick: () -> Unit,
    onEjercicioClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(nota.texto, style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Fecha: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(nota.fecha))}",
                style = MaterialTheme.typography.caption
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    nota.rutinaId?.let {
                        TextButton(onClick = onRutinaClick) {
                            Text("Ver Rutina")
                        }
                    }
                    nota.ejercicioId?.let {
                        TextButton(onClick = onEjercicioClick) {
                            Text("Ver Ejercicio")
                        }
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}
