package com.tuusuario.myroutineapp.ui.screens.play

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuusuario.myroutineapp.data.dao.RutinaEjercicioDao
import com.tuusuario.myroutineapp.data.dao.EjercicioDao
import com.tuusuario.myroutineapp.data.entities.RutinaEjercicio
import com.tuusuario.myroutineapp.data.entities.Ejercicio
import com.tuusuario.myroutineapp.domain.usecase.ProgresoUseCases
import com.tuusuario.myroutineapp.data.entities.Progreso
import com.tuusuario.myroutineapp.data.PreferencesManager
import com.tuusuario.myroutineapp.service.PlayRoutineService
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private var playRoutineService: PlayRoutineService? = null
    private var bound = false

    // Estados delegados al servicio
    val currentStep: StateFlow<Int> = MutableStateFlow(0)
    val timer: StateFlow<Int> = MutableStateFlow(0)
    val isRest: StateFlow<Boolean> = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = MutableStateFlow(false)
    val rutinaEjercicios: StateFlow<List<RutinaEjercicio>> = MutableStateFlow(emptyList())
    val ejercicios: StateFlow<Map<Long, Ejercicio>> = MutableStateFlow(emptyMap())

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as PlayRoutineService.PlayRoutineBinder
            playRoutineService = binder.getService()
            bound = true
            
            // Observar estados del servicio
            viewModelScope.launch {
                playRoutineService?.currentStep?.collect { step ->
                    (currentStep as MutableStateFlow).value = step
                }
            }
            viewModelScope.launch {
                playRoutineService?.timer?.collect { time ->
                    (timer as MutableStateFlow).value = time
                }
            }
            viewModelScope.launch {
                playRoutineService?.isRest?.collect { rest ->
                    (isRest as MutableStateFlow).value = rest
                }
            }
            viewModelScope.launch {
                playRoutineService?.isPaused?.collect { paused ->
                    (isPaused as MutableStateFlow).value = paused
                }
            }
            viewModelScope.launch {
                playRoutineService?.rutinaEjercicios?.collect { ejercicios ->
                    (rutinaEjercicios as MutableStateFlow).value = ejercicios
                }
            }
            viewModelScope.launch {
                playRoutineService?.ejercicios?.collect { ejerciciosMap ->
                    (ejercicios as MutableStateFlow).value = ejerciciosMap
                }
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
        }
    }

    fun startRutina(rutinaId: Long) {
        val intent = Intent(getApplication(), PlayRoutineService::class.java)
        getApplication<Application>().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        getApplication<Application>().startService(intent)
        
        // El servicio se encargará de iniciar la rutina
        playRoutineService?.startRutina(rutinaId)
    }

    fun pause() {
        playRoutineService?.pause()
    }

    fun resume() {
        playRoutineService?.resume()
    }

    fun skip() {
        playRoutineService?.skip()
    }

    fun stop() {
        playRoutineService?.let { service ->
            getApplication<Application>().unbindService(connection)
            getApplication<Application>().stopService(Intent(getApplication(), PlayRoutineService::class.java))
        }
        bound = false
    }

    override fun onCleared() {
        super.onCleared()
        if (bound) {
            getApplication<Application>().unbindService(connection)
            bound = false
        }
    }
}
