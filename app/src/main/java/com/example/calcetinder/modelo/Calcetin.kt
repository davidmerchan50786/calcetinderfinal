package com.example.calcetinder.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calcetines")
data class Calcetin(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val nombre: String,
    val descripcion: String,
    val color: String,
    val material: String = "",
    val imagenUri: String = ""
)
