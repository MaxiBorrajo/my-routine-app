import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ConfiguracionScreen(viewModel: ConfiguracionViewModel = hiltViewModel()) {
    val sonidos by viewModel.sonidos.collectAsState()
    val descanso by viewModel.descanso.collectAsState()
    val unidadPeso by viewModel.unidadPeso.collectAsState()
    val unidadDistancia by viewModel.unidadDistancia.collectAsState()
    val sonidoInicio by viewModel.sonidoInicio.collectAsState()
    val sonidoFinSerie by viewModel.sonidoFinSerie.collectAsState()
    val sonidoDescanso by viewModel.sonidoDescanso.collectAsState()
    val preparacion by viewModel.preparacion.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Configuración avanzada", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("Sonidos activados")
            Switch(checked = sonidos, onCheckedChange = { viewModel.setSonidos(it) })
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = sonidoInicio,
            onValueChange = { viewModel.setSonidoInicio(it) },
            label = { Text("Sonido de inicio (archivo)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = sonidoFinSerie,
            onValueChange = { viewModel.setSonidoFinSerie(it) },
            label = { Text("Sonido fin de serie (archivo)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = sonidoDescanso,
            onValueChange = { viewModel.setSonidoDescanso(it) },
            label = { Text("Sonido descanso (archivo)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = preparacion.toString(),
            onValueChange = { it.toIntOrNull()?.let(viewModel::setPreparacion) },
            label = { Text("Tiempo de preparación inicial (segundos)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = descanso.toString(),
            onValueChange = { it.toIntOrNull()?.let(viewModel::setDescanso) },
            label = { Text("Descanso entre ejercicios (segundos)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        Row {
            Text("Unidad de peso:")
            Spacer(Modifier.width(8.dp))
            DropdownMenuUnidad(
                opciones = listOf("kg", "lb"),
                seleccion = unidadPeso,
                onSeleccion = viewModel::setUnidadPeso
            )
        }
        Row {
            Text("Unidad de distancia:")
            Spacer(Modifier.width(8.dp))
            DropdownMenuUnidad(
                opciones = listOf("km", "mi"),
                seleccion = unidadDistancia,
                onSeleccion = viewModel::setUnidadDistancia
            )
        }
    }
}

@Composable
fun DropdownMenuUnidad(opciones: List<String>, seleccion: String, onSeleccion: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) {
            Text(seleccion)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(onClick = {
                    onSeleccion(opcion)
                    expanded = false
                }, text = { Text(opcion) })
            }
        }
    }
}
