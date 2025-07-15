package com.tuusuario.myroutineapp.data.dao

import androidx.room.*
import com.tuusuario.myroutineapp.data.entities.Rutina
import kotlinx.coroutines.flow.Flow

@Dao
interface RutinaDao {
    @Query("SELECT * FROM rutinas ORDER BY nombre")
    fun getAll(): Flow<List<Rutina>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rutina: Rutina): Long

    @Update
    suspend fun update(rutina: Rutina)

    @Delete
    suspend fun delete(rutina: Rutina)

    @Query("SELECT * FROM rutinas WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Rutina?
}
