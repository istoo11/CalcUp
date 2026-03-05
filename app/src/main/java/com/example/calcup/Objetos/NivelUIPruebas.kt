package com.example.calcup.Objetos

import java.io.Serializable


data class NivelUIPruebas(
    val nivel: Int,
    val descripcion: String,
    var desbloqueado: Boolean = false,
    val consejos: List<String>,
    val EJ01: List<String>,
    val SolucionEJ01: List<String>,
    val EJ02: List<String>,
    val SolucionEJ02: List<String>,
    val EJ03: List<String>,
    val SolucionEJ03: List<String>
) : Serializable
