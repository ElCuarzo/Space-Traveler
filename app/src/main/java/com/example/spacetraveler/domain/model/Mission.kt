package com.example.spacetraveler.domain.model

data class Mission(
    val id: Int,
    val nombre: String,
    val planetaDestino: String,
    val fechaLanzamiento: String,
    val descripcion: String
)
