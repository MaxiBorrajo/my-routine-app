package com.tuusuario.myroutineapp.ui.screens.ejercicios

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tuusuario.myroutineapp.data.entities.Ejercicio
import androidx.compose.foundation.Image
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.viewinterop.AndroidView
import android.net.Uri
import android.widget.VideoView
import androidx.compose.ui.platform.LocalContext

@Composable
fun EjerciciosScreen(
    viewModel: EjerciciosViewModel,
    onEjercicioClick: (Ejercicio) -> Unit,
    onNuevoEjercicio: () -> Unit
) {
    val ejercicios by viewModel.ejercicios.collectAsState()
    var search by remember { mutableStateOf("") }
    var grupoFiltro by remember { mutableStateOf("") }

    val ejerciciosFiltrados = ejercicios.filter {
        (search.isBlank() || it.nombre.contains(search, ignoreCase = true)) &&
        (grupoFiltro.isBlank() || it.gruposMusculares.contains(grupoFiltro, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ejercicios") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevoEjercicio) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo ejercicio")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Row(modifier = Modifier.padding(8.dp)) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Buscar") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = grupoFiltro,
                    onValueChange = { grupoFiltro = it },
                    label = { Text("Grupo muscular") },
                    modifier = Modifier.weight(1f)
                )
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(ejerciciosFiltrados) { ejercicio ->
                    EjercicioItem(
                        ejercicio = ejercicio,
                        onClick = { onEjercicioClick(ejercicio) },
                        onDelete = { viewModel.eliminarEjercicio(ejercicio) }
                    )
                }
            }
        }
    }
}

@Composable
fun EjercicioItem(
    ejercicio: Ejercicio,
    onClick: () -> Unit,
    onDelete: () -> Unit
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
            if (!ejercicio.mediaUri.isNullOrBlank()) {
                val uri = ejercicio.mediaUri
                val isVideo = uri.startsWith("content") && (uri.contains("video") || uri.endsWith(".mp4") || uri.endsWith(".3gp") || uri.endsWith(".mkv"))
                val context = LocalContext.current
                if (isVideo) {
                    AndroidView(
                        factory = {
                            VideoView(context).apply {
                                setVideoURI(Uri.parse(uri))
                                setOnPreparedListener { it.isLooping = true; start() }
                            }
                        },
                        modifier = Modifier.size(64.dp)
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(ejercicio.nombre, style = MaterialTheme.typography.h6)
                Text(ejercicio.gruposMusculares, style = MaterialTheme.typography.body2)
                ejercicio.descripcion?.let {
                    Text(it, style = MaterialTheme.typography.body2)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}
