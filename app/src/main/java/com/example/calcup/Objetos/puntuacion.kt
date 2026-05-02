package com.example.calcup.Objetos

import kotlinx.serialization.Serializable

@Serializable
data class puntuacion(
    val id: Long? = null,
    val id_usuario: String,
    val id_nivel: Int,
    val comienzo: String? = null,
    val fin: String? = null
)