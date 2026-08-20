package com.svysk.song_service.controller;

import com.svysk.openapi.api.SongsApi;
import com.svysk.openapi.dto.DeleteRecordResponseDto;
import com.svysk.openapi.dto.SongMetadataRecordDto;
import com.svysk.openapi.dto.UploadSongResponseDto;
import com.svysk.song_service.controller.mapper.SongMetadataRecordMapper;
import com.svysk.song_service.domain.service.SongMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class SongMetadataController implements SongsApi {

    private final SongMetadataService service;

    private final SongMetadataRecordMapper mapper;

    @Override
    public ResponseEntity<UploadSongResponseDto> createSong(
            SongMetadataRecordDto recordDto,
            String xSourceService
    ) {

        Long recordId = service.upload(mapper.toSongMetadataRecord(recordDto), xSourceService);

        return ResponseEntity.ok(new UploadSongResponseDto(recordId));
    }

    @Override
    public ResponseEntity<SongMetadataRecordDto> getSong(Long id) {

        return ResponseEntity.ok(
                mapper.toSongMetadataRecordDto(service.getById(id))
        );
    }

    @Override
    public ResponseEntity<DeleteRecordResponseDto> deleteSongs(String ids) {
        List<Long> deletedIds = service.deleteByIds(ids);

        DeleteRecordResponseDto response = new DeleteRecordResponseDto();
        response.setIds(deletedIds);

        return ResponseEntity.ok(response);
    }
}
