package com.example.spacetraveler.data.repository

import com.example.spacetraveler.core.NetworkError
import com.example.spacetraveler.core.Resource
import com.example.spacetraveler.core.mapToUnit
import com.example.spacetraveler.core.safeApiResponse
import com.example.spacetraveler.data.local.MissionDao
import com.example.spacetraveler.data.local.OfflineOperation
import com.example.spacetraveler.data.local.OfflineOperationDao
import com.example.spacetraveler.data.mapper.MissionMapper
import com.example.spacetraveler.data.remote.MissionApi
import com.example.spacetraveler.domain.repository.OfflineOperationsRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class OfflineOperationsImpl @Inject constructor(
    private val api: MissionApi,
    private val offlineDao: OfflineOperationDao,
    private val missionDao: MissionDao
) : OfflineOperationsRepository {

    override suspend fun syncOfflineOperations(retryDelay: Long): List<OfflineOperation> {
        cleanAppliedOperations()

        var pendingOperations = offlineDao.getAllOperations()
        val syncedOperations = mutableListOf<OfflineOperation>()
        val maxRetries = 3
        val retriesMap = mutableMapOf<Int, Int>()

        while (pendingOperations.isNotEmpty()) {
            val failedOperations = mutableListOf<OfflineOperation>()

            for (op in pendingOperations) {
                val result: Resource<Unit> = when (op.type) {
                    "delete" -> safeApiResponse { api.deleteMission(op.missionId) }.mapToUnit()
                    "create" -> {
                        val missionEntity = missionDao.getMissionById(op.missionId)
                        if (missionEntity == null) {
                            Resource.Error("Misión no encontrada en DB local", errorType = NetworkError.UNKNOWN)
                        } else {
                            safeApiResponse { api.createMission(MissionMapper.fromEntityToDto(missionEntity)) }
                                .mapToUnit()
                        }
                    }
                    else -> Resource.Error("Tipo de operación desconocida: ${op.type}", errorType = NetworkError.UNKNOWN)
                }

                when (result) {
                    is Resource.Success -> {
                        if (op.type == "create") missionDao.markMissionAsSynced(op.missionId)
                        offlineDao.deleteOperationById(op.id)
                        syncedOperations.add(op)
                    }
                    is Resource.Error -> {
                        if (result.errorType == NetworkError.NO_INTERNET || result.errorType == NetworkError.REQUEST_TIMEOUT) {
                            val retries = retriesMap.getOrDefault(op.id, 0)
                            if (retries < maxRetries) {
                                retriesMap[op.id] = retries + 1
                                failedOperations.add(op)
                            } else {
                                println("Máximo reintentos alcanzado para operación ${op.type} misión ${op.missionId}")
                            }
                        } else {
                            println("Falló operación ${op.type} para misión ${op.missionId}: ${result.message}")
                        }
                    }
                    is Resource.Loading -> {}
                }
            }

            if (failedOperations.isNotEmpty()) {
                println("Esperando $retryDelay ms antes de reintentar operaciones pendientes")
                delay(retryDelay)
                pendingOperations = failedOperations.toList()
            } else {
                pendingOperations = emptyList()
            }
        }

        return syncedOperations
    }

    override suspend fun getPendingOperations(): List<OfflineOperation> =
        offlineDao.getAllOperations()

    private suspend fun cleanAppliedOperations() {
        val pendingOps = offlineDao.getAllOperations()

        for (op in pendingOps) {
            if (op.type !in listOf("create", "delete")) continue

            val apiResult: Resource<Unit> = safeApiResponse {
                when (op.type) {
                    "create" -> {
                        val missionEntity = missionDao.getMissionById(op.missionId)
                        if (missionEntity != null) {
                            api.createMission(MissionMapper.fromEntityToDto(missionEntity))
                        } else {
                            throw Exception("Misión no encontrada en DB local")
                        }
                    }
                    "delete" -> api.deleteMission(op.missionId)
                    else -> throw Exception("Operación desconocida")
                }
            }.mapToUnit()

            when {
                op.type == "create" && apiResult is Resource.Success -> {
                    offlineDao.deleteOperationById(op.id)
                    println("Eliminada operación CREATE pendiente para misión ${op.missionId}")
                }
                op.type == "delete" && apiResult is Resource.Error &&
                        apiResult.errorType == NetworkError.NOT_FOUND -> {
                    offlineDao.deleteOperationById(op.id)
                    println("Eliminada operación DELETE pendiente para misión ${op.missionId}")
                }
                else -> {
                    println("Operación pendiente ${op.type} para misión ${op.missionId} permanece")
                }
            }
        }
    }
}
