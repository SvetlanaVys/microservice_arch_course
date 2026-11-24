package com.svysk.storage_service.adapters.storage.mapper;

import com.svysk.storage_service.adapters.storage.entity.StorageEntity;
import com.svysk.storage_service.domain.Storage;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface StorageEntityMapper {

    StorageEntity toEntity(Storage storage);

    Storage toStorage(StorageEntity entity);
}
