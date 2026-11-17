package com.svysk.storage_service.domain;

import com.svysk.storage_service.adapters.storage.entity.StorageType;
import lombok.Data;

@Data
public class Storage {

    private Long id;
    private StorageType storageType;
    private String bucket;
    private String path;
}
