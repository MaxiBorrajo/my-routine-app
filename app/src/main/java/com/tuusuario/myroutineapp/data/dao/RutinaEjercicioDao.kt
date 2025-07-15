package com.tuusuario.myroutineapp.data.dao

import androidx.room.*
import com.tuusuario.myroutineapp.data.entities.RutinaEjercicio
import kotlinx.coroutines.flow.Flow

@Dao
interface RutinaEjercicioDao {
    @Query("SELECT * FROM rutina_ejercicios WHERE rutinaId = :rutinaId ORDER BY orden")
    fun getByRutina(rutinaId: Long): Flow<List<RutinaEjercicio>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rutinaEjercicio: RutinaEjercicio)

    @Update
    suspend fun update(rutinaEjercicio: RutinaEjercicio)

    @Delete
    suspend fun delete(rutinaEjercicio: RutinaEjercicio)
}
