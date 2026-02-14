package com.example.spacetraveler.data.di

import android.app.Application
import androidx.room.Room
import com.example.spacetraveler.data.local.AppDatabase
import com.example.spacetraveler.data.local.MissionDao
import com.example.spacetraveler.data.local.OfflineOperationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "space_traveler_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMissionDao(db: AppDatabase): MissionDao = db.missionDao()

    @Provides
    fun provideOfflineOperationDao(db: AppDatabase): OfflineOperationDao = db.offlineOperationDao()
}