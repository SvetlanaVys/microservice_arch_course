package com.svysk.resource_service.application.service_impl;

import com.svysk.openapi.storage.client.StoragesApi;
import com.svysk.resource_service.config.StorageFallbackProperties;
import com.svysk.resource_service.controller.mapper.StorageResponseMapper;
import com.svysk.resource_service.domain.StorageResponse;
import com.svysk.resource_service.domain.StorageType;
import com.svysk.resource_service.domain.service.StorageClientService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StorageClientServiceImpl implements StorageClientService {


    private final StoragesApi storagesApi;
    private final StorageResponseMapper storageMapper;
    private final StorageFallbackProperties fallbackProperties;

    @Override
    @CircuitBreaker(
            name = "storageService",
            fallbackMethod = "getStorageByTypesFallback"
    )
    public StorageResponse getStorageByTypes(StorageType storageType) {
        return storagesApi.getStorages()
                .stream()
                .filter(storage -> storage.getStorageType().toString().equals(storageType.toString()))
                .findFirst()
                .map(storageMapper::toStorageResponse)
                .orElseThrow(() -> new RuntimeException(
                        "Storage not found for type: " + storageType
                ));
    }

    private StorageResponse getStorageByTypesFallback(
            StorageType storageType,
            Throwable throwable
    ) {
        return fallbackProperties.getStorages()
                .stream()
                .filter(storage -> storage.getStorageType() == storageType)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No fallback storage configured for type: " + storageType,
                        throwable
                ));
    }
}
