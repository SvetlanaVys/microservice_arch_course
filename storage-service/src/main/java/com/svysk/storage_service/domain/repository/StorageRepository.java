package com.svysk.storage_service.domain.repository;

import com.svysk.storage_service.domain.Storage;

import java.util.List;

public interface StorageRepository {
    Long createStorage(Storage storage);

    List<Storage> getStorages();

    List<Storage> findByIds(List<Long> storageIds);

    void deleteByIds(List<Long> existingIds);

    boolean existsByBucketAndPath(String bucket, String path);
}
