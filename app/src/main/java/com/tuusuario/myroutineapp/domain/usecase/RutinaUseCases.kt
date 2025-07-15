package com.tuusuario.myroutineapp.domain.usecase

import com.tuusuario.myroutineapp.data.dao.RutinaDao
import com.tuusuario.myroutineapp.data.entities.Rutina

class RutinaUseCases(private val dao: RutinaDao) {
    fun getAll() = dao.getAll()
    suspend fun insert(rutina: Rutina) = dao.insert(rutina)
    suspend fun update(rutina: Rutina) = dao.update(rutina)
    suspend fun delete(rutina: Rutina) = dao.delete(rutina)
    suspend fun getById(id: Long) = dao.getById(id)
}
