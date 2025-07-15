package com.tuusuario.myroutineapp.domain.usecase

import com.tuusuario.myroutineapp.data.dao.ProgresoDao
import com.tuusuario.myroutineapp.data.entities.Progreso

class ProgresoUseCases(private val dao: ProgresoDao) {
    fun getByEjercicio(ejercicioId: Long) = dao.getByEjercicio(ejercicioId)
    suspend fun insert(progreso: Progreso) = dao.insert(progreso)
    suspend fun update(progreso: Progreso) = dao.update(progreso)
    suspend fun delete(progreso: Progreso) = dao.delete(progreso)
}
