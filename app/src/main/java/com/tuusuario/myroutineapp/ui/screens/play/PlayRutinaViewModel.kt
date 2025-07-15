package com.tuusuario.myroutineapp.ui.screens.play

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuusuario.myroutineapp.data.dao.RutinaEjercicioDao
import com.tuusuario.myroutineapp.data.dao.EjercicioDao
import com.tuusuario.myroutineapp.data.entities.RutinaEjercicio
import com.tuusuario.myroutineapp.data.entities.Ejercicio
import com.tuusuario.myroutineapp.domain.usecase.ProgresoUseCases
import com.tuusuario.myroutineapp.data.entities.Progreso
import com.tuusuario.myroutineapp.data.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayRutinaViewModel @Inject constructor(
    app: Application,
    private val rutinaEjercicioDao: RutinaEjercicioDao,
    private val ejercicioDao: EjercicioDao,
    private val progresoUseCases: ProgresoUseCases,
    private val preferencesManager: PreferencesManager
) : AndroidViewModel(app) {

    private val _rutinaEjercicios = MutableStateFlow<List<RutinaEjercicio>>(emptyList())
    val rutinaEjercicios: StateFlow<List<RutinaEjercicio>> = _rutinaEjercicios

    private val _ejercicios = MutableStateFlow<Map<Long, Ejercicio>>(emptyMap())
    val ejercicios: StateFlow<Map<Long, Ejercicio>> = _ejercicios

    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep

    private val _timer = MutableStateFlow(0)
    val timer: StateFlow<Int> = _timer

    private val _isRest = MutableStateFlow(false)
    val isRest: StateFlow<Boolean> = _isRest

    private var timerJob: Job? = null

    private var sonidoInicio: String = "default_inicio.mp3"
    private var sonidoFinSerie: String = "default_fin_serie.mp3"
    private var sonidoDescanso: String = "default_descanso.mp3"
    private var preparacion: Int = 10
    private var descansoPorDefecto: Int = 60
    private var unidadPeso: String = "kg"
    private var unidadDistancia: String = "km"
    private var sonidosActivados: Boolean = true

    init {
        viewModelScope.launch {
            preferencesManager.sonidoInicio.collect { sonidoInicio = it }
        }
        viewModelScope.launch {
            preferencesManager.sonidoFinSerie.collect { sonidoFinSerie = it }
        }
        viewModelScope.launch {
            preferencesManager.sonidoDescanso.collect { sonidoDescanso = it }
        }
        viewModelScope.launch {
            preferencesManager.preparacion.collect { preparacion = it }
        }
        viewModelScope.launch {
            preferencesManager.descanso.collect { descansoPorDefecto = it }
        }
        viewModelScope.launch {
            preferencesManager.unidadPeso.collect { unidadPeso = it }
        }
        viewModelScope.launch {
            preferencesManager.unidadDistancia.collect { unidadDistancia = it }
        }
        viewModelScope.launch {
            preferencesManager.sonidos.collect { sonidosActivados = it }
        }
    }

    fun startRutina(rutinaId: Long) {
        viewModelScope.launch {
            rutinaEjercicioDao.getByRutina(rutinaId).collect { lista ->
                _rutinaEjercicios.value = lista
                val ids = lista.map { it.ejercicioId }
                val ejerciciosMap = ids.associateWith { ejercicioDao.getById(it) }
                _ejercicios.value = ejerciciosMap.filterValues { it != null } as Map<Long, Ejercicio>
            }
        }
        _currentStep.value = 0
        // Usar tiempo de preparación inicial
        startPreparacion()
    }

    private fun startPreparacion() {
        timerJob?.cancel()
        _isRest.value = false
        _timer.value = preparacion
        playAudio(sonidoInicio)
        timerJob = viewModelScope.launch {
            while (_timer.value > 0) {
                delay(1000)
                _timer.value -= 1
            }
            startStep()
        }
    }

    fun startStep() {
        timerJob?.cancel()
        val step = _rutinaEjercicios.value.getOrNull(_currentStep.value)
        if (step != null) {
            _isRest.value = false
            _timer.value = step.tiempo ?: 0
            playAudio(sonidoInicio)
            timerJob = viewModelScope.launch {
                while (_timer.value > 0) {
                    delay(1000)
                    _timer.value -= 1
                }
                playAudio(sonidoFinSerie)
                startRest()
            }
        }
    }

    fun startRest() {
        timerJob?.cancel()
        val step = _rutinaEjercicios.value.getOrNull(_currentStep.value)
        if (step != null) {
            _isRest.value = true
            _timer.value = step.descansoEntreEjercicios.takeIf { it > 0 } ?: descansoPorDefecto
            playAudio(sonidoDescanso)
            timerJob = viewModelScope.launch {
                while (_timer.value > 0) {
                    delay(1000)
                    _timer.value -= 1
                }
                nextStep()
            }
        }
    }

    fun nextStep() {
        timerJob?.cancel()
        if (_currentStep.value < _rutinaEjercicios.value.size - 1) {
            _currentStep.value += 1
            startStep()
        } else {
            playAudio("finish")
            // Rutina terminada: registrar progreso automático
            registrarProgresoAutomatico()
        }
    }

    private fun registrarProgresoAutomatico() {
        viewModelScope.launch {
            val fecha = System.currentTimeMillis()
            _rutinaEjercicios.value.forEach { re ->
                progresoUseCases.insert(
                    Progreso(
                        ejercicioId = re.ejercicioId,
                        fecha = fecha,
                        peso = null, // Si tienes el dato, ponlo aquí
                        repeticiones = re.repeticiones,
                        tiempo = re.tiempo,
                        distancia = null, // Si aplica
                        notas = "Registro automático"
                    )
                )
            }
        }
    }

    fun pause() {
        timerJob?.cancel()
    }

    fun resume() {
        if (_isRest.value) startRest() else startStep()
    }

    fun skip() {
        nextStep()
    }

    fun repeat() {
        startStep()
    }

    private fun playAudio(sonido: String) {
        if (!sonidosActivados) return
        // Aquí deberías cargar y reproducir el archivo de sonido personalizado (por nombre/ruta)
        // Ejemplo: MediaPlayer.create(getApplication(), R.raw.start).start()
        // Si tienes la ruta, puedes usar MediaPlayer con Uri.parse(sonido)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
