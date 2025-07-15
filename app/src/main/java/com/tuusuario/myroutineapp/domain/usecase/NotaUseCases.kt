package com.tuusuario.myroutineapp.domain.usecase

import com.tuusuario.myroutineapp.data.dao.NotaDao
import com.tuusuario.myroutineapp.data.entities.Nota

class NotaUseCases(private val dao: NotaDao) {
    fun getAll() = dao.getAll()
    suspend fun insert(nota: Nota) = dao.insert(nota)
    suspend fun update(nota: Nota) = dao.update(nota)
    suspend fun delete(nota: Nota) = dao.delete(nota)
}
