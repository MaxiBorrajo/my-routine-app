package com.tuusuario.myroutineapp.ui.screens.inicio

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tuusuario.myroutineapp.ui.screens.rutinas.RutinasViewModel
import com.tuusuario.myroutineapp.ui.screens.metricas.MetricasViewModel
import com.tuusuario.myroutineapp.ui.screens.notas.NotasViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun InicioScreen(
    onRutinas: () -> Unit,
    onEjercicios: () -> Unit,
    onMetricas: () -> Unit,
    onNotas: () -> Unit,
    onPlayRutina: (Long) -> Unit
) {
    val rutinasViewModel: RutinasViewModel = hiltViewModel()
    val metricasViewModel: MetricasViewModel = hiltViewModel()
    val notasViewModel: NotasViewModel = hiltViewModel()
    val rutinas by rutinasViewModel.rutinas.collectAsState()
    val notas by notasViewModel.notas.collectAsState()

    val ultimaRutina = rutinas.lastOrNull()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mi Progreso") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Resumen general", style = MaterialTheme.typography.h6)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(modifier = Modifier.weight(1f)) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Rutinas", style = MaterialTheme.typography.caption)
                        Text("${rutinas.size}", style = MaterialTheme.typography.h5)
                    }
                }
                Card(modifier = Modifier.weight(1f)) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Notas", style = MaterialTheme.typography.caption)
                        Text("${notas.size}", style = MaterialTheme.typography.h5)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { ultimaRutina?.let { onPlayRutina(it.id) } },
                enabled = ultimaRutina != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Iniciar rutina")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar última rutina")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = onRutinas, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Rutinas")
                }
                Button(onClick = onEjercicios, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ejercicios")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = onMetricas, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.BarChart, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Métricas")
                }
                Button(onClick = onNotas, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.StickyNote2, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Notas")
                }
            }
        }
    }
}
