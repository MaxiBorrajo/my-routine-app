package com.tuusuario.myroutineapp.ui.screens.ejercicios

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tuusuario.myroutineapp.data.entities.Ejercicio
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.viewinterop.AndroidViewBinding
import android.widget.VideoView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun EjercicioDetailScreen(
    ejercicioId: Long,
    navController: NavController,
    viewModel: EjerciciosViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val ejercicios by viewModel.ejercicios.collectAsState()
    val ejercicio = ejercicios.find { it.id == ejercicioId } ?: Ejercicio(
        nombre = "",
        descripcion = "",
        gruposMusculares = "",
        tipoSerie = "repeticiones",
        peso = null,
        intensidad = "media",
        mediaUri = null
    )

    var nombre by remember { mutableStateOf(ejercicio.nombre) }
    var descripcion by remember { mutableStateOf(ejercicio.descripcion ?: "") }
    var gruposMusculares by remember { mutableStateOf(ejercicio.gruposMusculares) }
    var tipoSerie by remember { mutableStateOf(ejercicio.tipoSerie) }
    var peso by remember { mutableStateOf(ejercicio.peso?.toString() ?: "") }
    var intensidad by remember { mutableStateOf(ejercicio.intensidad) }
    var mediaUri by remember { mutableStateOf(ejercicio.mediaUri ?: "") }
    val context = androidx.compose.ui.platform.LocalContext.current

    val pickMediaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { mediaUri = it.toString() }
    }
    val takePhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val path = android.provider.MediaStore.Images.Media.insertImage(
                context.contentResolver, it, "ejercicio_${System.currentTimeMillis()}", null
            )
            path?.let { mediaUri = it }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (ejercicioId == 0L) "Nuevo Ejercicio" else "Editar Ejercicio") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val nuevoEjercicio = ejercicio.copy(
                    nombre = nombre,
                    descripcion = descripcion,
                    gruposMusculares = gruposMusculares,
                    tipoSerie = tipoSerie,
                    peso = peso.toFloatOrNull(),
                    intensidad = intensidad,
                    mediaUri = if (mediaUri.isBlank()) null else mediaUri
                )
                viewModel.guardarEjercicio(nuevoEjercicio)
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
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = gruposMusculares,
                onValueChange = { gruposMusculares = it },
                label = { Text("Grupo(s) muscular(es)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = tipoSerie,
                onValueChange = { tipoSerie = it },
                label = { Text("Tipo de serie (repeticiones/tiempo/distancia)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = peso,
                onValueChange = { peso = it },
                label = { Text("Peso (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = intensidad,
                onValueChange = { intensidad = it },
                label = { Text("Intensidad (baja/media/alta)") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                OutlinedButton(onClick = { pickMediaLauncher.launch("image/*") }) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Galería")
                    Spacer(Modifier.width(4.dp))
                    Text("Imagen")
                }
                OutlinedButton(onClick = { pickMediaLauncher.launch("video/*") }) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Galería")
                    Spacer(Modifier.width(4.dp))
                    Text("Video")
                }
                OutlinedButton(onClick = { takePhotoLauncher.launch(null) }) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Cámara")
                    Spacer(Modifier.width(4.dp))
                    Text("Cámara")
                }
            }
            if (mediaUri.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("Previsualización:")
                if (mediaUri.startsWith("content") && (mediaUri.contains("video") || mediaUri.endsWith(".mp4") || mediaUri.endsWith(".3gp") || mediaUri.endsWith(".mkv"))) {
                    val context = LocalContext.current
                    AndroidView(
                        factory = {
                            VideoView(context).apply {
                                setVideoURI(Uri.parse(mediaUri))
                                setOnPreparedListener { it.isLooping = true; start() }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(mediaUri),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }
            }
            OutlinedTextField(
                value = mediaUri,
                onValueChange = { mediaUri = it },
                label = { Text("Foto/Video/Link (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
