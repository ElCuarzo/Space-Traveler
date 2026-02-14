package com.example.spacetraveler.data.mapper

import com.example.spacetraveler.data.local.MissionEntity
import com.example.spacetraveler.data.remote.MissionDto
import com.example.spacetraveler.domain.model.Mission

object MissionMapper {

    fun fromEntityToDto(entity: MissionEntity): MissionDto {
        return MissionDto(
            id = entity.id,
            name = entity.name,
            planetaDestino = entity.planetaDestino,
            fechaLanzamiento = entity.fechaLanzamiento,
            descripcion = entity.descripcion
        )
    }

    fun fromEntityToDomain(entity: MissionEntity): Mission {
        return Mission(
            id = entity.id,
            nombre = entity.name,
            planetaDestino = entity.planetaDestino,
            fechaLanzamiento = entity.fechaLanzamiento,
            descripcion = entity.descripcion,
        )
    }

    fun fromDomainToEntity(mission: Mission, isSynced: Boolean = true): MissionEntity {
        return MissionEntity(
            id = mission.id,
            name = mission.nombre,
            planetaDestino = mission.planetaDestino,
            fechaLanzamiento = mission.fechaLanzamiento,
            descripcion = mission.descripcion,
            isSynced = isSynced
        )
    }

    fun fromDtoToEntity(dto: MissionDto, isSynced: Boolean = true): MissionEntity {
        return MissionEntity(
            id = dto.id,
            name = dto.name,
            planetaDestino = dto.planetaDestino,
            fechaLanzamiento = dto.fechaLanzamiento,
            descripcion = dto.descripcion,
            isSynced = isSynced
        )
    }

}
