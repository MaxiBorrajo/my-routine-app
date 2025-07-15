package com.tuusuario.myroutineapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progresos")
data class Progreso(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ejercicioId: Long,
    val fecha: Long, // timestamp
    val peso: Float?,
    val repeticiones: Int?,
    val tiempo: Int?, // segundos
    val distancia: Float?, // km
    val notas: String?
)
