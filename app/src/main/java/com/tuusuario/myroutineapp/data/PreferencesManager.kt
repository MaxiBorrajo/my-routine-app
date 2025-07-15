package com.tuusuario.myroutineapp.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesManager(private val context: Context) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "config_prefs")
        val SONIDOS_KEY = booleanPreferencesKey("sonidos")
        val DESCANSO_KEY = intPreferencesKey("descanso")
        val UNIDAD_PESO_KEY = stringPreferencesKey("unidad_peso")
        val UNIDAD_DISTANCIA_KEY = stringPreferencesKey("unidad_distancia")
        val SONIDO_INICIO_KEY = stringPreferencesKey("sonido_inicio")
        val SONIDO_FIN_SERIE_KEY = stringPreferencesKey("sonido_fin_serie")
        val SONIDO_DESCANSO_KEY = stringPreferencesKey("sonido_descanso")
        val PREPARACION_KEY = intPreferencesKey("preparacion")
    }

    val sonidos: Flow<Boolean> = context.dataStore.data.map { it[SONIDOS_KEY] ?: true }
    val descanso: Flow<Int> = context.dataStore.data.map { it[DESCANSO_KEY] ?: 60 }
    val unidadPeso: Flow<String> = context.dataStore.data.map { it[UNIDAD_PESO_KEY] ?: "kg" }
    val unidadDistancia: Flow<String> = context.dataStore.data.map { it[UNIDAD_DISTANCIA_KEY] ?: "km" }
    val sonidoInicio: Flow<String> = context.dataStore.data.map { it[SONIDO_INICIO_KEY] ?: "default_inicio.mp3" }
    val sonidoFinSerie: Flow<String> = context.dataStore.data.map { it[SONIDO_FIN_SERIE_KEY] ?: "default_fin_serie.mp3" }
    val sonidoDescanso: Flow<String> = context.dataStore.data.map { it[SONIDO_DESCANSO_KEY] ?: "default_descanso.mp3" }
    val preparacion: Flow<Int> = context.dataStore.data.map { it[PREPARACION_KEY] ?: 10 }

    suspend fun setSonidos(value: Boolean) {
        context.dataStore.edit { it[SONIDOS_KEY] = value }
    }
    suspend fun setDescanso(value: Int) {
        context.dataStore.edit { it[DESCANSO_KEY] = value }
    }
    suspend fun setUnidadPeso(value: String) {
        context.dataStore.edit { it[UNIDAD_PESO_KEY] = value }
    }
    suspend fun setUnidadDistancia(value: String) {
        context.dataStore.edit { it[UNIDAD_DISTANCIA_KEY] = value }
    }
    suspend fun setSonidoInicio(value: String) {
        context.dataStore.edit { it[SONIDO_INICIO_KEY] = value }
    }
    suspend fun setSonidoFinSerie(value: String) {
        context.dataStore.edit { it[SONIDO_FIN_SERIE_KEY] = value }
    }
    suspend fun setSonidoDescanso(value: String) {
        context.dataStore.edit { it[SONIDO_DESCANSO_KEY] = value }
    }
    suspend fun setPreparacion(value: Int) {
        context.dataStore.edit { it[PREPARACION_KEY] = value }
    }
}
