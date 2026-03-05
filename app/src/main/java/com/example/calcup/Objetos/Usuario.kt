package com.example.calcup.Objetos

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: String,
    val usuario: String,
    val puntos: Int,
    val nivel: Int,
    val id_icono: Int,
    val id_color: Int,
)