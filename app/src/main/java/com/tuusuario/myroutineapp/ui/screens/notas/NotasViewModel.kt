package com.tuusuario.myroutineapp.ui.screens.notas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuusuario.myroutineapp.data.entities.Nota
import com.tuusuario.myroutineapp.domain.usecase.NotaUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotasViewModel @Inject constructor(
    private val notaUseCases: NotaUseCases
) : ViewModel() {
    val notas: StateFlow<List<Nota>> = notaUseCases.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun eliminarNota(nota: Nota) {
        viewModelScope.launch {
            notaUseCases.delete(nota)
        }
    }

    fun guardarNota(nota: Nota) {
        viewModelScope.launch {
            if (nota.id == 0L) {
                notaUseCases.insert(nota)
            } else {
                notaUseCases.update(nota)
            }
        }
    }
}
