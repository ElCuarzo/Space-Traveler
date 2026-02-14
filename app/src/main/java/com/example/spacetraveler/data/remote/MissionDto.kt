package com.example.spacetraveler.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class MissionDto(
    val id: Int,
    val name: String,
    val planetaDestino: String,
    val fechaLanzamiento: String,
    val descripcion: String
)
