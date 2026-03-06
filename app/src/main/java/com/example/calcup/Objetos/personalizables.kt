package com.example.calcup.Objetos

import kotlinx.serialization.Serializable

@Serializable
data class personalizables(
    val id: Int,
    val tipo: String,
    var clave: String,
    var descripcion: String
)