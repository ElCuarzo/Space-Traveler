package com.example.spacetraveler.data.di

import com.example.spacetraveler.data.local.MissionDao
import com.example.spacetraveler.data.local.OfflineOperationDao
import com.example.spacetraveler.data.remote.MissionApi
import com.example.spacetraveler.data.repository.MissionRepositoryImpl
import com.example.spacetraveler.data.repository.OfflineOperationsImpl
import com.example.spacetraveler.domain.repository.MissionRepository
import com.example.spacetraveler.domain.repository.OfflineOperationsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMissionRepository(
        api: MissionApi,
        missionDao: MissionDao,
        offlineDao: OfflineOperationDao
    ): MissionRepository = MissionRepositoryImpl(api, offlineDao, missionDao)

    @Provides
    @Singleton
    fun provideOfflineOperationsRepository(
        api: MissionApi,
        missionDao: MissionDao,
        offlineDao: OfflineOperationDao
    ): OfflineOperationsRepository = OfflineOperationsImpl(api, offlineDao, missionDao)
}
