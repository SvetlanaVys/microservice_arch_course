package com.svysk.resource_service.domain.service;

import com.svysk.resource_service.domain.StorageResponse;
import com.svysk.resource_service.domain.StorageType;

public interface StorageClientService {

    StorageResponse getStorageByTypes(StorageType storageType);
}
