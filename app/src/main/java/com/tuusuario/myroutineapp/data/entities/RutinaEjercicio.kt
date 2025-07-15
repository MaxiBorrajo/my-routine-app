package com.tuusuario.myroutineapp.data.entities

import androidx.room.Entity

@Entity(
    tableName = "rutina_ejercicios",
    primaryKeys = ["rutinaId", "orden"]
)
data class RutinaEjercicio(
    val rutinaId: Long,
    val ejercicioId: Long,
    val orden: Int,
    val series: Int,
    val repeticiones: Int?,
    val tiempo: Int?, // segundos
    val descansoEntreSeries: Int, // segundos
    val descansoEntreEjercicios: Int, // segundos
    val notas: String?
)
