package com.example.calcetinder.datos

import androidx.room.*
import com.example.calcetinder.modelo.Match
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDAO {
    @Insert suspend fun insertar(match: Match)
    @Query("SELECT * FROM matches WHERE usuarioId = :usuarioId AND tipoMatch = 'like'")
    fun obtenerMatches(usuarioId: Int): Flow<List<Match>>
    @Query("SELECT * FROM matches")
    fun obtenerTodos(): Flow<List<Match>>
}
