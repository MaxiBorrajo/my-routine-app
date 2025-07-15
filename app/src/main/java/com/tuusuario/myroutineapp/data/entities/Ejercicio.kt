package com.tuusuario.myroutineapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ejercicios")
data class Ejercicio(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val descripcion: String?,
    val gruposMusculares: String, // Ej: "Pecho,Tríceps"
    val tipoSerie: String, // "repeticiones", "tiempo", "distancia"
    val peso: Float?,
    val intensidad: String, // "baja", "media", "alta"
    val mediaUri: String? // Foto/video/link
)
