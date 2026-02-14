package com.example.spacetraveler.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MissionEntity::class, OfflineOperation::class],
    version = 1,
    exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    abstract fun missionDao(): MissionDao
    abstract fun offlineOperationDao(): OfflineOperationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "space_traveler_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
