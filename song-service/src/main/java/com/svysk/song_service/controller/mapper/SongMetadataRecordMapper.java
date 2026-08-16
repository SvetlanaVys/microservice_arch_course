package com.svysk.song_service.controller.mapper;

import com.svysk.openapi.dto.SongMetadataRecordDto;
import com.svysk.song_service.domain.SongMetadataRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SongMetadataRecordMapper {

    SongMetadataRecord toSongMetadataRecord(SongMetadataRecordDto recordDto) ;

    SongMetadataRecordDto toSongMetadataRecordDto(SongMetadataRecord recordDto);
}
