package com.example.spacetraveler.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {

    @Query("SELECT * FROM missions ORDER BY fechaLanzamiento DESC")
    fun getAllMissions(): Flow<List<MissionEntity>>

    @Query("SELECT * FROM missions WHERE id = :id")
    suspend fun getMissionById(id: Int): MissionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: MissionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissions(missions: List<MissionEntity>)

    @Query("SELECT * FROM missions WHERE isSynced = 0")
    suspend fun getPendingMissions(): List<MissionEntity>

    @Update
    suspend fun updateMission(mission: MissionEntity)

    @Query("DELETE FROM missions WHERE id = :missionId")
    suspend fun deleteMissionById(missionId: Int)

    @Query("UPDATE missions SET isSynced = 1 WHERE id = :missionId")
    suspend fun markMissionAsSynced(missionId: Int)
}
