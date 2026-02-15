package com.example.spacetraveler.data.repository

import com.example.spacetraveler.core.Resource
import com.example.spacetraveler.core.safeApiResponse
import com.example.spacetraveler.data.local.MissionDao
import com.example.spacetraveler.data.local.MissionEntity
import com.example.spacetraveler.data.local.OfflineOperation
import com.example.spacetraveler.data.local.OfflineOperationDao
import com.example.spacetraveler.data.mapper.MissionMapper
import com.example.spacetraveler.data.remote.MissionApi
import com.example.spacetraveler.data.remote.MissionDto
import com.example.spacetraveler.domain.model.Mission
import com.example.spacetraveler.domain.repository.MissionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MissionRepositoryImpl @Inject constructor(
    private val api: MissionApi,
    private val offlineDao: OfflineOperationDao,
    private val dao: MissionDao
) : MissionRepository {

    override suspend fun getMissionByIdWithFallback(id: Int): Resource<Mission> {
        val localMission = dao.getMissionById(id)?.let { MissionMapper.fromEntityToDomain(it) }

        return try {
            when (val remote = safeApiResponse { api.getMissionById(id) }) {
                is Resource.Success -> {
                    val entity = MissionMapper.fromDtoToEntity(remote.data)
                    dao.insertMission(entity)
                    Resource.Success(MissionMapper.fromEntityToDomain(entity))
                }
                is Resource.Error -> {
                    localMission?.let { Resource.Success(it) } ?: Resource.Error(
                        remote.message,
                        remote.errorType
                    )
                }
                is Resource.Loading -> {
                    localMission?.let { Resource.Success(it) } ?: Resource.Loading
                }
            }
        } catch (e: Exception) {
            localMission?.let { Resource.Success(it) } ?: Resource.Error("Error al obtener misión: ${e.localizedMessage}")
        }
    }

    override suspend fun createMission(mission: Mission): Resource<Unit> {
        val entity = MissionMapper.fromDomainToEntity(mission, isSynced = false)
        val generatedId = dao.insertMission(entity).toInt()
        val entityWithId = entity.copy(id = generatedId)

        return when (val resource = safeApiResponse { api.createMission(MissionMapper.fromEntityToDto(entityWithId)) }) {
            is Resource.Success -> {
                dao.markMissionAsSynced(generatedId)
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                val operation = OfflineOperation(type = "create", missionId = generatedId)
                offlineDao.insertOperation(operation)
                Resource.Error(resource.message, resource.errorType)
            }
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getAllMissions(): Resource<List<Mission>> {
        val localEntities: List<MissionEntity> = dao.getAllMissions().first()
        val localMissions: List<Mission> = localEntities.map { MissionMapper.fromEntityToDomain(it) }

        return try {
            when (val remote = safeApiResponse { api.getMissions() }) {
                is Resource.Success -> {
                    val body: List<MissionDto> = remote.data
                    val entities = body.map { MissionMapper.fromDtoToEntity(it) }
                    dao.insertMissions(entities)
                    val domainList = entities.map { MissionMapper.fromEntityToDomain(it) }
                    Resource.Success(domainList)
                }
                is Resource.Error -> Resource.Success(localMissions)
                is Resource.Loading -> Resource.Success(localMissions)
            }
        } catch (e: Exception) {
            Resource.Success(localMissions)
        }
    }

    override suspend fun deleteMission(id: Int): Resource<Unit> {
        dao.deleteMissionById(id)
        val resource = safeApiResponse { api.deleteMission(id) }
        return if (resource is Resource.Success) {
            Resource.Success(Unit)
        } else if (resource is Resource.Error) {
            val operation = OfflineOperation(type = "delete", missionId = id)
            offlineDao.insertOperation(operation)
            Resource.Error(resource.message, resource.errorType)
        } else {
            Resource.Loading
        }
    }
}
