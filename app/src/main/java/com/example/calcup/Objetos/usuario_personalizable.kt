package com.example.calcup.Objetos

import kotlinx.serialization.Serializable

@Serializable
data class usuario_personalizable(
    val id_usuario: String,
    val id_cosmetico: Int
)