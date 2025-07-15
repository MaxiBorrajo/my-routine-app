package com.tuusuario.myroutineapp.ui.screens.play

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.ui.Alignment

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
    val isPaused by viewModel.isPaused.collectAsState()

    val step = rutinaEjercicios.getOrNull(currentStep)
    val ejercicio = step?.let { ejercicios[it.ejercicioId] }

    DisposableEffect(navController) {
        onDispose {
            viewModel.stop()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ejecutando Rutina") },
                navigationIcon = {
                    IconButton(onClick = { 
                        viewModel.stop()
                        navController.popBackStack() 
                    }) {
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (ejercicio != null) {
                Text(
                    text = if (isRest) "Descanso" else ejercicio.nombre,
                    style = MaterialTheme.typography.h4
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Media del ejercicio
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
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Timer principal
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isPaused) "PAUSADO" else "Tiempo restante",
                            style = MaterialTheme.typography.h6
                        )
                        Text(
                            text = "${timer}s",
                            style = MaterialTheme.typography.h3
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Información del ejercicio
                if (!isRest) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Información del ejercicio",
                                style = MaterialTheme.typography.h6
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (ejercicio.tipoSerie == "repeticiones") {
                                Text("Repeticiones: ${step?.repeticiones ?: "-"}")
                            } else if (ejercicio.tipoSerie == "tiempo") {
                                Text("Duración: ${step?.tiempo ?: "-"} s")
                            } else if (ejercicio.tipoSerie == "distancia") {
                                Text("Distancia: ${step?.distancia ?: "-"} km")
                            }
                            
                            step?.notas?.let { notas ->
                                if (notas.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Notas: $notas")
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Controles
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Botón Pausar/Reanudar
                    FloatingActionButton(
                        onClick = { 
                            if (isPaused) viewModel.resume() else viewModel.pause() 
                        },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isPaused) "Reanudar" else "Pausar"
                        )
                    }
                    
                    // Botón Siguiente
                    FloatingActionButton(
                        onClick = { viewModel.skip() },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Siguiente")
                    }
                    
                    // Botón Detener
                    FloatingActionButton(
                        onClick = { 
                            viewModel.stop()
                            navController.popBackStack() 
                        },
                        modifier = Modifier.size(64.dp),
                        backgroundColor = MaterialTheme.colors.error
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = "Detener")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Progreso
                Text(
                    text = "Ejercicio ${currentStep + 1} de ${rutinaEjercicios.size}",
                    style = MaterialTheme.typography.body1
                )
            } else {
                // Estado de carga
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cargando rutina...")
            }
        }
    }
}
