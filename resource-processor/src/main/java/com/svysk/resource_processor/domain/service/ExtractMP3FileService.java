package com.svysk.resource_processor.domain.service;

import com.svysk.openapi.dto.SongMetadataRecordDto;
import com.svysk.resource_processor.domain.SongDataRecord;

public interface ExtractMP3FileService {


    SongMetadataRecordDto extract(SongDataRecord resource);
}
