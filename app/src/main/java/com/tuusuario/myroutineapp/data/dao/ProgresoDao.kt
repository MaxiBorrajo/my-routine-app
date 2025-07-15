package com.tuusuario.myroutineapp.data.dao

import androidx.room.*
import com.tuusuario.myroutineapp.data.entities.Progreso
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgresoDao {
    @Query("SELECT * FROM progresos WHERE ejercicioId = :ejercicioId ORDER BY fecha DESC")
    fun getByEjercicio(ejercicioId: Long): Flow<List<Progreso>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progreso: Progreso): Long

    @Update
    suspend fun update(progreso: Progreso)

    @Delete
    suspend fun delete(progreso: Progreso)
}
