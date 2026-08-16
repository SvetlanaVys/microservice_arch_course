package com.svysk.song_service.domain.service;

import com.svysk.openapi.dto.SongMetadataRecordDto;
import com.svysk.song_service.domain.SongMetadataRecord;

import java.util.List;


public interface SongMetadataService {

    Long upload(SongMetadataRecord record, String xSourceService);

    SongMetadataRecord getById(Long id);

    List<Long> deleteByIds(String ids);
}
