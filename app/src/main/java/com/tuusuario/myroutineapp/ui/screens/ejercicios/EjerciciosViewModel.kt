package com.tuusuario.myroutineapp.ui.screens.ejercicios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuusuario.myroutineapp.data.entities.Ejercicio
import com.tuusuario.myroutineapp.domain.usecase.EjercicioUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EjerciciosViewModel @Inject constructor(
    private val ejercicioUseCases: EjercicioUseCases
) : ViewModel() {
    val ejercicios: StateFlow<List<Ejercicio>> = ejercicioUseCases.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun eliminarEjercicio(ejercicio: Ejercicio) {
        viewModelScope.launch {
            ejercicioUseCases.delete(ejercicio)
        }
    }

    fun guardarEjercicio(ejercicio: Ejercicio) {
        viewModelScope.launch {
            if (ejercicio.id == 0L) {
                ejercicioUseCases.insert(ejercicio)
            } else {
                ejercicioUseCases.update(ejercicio)
            }
        }
    }
}
