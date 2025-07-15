package com.tuusuario.myroutineapp.domain.usecase

import com.tuusuario.myroutineapp.data.dao.RutinaEjercicioDao
import com.tuusuario.myroutineapp.data.entities.RutinaEjercicio

class RutinaEjercicioUseCases(private val dao: RutinaEjercicioDao) {
    fun getByRutina(rutinaId: Long) = dao.getByRutina(rutinaId)
    suspend fun insert(rutinaEjercicio: RutinaEjercicio) = dao.insert(rutinaEjercicio)
    suspend fun update(rutinaEjercicio: RutinaEjercicio) = dao.update(rutinaEjercicio)
    suspend fun delete(rutinaEjercicio: RutinaEjercicio) = dao.delete(rutinaEjercicio)
}
