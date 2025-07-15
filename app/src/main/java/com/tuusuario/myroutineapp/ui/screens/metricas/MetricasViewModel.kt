package com.tuusuario.myroutineapp.ui.screens.metricas

import androidx.lifecycle.ViewModel
import com.tuusuario.myroutineapp.domain.usecase.ProgresoUseCases
import com.tuusuario.myroutineapp.domain.usecase.RutinaUseCases
import com.tuusuario.myroutineapp.domain.usecase.NotaUseCases
import com.tuusuario.myroutineapp.data.entities.Progreso
import com.tuusuario.myroutineapp.data.entities.Rutina
import com.tuusuario.myroutineapp.data.entities.Nota
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class MetricasViewModel @Inject constructor(
    private val progresoUseCases: ProgresoUseCases,
    private val rutinaUseCases: RutinaUseCases,
    private val notaUseCases: NotaUseCases
) : ViewModel() {
    fun progresoPorEjercicio(ejercicioId: Long): Flow<List<Progreso>> =
        progresoUseCases.getByEjercicio(ejercicioId)

    val rutinas: Flow<List<Rutina>> = rutinaUseCases.getAll()
    val notas: Flow<List<Nota>> = notaUseCases.getAll()

    // Evolución por grupo muscular
    val evolucionPorGrupo = combine(progresoUseCases.getAll(), rutinaUseCases.getAll()) { progresos, rutinas ->
        val grupoMap = mutableMapOf<String, MutableList<Progreso>>()
        rutinas.forEach { rutina ->
            rutina.ejercicios.forEach { ejercicioId ->
                val grupo = "" // Aquí deberías mapear ejercicioId a grupo muscular
                val progresosEj = progresos.filter { it.ejercicioId == ejercicioId }
                grupoMap.getOrPut(grupo) { mutableListOf() }.addAll(progresosEj)
            }
        }
        grupoMap
    }

    // Estadísticas de intensidad y duración
    val estadisticasRutinas = rutinaUseCases.getAll() // Puedes mapear a datos agregados
}
