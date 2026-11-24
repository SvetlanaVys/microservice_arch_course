package com.svysk.storage_service.controller.mapper;

import com.svysk.openapi.dto.CreateStorageRequestDto;
import com.svysk.openapi.dto.StorageResponseDto;
import com.svysk.storage_service.domain.Storage;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface StorageMapper {

    Storage toStorage(CreateStorageRequestDto requestDto);

    Storage toStorage(StorageResponseDto responseDto);

    CreateStorageRequestDto toCreateStorageRequestDto(Storage storage);

    StorageResponseDto toStorageResponseDto(Storage storage);


    List<StorageResponseDto> toStorageResponseDtoList(List<Storage> storageResponses);

    List<Storage> toStorageList(List<StorageResponseDto> storageResponseDtos);
}

