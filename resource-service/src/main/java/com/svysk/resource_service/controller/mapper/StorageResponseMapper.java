package com.svysk.resource_service.controller.mapper;

import com.svysk.openapi.storage.dto.StorageResponseDto;
import com.svysk.resource_service.domain.StorageResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StorageResponseMapper {

    StorageResponse toStorageResponse(StorageResponseDto dto);

    StorageResponseDto toDto(StorageResponse storageResponse);
}
