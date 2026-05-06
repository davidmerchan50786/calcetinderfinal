package com.example.calcetinder.datos

import com.example.calcetinder.modelo.Calcetin
import com.example.calcetinder.modelo.Match
import com.example.calcetinder.modelo.Usuario
import kotlinx.coroutines.flow.Flow

class Repositorio(
    private val usuarioDAO: UsuarioDAO,
    private val calcetinDAO: CalcetinDAO,
    private val matchDAO: MatchDAO
) {
    suspend fun insertarUsuario(u: Usuario): Long = usuarioDAO.insertar(u)
    suspend fun loginUsuario(email: String, pass: String): Usuario? = usuarioDAO.login(email, pass)
    fun obtenerUsuario(id: Int): Flow<Usuario?> = usuarioDAO.obtener(id)
    suspend fun insertarCalcetin(c: Calcetin) = calcetinDAO.insertar(c)
    suspend fun actualizarCalcetin(c: Calcetin) = calcetinDAO.actualizar(c)
    suspend fun eliminarCalcetin(c: Calcetin) = calcetinDAO.eliminar(c)
    fun obtenerCalcetin(id: Int): Flow<Calcetin?> = calcetinDAO.obtener(id)
    fun obtenerCalcetinesPorUsuario(uid: Int): Flow<List<Calcetin>> = calcetinDAO.obtenerPorUsuario(uid)
    fun obtenerTodosCalcetines(): Flow<List<Calcetin>> = calcetinDAO.obtenerTodos()
    suspend fun insertarMatch(m: Match) = matchDAO.insertar(m)
    fun obtenerMatches(uid: Int): Flow<List<Match>> = matchDAO.obtenerMatches(uid)
}
