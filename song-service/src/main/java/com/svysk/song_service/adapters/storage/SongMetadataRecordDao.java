package com.svysk.song_service.adapters.storage;

import com.svysk.song_service.adapters.storage.entity.SongMetadataRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongMetadataRecordDao extends JpaRepository<SongMetadataRecordEntity, Long> {
}