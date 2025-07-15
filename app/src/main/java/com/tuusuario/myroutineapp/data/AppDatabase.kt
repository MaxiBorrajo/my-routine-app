package com.tuusuario.myroutineapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tuusuario.myroutineapp.data.dao.*
import com.tuusuario.myroutineapp.data.entities.*

@Database(
    entities = [
        Ejercicio::class,
        Rutina::class,
        RutinaEjercicio::class,
        Progreso::class,
        Nota::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ejercicioDao(): EjercicioDao
    abstract fun rutinaDao(): RutinaDao
    abstract fun rutinaEjercicioDao(): RutinaEjercicioDao
    abstract fun progresoDao(): ProgresoDao
    abstract fun notaDao(): NotaDao
}
