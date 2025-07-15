package com.tuusuario.myroutineapp.ui.screens.rutinas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuusuario.myroutineapp.data.entities.Rutina
import com.tuusuario.myroutineapp.domain.usecase.RutinaUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RutinasViewModel @Inject constructor(
    private val rutinaUseCases: RutinaUseCases
) : ViewModel() {
    val rutinas: StateFlow<List<Rutina>> = rutinaUseCases.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun eliminarRutina(rutina: Rutina) {
        viewModelScope.launch {
            rutinaUseCases.delete(rutina)
        }
    }

    fun guardarRutina(rutina: Rutina) {
        viewModelScope.launch {
            if (rutina.id == 0L) {
                rutinaUseCases.insert(rutina)
            } else {
                rutinaUseCases.update(rutina)
            }
        }
    }
}
