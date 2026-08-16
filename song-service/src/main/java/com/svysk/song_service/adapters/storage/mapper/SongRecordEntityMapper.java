package com.svysk.song_service.adapters.storage.mapper;

import com.svysk.song_service.adapters.storage.entity.SongMetadataRecordEntity;
import com.svysk.song_service.domain.SongMetadataRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SongRecordEntityMapper {

    SongMetadataRecord toRecord(SongMetadataRecordEntity recordEntity) ;

    SongMetadataRecordEntity toEntity(SongMetadataRecord record);
}
