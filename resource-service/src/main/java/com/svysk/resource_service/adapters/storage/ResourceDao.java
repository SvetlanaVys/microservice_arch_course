package com.svysk.resource_service.adapters.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import com.svysk.resource_service.adapters.storage.entity.ResourceEntity;

public interface ResourceDao extends JpaRepository<ResourceEntity, Long> {
}