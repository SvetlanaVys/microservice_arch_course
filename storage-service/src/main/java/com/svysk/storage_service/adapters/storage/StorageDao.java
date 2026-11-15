package com.svysk.storage_service.adapters.storage;

import com.svysk.storage_service.adapters.storage.entity.StorageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageDao extends JpaRepository<StorageEntity, Long> {
}