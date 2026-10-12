package com.svysk.resource_service.adapters.storage.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.svysk.resource_service.adapters.storage.ResourceDao;
import com.svysk.resource_service.adapters.storage.entity.ResourceEntity;
import com.svysk.resource_service.adapters.storage.mapper.ResourceEntityMapper;
import com.svysk.resource_service.domain.MP3Resource;
import com.svysk.resource_service.domain.repository.ResourceRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ResourceRepositoryImpl implements ResourceRepository {
    
    private final ResourceDao dResourceDao;
     
    private final ResourceEntityMapper resourceMapper;

    @Override
    public Optional<MP3Resource> findById(Long id) {
        
        Optional<ResourceEntity> resourceEntity = dResourceDao.findById(id);

        return resourceEntity
                .map(resourceMapper::toResource);
    }

    @Override
    public List<MP3Resource> findByIds(List<Long> ids) {

        return dResourceDao.findAllById(ids)
                .stream()
                .map(resourceMapper::toResource)
                .toList();
    }

    @Override
    public Optional<MP3Resource> save(MP3Resource audioFile) {

        ResourceEntity resourceEntity = resourceMapper.toEntity(audioFile);
        
        return Optional.of(
            dResourceDao.save(resourceEntity)
        ).map(resourceMapper::toResource);
    }

    @Override
    public void deleteByIds(List<Long> resourceIds) {

        dResourceDao.deleteAllById(resourceIds);
    }

    @Override
    public Boolean existsById(Long id) {
        return dResourceDao.existsById(id);
    }

}
