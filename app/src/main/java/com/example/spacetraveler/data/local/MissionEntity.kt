package com.example.spacetraveler.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val planetaDestino: String,
    val fechaLanzamiento: String,
    val descripcion: String,
    val isSynced: Boolean = true
)
