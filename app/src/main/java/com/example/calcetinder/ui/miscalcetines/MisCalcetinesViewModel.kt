package com.example.calcetinder.ui.miscalcetines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calcetinder.datos.Repositorio
import com.example.calcetinder.modelo.Calcetin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MisCalcetinesViewModel(private val repo: Repositorio) : ViewModel() {
    private val _calcetines = MutableStateFlow<List<Calcetin>>(emptyList())
    val misCalcetines: StateFlow<List<Calcetin>> = _calcetines

    fun cargarCalcetinesPorUsuario(uid: Int) {
        viewModelScope.launch { repo.obtenerCalcetinesPorUsuario(uid).collect { _calcetines.value = it } }
    }
    fun crearCalcetin(uid: Int, nombre: String, desc: String, color: String, material: String, imagenUri: String = "") {
        viewModelScope.launch {
            repo.insertarCalcetin(Calcetin(usuarioId = uid, nombre = nombre, descripcion = desc, color = color, material = material, imagenUri = imagenUri))
        }
    }
    fun eliminarCalcetin(c: Calcetin) { viewModelScope.launch { repo.eliminarCalcetin(c) } }
    fun actualizarCalcetin(c: Calcetin) { viewModelScope.launch { repo.actualizarCalcetin(c) } }
}
