package com.example.spacetraveler.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_operations")
data class OfflineOperation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val missionId: Int
)