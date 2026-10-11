package com.svysk.resource_processor.controller.mapper;

import com.svysk.openapi.dto.SongDataRecordDto;
import com.svysk.resource_processor.domain.SongDataRecord;
import org.mapstruct.Mapper;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;

@Mapper(componentModel = "spring") 
public interface ResourceMapper {

    default SongDataRecord toSongDataRecord(SongDataRecordDto dataDto) {
        if(dataDto == null) {
            return null;
        }

        try {
            SongDataRecord songDataRecord = new SongDataRecord();

            songDataRecord.setId(dataDto.getId());
            songDataRecord.setData(dataDto.getData().getContentAsByteArray());

            return songDataRecord;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to read audio resource",
                    e
            );
        }
    }

    default SongDataRecordDto toSongDataRecordDto(SongDataRecord data) {
        if(data == null) {
            return null;
        }

        SongDataRecordDto songDataRecordDto = new SongDataRecordDto();

        songDataRecordDto.setId(data.getId());
        songDataRecordDto.setData(new ByteArrayResource(data.getData()));

        return songDataRecordDto;

    }
}

