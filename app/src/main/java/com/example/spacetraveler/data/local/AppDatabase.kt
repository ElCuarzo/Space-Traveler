package com.example.spacetraveler.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MissionEntity::class, OfflineOperation::class],
    version = 1,
    exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    abstract fun missionDao(): MissionDao
    abstract fun offlineOperationDao(): OfflineOperationDao

}
