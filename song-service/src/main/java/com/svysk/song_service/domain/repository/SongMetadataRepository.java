package com.svysk.song_service.domain.repository;

import com.svysk.song_service.domain.SongMetadataRecord;

import java.util.List;
import java.util.Optional;

public interface SongMetadataRepository {

    Optional<SongMetadataRecord> findById(Long id);

    List<Long> findByIds(List<Long> ids);

    Optional<SongMetadataRecord> save(SongMetadataRecord record);

    void deleteByIds(List<Long> resourceIds);
}
