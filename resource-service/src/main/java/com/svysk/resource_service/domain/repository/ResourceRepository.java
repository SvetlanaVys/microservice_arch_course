package com.svysk.resource_service.domain.repository;

import java.util.List;
import java.util.Optional;

import com.svysk.resource_service.domain.MP3Resource;

public interface ResourceRepository {

    Optional<MP3Resource> findById(Long id);

    List<MP3Resource> findByIds(List<Long> ids);

    Optional<MP3Resource> save(MP3Resource audioFile);

    void deleteByIds(List<Long> resourceIds);

    Boolean existsById(Long id);
}