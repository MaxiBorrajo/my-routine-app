package com.tuusuario.myroutineapp.data.dao

import androidx.room.*
import com.tuusuario.myroutineapp.data.entities.Nota
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {
    @Query("SELECT * FROM notas ORDER BY fecha DESC")
    fun getAll(): Flow<List<Nota>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(nota: Nota): Long

    @Update
    suspend fun update(nota: Nota)

    @Delete
    suspend fun delete(nota: Nota)
}
