package com.example.calcetinder.datos

import androidx.room.*
import com.example.calcetinder.modelo.Calcetin
import kotlinx.coroutines.flow.Flow

@Dao
interface CalcetinDAO {
    @Insert suspend fun insertar(calcetin: Calcetin)
    @Update suspend fun actualizar(calcetin: Calcetin)
    @Delete suspend fun eliminar(calcetin: Calcetin)
    @Query("SELECT * FROM calcetines WHERE id = :id")
    fun obtener(id: Int): Flow<Calcetin?>
    @Query("SELECT * FROM calcetines WHERE usuarioId = :usuarioId")
    fun obtenerPorUsuario(usuarioId: Int): Flow<List<Calcetin>>
    @Query("SELECT * FROM calcetines")
    fun obtenerTodos(): Flow<List<Calcetin>>
}
