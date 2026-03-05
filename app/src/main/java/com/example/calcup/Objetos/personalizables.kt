package com.example.calcup.Objetos

import java.io.Serializable

data class personalizables(
    val id: Int,
    val tipo: String,
    var clave: String,
    var descripcion: String
) : Serializable