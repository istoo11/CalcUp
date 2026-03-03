package com.example.calcup.Objetos

import java.io.Serializable

data class NivelUI(
    val nivel: Int,
    val descripcion: String,
    var desbloqueado: Boolean = false,
    val consejos: List<String>,
) : Serializable
