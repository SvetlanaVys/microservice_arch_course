package com.svysk.resource_service.adapters.storage.mapper;

import org.mapstruct.Mapper;

import com.svysk.resource_service.adapters.storage.entity.ResourceEntity;
import com.svysk.resource_service.domain.MP3Resource;


@Mapper(componentModel = "spring") 
public interface ResourceEntityMapper {

    ResourceEntity toEntity(MP3Resource resource); 

    MP3Resource toResource(ResourceEntity entity); 
}
