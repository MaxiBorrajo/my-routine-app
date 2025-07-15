package com.tuusuario.myroutineapp.service

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.tuusuario.myroutineapp.MainActivity
import com.tuusuario.myroutineapp.R
import com.tuusuario.myroutineapp.data.dao.RutinaEjercicioDao
import com.tuusuario.myroutineapp.data.dao.EjercicioDao
import com.tuusuario.myroutineapp.data.entities.RutinaEjercicio
import com.tuusuario.myroutineapp.data.entities.Ejercicio
import com.tuusuario.myroutineapp.domain.usecase.ProgresoUseCases
import com.tuusuario.myroutineapp.data.entities.Progreso
import com.tuusuario.myroutineapp.data.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@AndroidEntryPoint
class PlayRoutineService : Service() {

    @Inject
    lateinit var rutinaEjercicioDao: RutinaEjercicioDao
    
    @Inject
    lateinit var ejercicioDao: EjercicioDao
    
    @Inject
    lateinit var progresoUseCases: ProgresoUseCases
    
    @Inject
    lateinit var preferencesManager: PreferencesManager

    private val binder = PlayRoutineBinder()
    private var mediaSession: MediaSessionCompat? = null
    private var mediaPlayer: MediaPlayer? = null
    private var timerJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Estados del servicio
    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep

    private val _timer = MutableStateFlow(0)
    val timer: StateFlow<Int> = _timer

    private val _isRest = MutableStateFlow(false)
    val isRest: StateFlow<Boolean> = _isRest

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused

    private val _rutinaEjercicios = MutableStateFlow<List<RutinaEjercicio>>(emptyList())
    val rutinaEjercicios: StateFlow<List<RutinaEjercicio>> = _rutinaEjercicios

    private val _ejercicios = MutableStateFlow<Map<Long, Ejercicio>>(emptyMap())
    val ejercicios: StateFlow<Map<Long, Ejercicio>> = _ejercicios

    // Configuración
    private var sonidoInicio: String = "default_inicio.mp3"
    private var sonidoFinSerie: String = "default_fin_serie.mp3"
    private var sonidoDescanso: String = "default_descanso.mp3"
    private var preparacion: Int = 10
    private var descansoPorDefecto: Int = 60
    private var sonidosActivados: Boolean = true

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "play_routine_channel"
        private const val ACTION_PLAY = "action_play"
        private const val ACTION_PAUSE = "action_pause"
        private const val ACTION_SKIP = "action_skip"
        private const val ACTION_STOP = "action_stop"
    }

    inner class PlayRoutineBinder : Binder() {
        fun getService(): PlayRoutineService = this@PlayRoutineService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        setupMediaSession()
        loadPreferences()
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> resume()
            ACTION_PAUSE -> pause()
            ACTION_SKIP -> skip()
            ACTION_STOP -> stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Ejecución de Rutina",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Canal para notificaciones de ejecución de rutinas"
            setShowBadge(false)
        }
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(this, "PlayRoutineService").apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            setPlaybackState(PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                .build())
            isActive = true
        }
    }

    private fun loadPreferences() {
        serviceScope.launch {
            preferencesManager.sonidoInicio.collect { sonidoInicio = it }
        }
        serviceScope.launch {
            preferencesManager.sonidoFinSerie.collect { sonidoFinSerie = it }
        }
        serviceScope.launch {
            preferencesManager.sonidoDescanso.collect { sonidoDescanso = it }
        }
        serviceScope.launch {
            preferencesManager.preparacion.collect { preparacion = it }
        }
        serviceScope.launch {
            preferencesManager.descanso.collect { descansoPorDefecto = it }
        }
        serviceScope.launch {
            preferencesManager.sonidos.collect { sonidosActivados = it }
        }
    }

    fun startRutina(rutinaId: Long) {
        serviceScope.launch {
            rutinaEjercicioDao.getByRutina(rutinaId).collect { lista ->
                _rutinaEjercicios.value = lista
                val ids = lista.map { it.ejercicioId }
                val ejerciciosMap = ids.associateWith { ejercicioDao.getById(it) }
                _ejercicios.value = ejerciciosMap.filterValues { it != null } as Map<Long, Ejercicio>
            }
        }
        _currentStep.value = 0
        _isPaused.value = false
        startForeground(NOTIFICATION_ID, createNotification())
        startPreparacion()
    }

    private fun startPreparacion() {
        timerJob?.cancel()
        _isRest.value = false
        _timer.value = preparacion
        playAudio(sonidoInicio)
        updateNotification()
        
        timerJob = serviceScope.launch {
            while (_timer.value > 0 && !_isPaused.value) {
                delay(1000)
                _timer.value -= 1
                updateNotification()
            }
            if (!_isPaused.value) {
                startStep()
            }
        }
    }

    fun startStep() {
        timerJob?.cancel()
        val step = _rutinaEjercicios.value.getOrNull(_currentStep.value)
        if (step != null) {
            _isRest.value = false
            _timer.value = step.tiempo ?: 0
            playAudio(sonidoInicio)
            updateNotification()
            
            timerJob = serviceScope.launch {
                while (_timer.value > 0 && !_isPaused.value) {
                    delay(1000)
                    _timer.value -= 1
                    updateNotification()
                }
                if (!_isPaused.value) {
                    playAudio(sonidoFinSerie)
                    startRest()
                }
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
            updateNotification()
            
            timerJob = serviceScope.launch {
                while (_timer.value > 0 && !_isPaused.value) {
                    delay(1000)
                    _timer.value -= 1
                    updateNotification()
                }
                if (!_isPaused.value) {
                    nextStep()
                }
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
            registrarProgresoAutomatico()
            stopSelf()
        }
    }

    fun pause() {
        _isPaused.value = true
        timerJob?.cancel()
        updateNotification()
        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PAUSED, 0, 1.0f)
            .build())
    }

    fun resume() {
        _isPaused.value = false
        updateNotification()
        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
            .build())
        
        if (_isRest.value) startRest() else startStep()
    }

    fun skip() {
        nextStep()
    }

    private fun registrarProgresoAutomatico() {
        serviceScope.launch {
            val fecha = System.currentTimeMillis()
            _rutinaEjercicios.value.forEach { re ->
                progresoUseCases.insert(
                    Progreso(
                        ejercicioId = re.ejercicioId,
                        fecha = fecha,
                        peso = null,
                        repeticiones = re.repeticiones,
                        tiempo = re.tiempo,
                        distancia = null,
                        notas = "Registro automático"
                    )
                )
            }
        }
    }

    private fun playAudio(sonido: String) {
        if (!sonidosActivados) return
        
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer()
            
            // Aquí deberías cargar el archivo de sonido personalizado
            // Por ahora usamos un sonido del sistema como placeholder
            when (sonido) {
                "finish" -> {
                    // Sonido de finalización
                    val notification = getSystemService(NotificationManager::class.java)
                    notification.notify(NOTIFICATION_ID, createNotification())
                }
                else -> {
                    // Cargar sonido personalizado desde assets o storage
                    // mediaPlayer?.setDataSource(sonido)
                    // mediaPlayer?.prepare()
                    // mediaPlayer?.start()
                }
            }
        } catch (e: Exception) {
            // Manejar error de audio
        }
    }

    private fun createNotification(): Notification {
        val step = _rutinaEjercicios.value.getOrNull(_currentStep.value)
        val ejercicio = step?.let { _ejercicios.value[it.ejercicioId] }
        
        val title = if (_isRest.value) "Descanso" else ejercicio?.nombre ?: "Preparación"
        val content = "Tiempo restante: ${_timer.value}s"
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val playPauseAction = if (_isPaused.value) {
            NotificationCompat.Action(
                R.drawable.ic_play_arrow,
                "Reanudar",
                PendingIntent.getService(this, 0, Intent(this, PlayRoutineService::class.java).setAction(ACTION_PLAY), PendingIntent.FLAG_IMMUTABLE)
            )
        } else {
            NotificationCompat.Action(
                R.drawable.ic_pause,
                "Pausar",
                PendingIntent.getService(this, 0, Intent(this, PlayRoutineService::class.java).setAction(ACTION_PAUSE), PendingIntent.FLAG_IMMUTABLE)
            )
        }

        val skipAction = NotificationCompat.Action(
            R.drawable.ic_skip_next,
            "Siguiente",
            PendingIntent.getService(this, 0, Intent(this, PlayRoutineService::class.java).setAction(ACTION_SKIP), PendingIntent.FLAG_IMMUTABLE)
        )

        val stopAction = NotificationCompat.Action(
            R.drawable.ic_stop,
            "Detener",
            PendingIntent.getService(this, 0, Intent(this, PlayRoutineService::class.java).setAction(ACTION_STOP), PendingIntent.FLAG_IMMUTABLE)
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_fitness_center)
            .setContentIntent(pendingIntent)
            .addAction(playPauseAction)
            .addAction(skipAction)
            .addAction(stopAction)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        mediaPlayer?.release()
        mediaSession?.release()
        serviceScope.cancel()
    }
}
