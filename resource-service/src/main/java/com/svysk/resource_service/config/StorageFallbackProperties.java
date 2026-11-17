package com.svysk.resource_service.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.svysk.resource_service.domain.StorageResponse;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "storage.fallback")
public class StorageFallbackProperties {

    private List<StorageResponse> storages = new ArrayList<>();
}
