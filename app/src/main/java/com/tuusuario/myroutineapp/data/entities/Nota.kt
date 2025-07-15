package com.tuusuario.myroutineapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notas")
data class Nota(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fecha: Long,
    val texto: String,
    val rutinaId: Long? = null,
    val ejercicioId: Long? = null
)
