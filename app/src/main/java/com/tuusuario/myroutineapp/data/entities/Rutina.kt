package com.tuusuario.myroutineapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rutinas")
data class Rutina(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val notas: String?,
    val tiempoPreparacion: Int // segundos
)
