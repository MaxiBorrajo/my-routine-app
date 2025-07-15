package com.tuusuario.myroutineapp.ui.screens.configuracion

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun ConfiguracionScreen(
    navController: NavController,
    viewModel: ConfiguracionViewModel = hiltViewModel()
) {
    val sonidos by viewModel.sonidos.collectAsState()
    val descanso by viewModel.descanso.collectAsState()
    val unidadPeso by viewModel.unidadPeso.collectAsState()
    val unidadDistancia by viewModel.unidadDistancia.collectAsState()
    val sonidoInicio by viewModel.sonidoInicio.collectAsState()
    val sonidoFinSerie by viewModel.sonidoFinSerie.collectAsState()
    val sonidoDescanso by viewModel.sonidoDescanso.collectAsState()
    val preparacion by viewModel.preparacion.collectAsState()

    var descansoText by remember { mutableStateOf(descanso.toString()) }
    var preparacionText by remember { mutableStateOf(preparacion.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección de Sonidos
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sonidos",
                            style = MaterialTheme.typography.h6
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Activar/Desactivar sonidos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Activar sonidos")
                        Switch(
                            checked = sonidos,
                            onCheckedChange = { viewModel.setSonidos(it) }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Sonido de inicio
                    OutlinedTextField(
                        value = sonidoInicio,
                        onValueChange = { viewModel.setSonidoInicio(it) },
                        label = { Text("Sonido de inicio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Sonido fin de serie
                    OutlinedTextField(
                        value = sonidoFinSerie,
                        onValueChange = { viewModel.setSonidoFinSerie(it) },
                        label = { Text("Sonido fin de serie") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Sonido descanso
                    OutlinedTextField(
                        value = sonidoDescanso,
                        onValueChange = { viewModel.setSonidoDescanso(it) },
                        label = { Text("Sonido descanso") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Sección de Tiempos
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Tiempos",
                        style = MaterialTheme.typography.h6
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Tiempo de descanso por defecto
                    OutlinedTextField(
                        value = descansoText,
                        onValueChange = { 
                            descansoText = it
                            it.toIntOrNull()?.let { value ->
                                viewModel.setDescanso(value)
                            }
                        },
                        label = { Text("Descanso por defecto (segundos)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Tiempo de preparación
                    OutlinedTextField(
                        value = preparacionText,
                        onValueChange = { 
                            preparacionText = it
                            it.toIntOrNull()?.let { value ->
                                viewModel.setPreparacion(value)
                            }
                        },
                        label = { Text("Tiempo de preparación (segundos)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Sección de Unidades
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Unidades",
                        style = MaterialTheme.typography.h6
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Unidad de peso
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Unidad de peso")
                        Row {
                            RadioButton(
                                selected = unidadPeso == "kg",
                                onClick = { viewModel.setUnidadPeso("kg") }
                            )
                            Text("kg")
                            Spacer(modifier = Modifier.width(16.dp))
                            RadioButton(
                                selected = unidadPeso == "lb",
                                onClick = { viewModel.setUnidadPeso("lb") }
                            )
                            Text("lb")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Unidad de distancia
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Unidad de distancia")
                        Row {
                            RadioButton(
                                selected = unidadDistancia == "km",
                                onClick = { viewModel.setUnidadDistancia("km") }
                            )
                            Text("km")
                            Spacer(modifier = Modifier.width(16.dp))
                            RadioButton(
                                selected = unidadDistancia == "mi",
                                onClick = { viewModel.setUnidadDistancia("mi") }
                            )
                            Text("mi")
                        }
                    }
                }
            }

            // Información adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Información",
                        style = MaterialTheme.typography.h6
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "• Los sonidos se reproducen durante la ejecución de rutinas",
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = "• El tiempo de descanso se aplica entre ejercicios",
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = "• El tiempo de preparación se ejecuta al inicio de cada rutina",
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = "• Las unidades se aplican en métricas y registros",
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }
}
