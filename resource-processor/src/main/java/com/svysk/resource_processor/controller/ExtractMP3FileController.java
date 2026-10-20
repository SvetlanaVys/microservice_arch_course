package com.svysk.resource_processor.controller;

import com.svysk.openapi.api.ExtractApi;
import com.svysk.openapi.dto.SongDataRecordDto;
import com.svysk.openapi.dto.SongMetadataRecordDto;
import com.svysk.resource_processor.application.ExtractMP3FileServiceImpl;
import com.svysk.resource_processor.controller.mapper.ResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class ExtractMP3FileController implements ExtractApi {

    private final ExtractMP3FileServiceImpl extractService;

    private final ResourceMapper mapper;


    @Override
    public ResponseEntity<SongMetadataRecordDto> extractAudio(SongDataRecordDto dataRecord) {

        return ResponseEntity.ok(extractService.extract(mapper.toSongDataRecord(dataRecord)));
    }
}
