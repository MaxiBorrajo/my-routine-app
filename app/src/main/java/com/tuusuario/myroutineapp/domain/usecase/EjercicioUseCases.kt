package com.tuusuario.myroutineapp.domain.usecase

import com.tuusuario.myroutineapp.data.dao.EjercicioDao
import com.tuusuario.myroutineapp.data.entities.Ejercicio

class EjercicioUseCases(private val dao: EjercicioDao) {
    fun getAll() = dao.getAll()
    suspend fun insert(ejercicio: Ejercicio) = dao.insert(ejercicio)
    suspend fun update(ejercicio: Ejercicio) = dao.update(ejercicio)
    suspend fun delete(ejercicio: Ejercicio) = dao.delete(ejercicio)
    suspend fun getById(id: Long) = dao.getById(id)
}
