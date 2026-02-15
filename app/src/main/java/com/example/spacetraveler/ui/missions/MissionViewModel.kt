package com.example.spacetraveler.ui.missions

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacetraveler.core.NetworkError
import com.example.spacetraveler.core.Resource
import com.example.spacetraveler.core.ui.UiEvent
import com.example.spacetraveler.domain.model.Mission
import com.example.spacetraveler.domain.repository.MissionRepository
import com.example.spacetraveler.domain.repository.OfflineOperationsRepository
import com.example.spacetraveler.domain.usecase.CreateMissionUseCase
import com.example.spacetraveler.domain.usecase.DeleteMissionUseCase
import com.example.spacetraveler.domain.usecase.GetAllMissionsUseCase
import com.example.spacetraveler.domain.usecase.safeInvoke
import com.example.spacetraveler.utils.NetworkStatus
import com.example.spacetraveler.utils.isValidDate
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionViewModel @Inject constructor(
    missionRepository: MissionRepository,
    private val offlineOperationsRepository: OfflineOperationsRepository,
    @ApplicationContext context: Context
) : ViewModel() {

    private val _missions = MutableStateFlow<List<Mission>>(emptyList())
    val missions: StateFlow<List<Mission>> = _missions.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)

    private val _isLoading = MutableStateFlow(false)

    private val getAllMissionsUseCase = GetAllMissionsUseCase(missionRepository)
    private val createMissionUseCase = CreateMissionUseCase(missionRepository)
    private val deleteMissionUseCase = DeleteMissionUseCase(missionRepository)

    private val networkStatus = NetworkStatus(context)

    private val _uiEvent = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        observeNetwork()
        loadMissions()
        logPendingOperations()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkStatus.isOnline.collect { isOnline ->
                Log.d("MissionViewModel", "Estado de la red: isOnline=$isOnline")
                if (isOnline) {
                    _isLoading.value = true
                    val syncedOps = safeInvoke { offlineOperationsRepository.syncOfflineOperations(5000) }
                    if (syncedOps is Resource.Success) {
                        Log.d("MissionViewModel", "Operaciones sincronizadas: ${syncedOps.data.size}")
                    }
                    logPendingOperations()
                    _isLoading.value = false
                }
            }
        }
    }

    fun loadMissions() {
        viewModelScope.launch {
            _isLoading.value = true

            when (val resource: Resource<List<Mission>> = getAllMissionsUseCase()) {
                is Resource.Success -> {
                    _missions.value = resource.data
                    _errorMessage.value = null
                }
                is Resource.Error -> {
                    _errorMessage.value = mapError(
                        resource.errorType ?: NetworkError.UNKNOWN,
                        resource.message
                    )
                }
                is Resource.Loading -> {
                }
            }

            _isLoading.value = false
        }
    }

    fun createMission(
        nombre: String,
        planetaDestino: String,
        fechaLanzamiento: String,
        descripcion: String,
        onResult: (success: Boolean) -> Unit
    ) {
        if (nombre.isBlank() || planetaDestino.isBlank() || fechaLanzamiento.isBlank() || descripcion.isBlank()) {
            _errorMessage.value = "Todos los campos son obligatorios"
            onResult(false)
            return
        }

        if (!isValidDate(fechaLanzamiento)) {
            _errorMessage.value = "Formato de fecha inválido (use dd/MM/yyyy)"
            onResult(false)
            return
        }

        viewModelScope.launch {

            _isLoading.value = true
            when (val resource = safeInvoke {
                createMissionUseCase(
                    Mission(0, nombre, planetaDestino, fechaLanzamiento, descripcion)
                )
            }) {
                is Resource.Success -> {
                    loadMissions()
                    _uiEvent.emit(UiEvent.ShowSnackbar("Misión creada correctamente"))
                    onResult(true)
                }
                is Resource.Error -> {
                    val message = mapError(
                        resource.errorType ?: NetworkError.UNKNOWN,
                        resource.message
                    )
                    _errorMessage.value = message
                    _uiEvent.emit(UiEvent.ShowSnackbar(message))
                    onResult(false)
                }
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    fun deleteMission(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val resource = safeInvoke { deleteMissionUseCase(id) }) {
                is Resource.Success -> {
                    loadMissions()
                    _uiEvent.emit(UiEvent.ShowSnackbar("Misión eliminada correctamente"))
                }
                is Resource.Error -> {
                    val message = mapError(
                        resource.errorType ?: NetworkError.UNKNOWN,
                        resource.message
                    )
                    _errorMessage.value = message
                    _uiEvent.emit(UiEvent.ShowSnackbar(message))
                }
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    private fun logPendingOperations() {
        viewModelScope.launch {
            try {
                val pendingOps = offlineOperationsRepository.getPendingOperations()
                if (pendingOps.isEmpty()) {
                    Log.d("MissionViewModel", "No hay operaciones pendientes")
                } else {
                    Log.d("MissionViewModel", "Operaciones pendientes:")
                    pendingOps.forEach { op ->
                        Log.d(
                            "MissionViewModel",
                            "id=${op.id}, type=${op.type}, missionId=${op.missionId}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("MissionViewModel", "Error al obtener operaciones pendientes", e)
            }
        }
    }

    private fun mapError(errorType: NetworkError, fallback: String?): String = when (errorType) {
        NetworkError.NO_INTERNET -> "Sin conexión a internet"
        NetworkError.NOT_FOUND -> "No se encontró el recurso"
        NetworkError.UNAUTHORIZED -> "No autorizado"
        NetworkError.CONFLICT -> "Conflicto en los datos"
        NetworkError.REQUEST_TIMEOUT -> "Tiempo de espera agotado"
        NetworkError.TOO_MANY_REQUESTS -> "Demasiadas solicitudes"
        NetworkError.SERVER_ERROR -> "Error interno del servidor"
        NetworkError.UNKNOWN -> fallback ?: "Error desconocido"
    }
}
