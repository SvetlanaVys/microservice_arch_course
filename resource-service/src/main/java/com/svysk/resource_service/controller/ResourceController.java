package com.svysk.resource_service.controller;

import com.svysk.openapi.dto.DeleteResourceResponseDto;
import com.svysk.openapi.dto.UploadResourceResponseDto;
import com.svysk.resource_service.domain.MP3Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.svysk.openapi.api.ResourcesApi;
import com.svysk.resource_service.controller.mapper.ResourceMapper;
import com.svysk.resource_service.domain.service.ResourceService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResourceController implements ResourcesApi {

    private final ResourceService service;

    private final ResourceMapper mapper;

    @Override
    public ResponseEntity<UploadResourceResponseDto> uploadAudio(Resource resource) {

        Long resourceId = service.upload(mapper.toMP3Resource(resource));

        return ResponseEntity.ok(new UploadResourceResponseDto(resourceId));
    }

    @Override
    public ResponseEntity<Resource> getAudio(Long id) {
        MP3Resource audio = service.getById(id);

        Resource resource = new ByteArrayResource(audio.getData());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(audio.getContentType()))
                .contentLength(audio.getSize())
                .body(resource);
    }

    @Override
    public ResponseEntity<Boolean> resourceExists(Long id) {

        return ResponseEntity.ok(service.isExists(id));
    }

    @Override
    public ResponseEntity<DeleteResourceResponseDto> deleteAudioResources(String ids) {
        List<Long> deletedIds = service.deleteByIds(ids);

        DeleteResourceResponseDto response = new DeleteResourceResponseDto();
        response.setIds(deletedIds);

        return ResponseEntity.ok(response);
    }
}
