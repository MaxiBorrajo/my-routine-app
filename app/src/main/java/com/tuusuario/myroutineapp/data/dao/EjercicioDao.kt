package com.tuusuario.myroutineapp.data.dao

import androidx.room.*
import com.tuusuario.myroutineapp.data.entities.Ejercicio
import kotlinx.coroutines.flow.Flow

@Dao
interface EjercicioDao {
    @Query("SELECT * FROM ejercicios ORDER BY nombre")
    fun getAll(): Flow<List<Ejercicio>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ejercicio: Ejercicio): Long

    @Update
    suspend fun update(ejercicio: Ejercicio)

    @Delete
    suspend fun delete(ejercicio: Ejercicio)

    @Query("SELECT * FROM ejercicios WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Ejercicio?
}
