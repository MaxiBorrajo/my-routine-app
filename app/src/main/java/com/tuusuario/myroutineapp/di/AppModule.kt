package com.tuusuario.myroutineapp.di

import android.content.Context
import androidx.room.Room
import com.tuusuario.myroutineapp.data.AppDatabase
import com.tuusuario.myroutineapp.data.PreferencesManager
import com.tuusuario.myroutineapp.data.dao.*
import com.tuusuario.myroutineapp.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext app: Context): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "myroutine-db").build()

    @Provides
    fun provideEjercicioDao(db: AppDatabase): EjercicioDao = db.ejercicioDao()
    @Provides
    fun provideRutinaDao(db: AppDatabase): RutinaDao = db.rutinaDao()
    @Provides
    fun provideRutinaEjercicioDao(db: AppDatabase): RutinaEjercicioDao = db.rutinaEjercicioDao()
    @Provides
    fun provideProgresoDao(db: AppDatabase): ProgresoDao = db.progresoDao()
    @Provides
    fun provideNotaDao(db: AppDatabase): NotaDao = db.notaDao()

    @Provides
    fun provideEjercicioUseCases(dao: EjercicioDao) = EjercicioUseCases(dao)
    @Provides
    fun provideRutinaUseCases(dao: RutinaDao) = RutinaUseCases(dao)
    @Provides
    fun provideRutinaEjercicioUseCases(dao: RutinaEjercicioDao) = RutinaEjercicioUseCases(dao)
    @Provides
    fun provideProgresoUseCases(dao: ProgresoDao) = ProgresoUseCases(dao)
    @Provides
    fun provideNotaUseCases(dao: NotaDao) = NotaUseCases(dao)

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext app: Context): PreferencesManager = PreferencesManager(app)
}
