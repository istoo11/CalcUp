package com.example.calcup.Objetos

import kotlinx.serialization.Serializable


@Serializable
data class Laderboard(
    val id_usuario: String,
    val id_nivel: Int,
)