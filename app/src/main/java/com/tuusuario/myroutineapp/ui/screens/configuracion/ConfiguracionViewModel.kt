import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuusuario.myroutineapp.data.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfiguracionViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    val sonidos = preferencesManager.sonidos.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val descanso = preferencesManager.descanso.stateIn(viewModelScope, SharingStarted.Lazily, 60)
    val unidadPeso = preferencesManager.unidadPeso.stateIn(viewModelScope, SharingStarted.Lazily, "kg")
    val unidadDistancia = preferencesManager.unidadDistancia.stateIn(viewModelScope, SharingStarted.Lazily, "km")
    val sonidoInicio = preferencesManager.sonidoInicio.stateIn(viewModelScope, SharingStarted.Lazily, "default_inicio.mp3")
    val sonidoFinSerie = preferencesManager.sonidoFinSerie.stateIn(viewModelScope, SharingStarted.Lazily, "default_fin_serie.mp3")
    val sonidoDescanso = preferencesManager.sonidoDescanso.stateIn(viewModelScope, SharingStarted.Lazily, "default_descanso.mp3")
    val preparacion = preferencesManager.preparacion.stateIn(viewModelScope, SharingStarted.Lazily, 10)

    fun setSonidos(value: Boolean) {
        viewModelScope.launch {
            preferencesManager.setSonidos(value)
        }
    }

    fun setDescanso(value: Int) {
        viewModelScope.launch {
            preferencesManager.setDescanso(value)
        }
    }

    fun setUnidadPeso(value: String) {
        viewModelScope.launch {
            preferencesManager.setUnidadPeso(value)
        }
    }

    fun setUnidadDistancia(value: String) {
        viewModelScope.launch {
            preferencesManager.setUnidadDistancia(value)
        }
    }

    fun setSonidoInicio(value: String) {
        viewModelScope.launch {
            preferencesManager.setSonidoInicio(value)
        }
    }

    fun setSonidoFinSerie(value: String) {
        viewModelScope.launch {
            preferencesManager.setSonidoFinSerie(value)
        }
    }

    fun setSonidoDescanso(value: String) {
        viewModelScope.launch {
            preferencesManager.setSonidoDescanso(value)
        }
    }

    fun setPreparacion(value: Int) {
        viewModelScope.launch {
            preferencesManager.setPreparacion(value)
        }
    }
}
