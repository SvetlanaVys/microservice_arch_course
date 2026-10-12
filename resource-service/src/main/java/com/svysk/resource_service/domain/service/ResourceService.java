package com.svysk.resource_service.domain.service;

import com.svysk.resource_service.domain.MP3Resource;

import java.util.List;

public interface ResourceService {
    
    MP3Resource getById(Long id);

    Boolean existsById(Long id);

    Long upload(MP3Resource audioFile);

    List<Long> deleteByIds(String id);
}
