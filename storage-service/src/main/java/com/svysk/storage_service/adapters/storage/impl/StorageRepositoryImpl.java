package com.svysk.storage_service.adapters.storage.impl;

import com.svysk.storage_service.adapters.storage.StorageDao;
import com.svysk.storage_service.adapters.storage.entity.StorageEntity;
import com.svysk.storage_service.adapters.storage.mapper.StorageEntityMapper;
import com.svysk.storage_service.domain.Storage;
import com.svysk.storage_service.domain.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class StorageRepositoryImpl implements StorageRepository {
    
    private final StorageDao dStorageDao;
     
    private final StorageEntityMapper storageMapper;

    @Override
    public Long createStorage(Storage storage) {

        StorageEntity storageEntity = storageMapper.toEntity(storage);
        StorageEntity createdStorage = dStorageDao.save(storageEntity);

        return createdStorage.getId();
    }

    @Override
    public List<Storage> getStorages() {
        return dStorageDao.findAll()
                .stream()
                .map(storageMapper::toStorage)
                .toList();
    }

    @Override
    public List<Storage> findByIds(List<Long> ids) {
        return dStorageDao.findAllById(ids)
                .stream()
                .map(storageMapper::toStorage)
                .toList();
    }

    @Override
    public void deleteByIds(List<Long> existingIds) {
        dStorageDao.deleteAllById(existingIds);
    }

    @Override
    public boolean existsByBucketAndPath(String bucket, String path) {
        return dStorageDao.existsByBucketAndPath(bucket, path);
    }
}
