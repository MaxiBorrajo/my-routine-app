package com.tuusuario.myroutineapp.ui.screens.play

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.viewinterop.AndroidView
import android.net.Uri
import android.widget.VideoView
import androidx.compose.ui.platform.LocalContext

@Composable
fun PlayRutinaScreen(
    rutinaId: Long,
    navController: NavController,
    viewModel: PlayRutinaViewModel = hiltViewModel()
) {
    LaunchedEffect(rutinaId) {
        viewModel.startRutina(rutinaId)
    }

    val ejercicios by viewModel.ejercicios.collectAsState()
    val rutinaEjercicios by viewModel.rutinaEjercicios.collectAsState()
    val currentStep by viewModel.currentStep.collectAsState()
    val timer by viewModel.timer.collectAsState()
    val isRest by viewModel.isRest.collectAsState()
    val unidadPeso = viewModel.unidadPeso
    val unidadDistancia = viewModel.unidadDistancia

    val step = rutinaEjercicios.getOrNull(currentStep)
    val ejercicio = step?.let { ejercicios[it.ejercicioId] }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Play Rutina") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
            verticalArrangement = Arrangement.Center
        ) {
            if (ejercicio != null) {
                Text(
                    text = if (isRest) "Descanso" else ejercicio.nombre,
                    style = MaterialTheme.typography.h4
                )
                ejercicio.mediaUri?.let {
                    val isVideo = it.startsWith("content") && (it.contains("video") || it.endsWith(".mp4") || it.endsWith(".3gp") || it.endsWith(".mkv"))
                    val context = LocalContext.current
                    if (isVideo) {
                        AndroidView(
                            factory = {
                                VideoView(context).apply {
                                    setVideoURI(Uri.parse(it))
                                    setOnPreparedListener { mp -> mp.isLooping = true; start() }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(it),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tiempo restante: $timer s",
                    style = MaterialTheme.typography.h5
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Mostrar repeticiones, peso o distancia con unidad
                if (ejercicio.tipoSerie == "repeticiones") {
                    Text("Repeticiones: ${step?.repeticiones ?: "-"}")
                } else if (ejercicio.tipoSerie == "tiempo") {
                    Text("Duración: ${step?.tiempo ?: "-"} s")
                } else if (ejercicio.tipoSerie == "distancia") {
                    Text("Distancia: ${step?.distancia ?: "-"} ${unidadDistancia.value}")
                }
                if (ejercicio.peso != null) {
                    Text("Peso: ${ejercicio.peso} ${unidadPeso.value}")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(onClick = { viewModel.pause() }) { Text("Pausar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { viewModel.resume() }) { Text("Reanudar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { viewModel.skip() }) { Text("Saltar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { viewModel.repeat() }) { Text("Repetir") }
                }
            } else {
                Text("¡Rutina finalizada!", style = MaterialTheme.typography.h4)
            }
        }
    }
}
