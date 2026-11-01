package com.svysk.resource_service.application.service_impl;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.svysk.openapi.client.SongsApi;
import com.svysk.resource_service.adapters.s3.impl.S3FIleStorageAdapter;
import com.svysk.resource_service.application.port.out.ResourceEventPublisher;
import com.svysk.resource_service.controller.error_handler.ResourceIdsTooLongExcepion;
import org.springframework.stereotype.Service;

import com.svysk.resource_service.controller.error_handler.AudioResourceNotFoundException;
import com.svysk.resource_service.controller.error_handler.InvalidResourceIdValueException;
import com.svysk.resource_service.controller.error_handler.InvalidResourceIdsFormatException;
import com.svysk.resource_service.domain.MP3Resource;
import com.svysk.resource_service.domain.repository.ResourceRepository;
import com.svysk.resource_service.domain.service.ResourceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private static final String RESOURCE_SERVICE = "resource-srv";

    private final ResourceRepository repository;
    private final S3FIleStorageAdapter s3FIleStorage;
    private final ResourceEventPublisher eventPublisher;
    private final SongsApi songsApi;

    @Override
    public MP3Resource getById(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidResourceIdValueException(id);
        }

        MP3Resource resource = repository.findById(id)
            .orElseThrow(() ->
                    new AudioResourceNotFoundException(id)
            );

        if(!resource.getFilePath().isBlank()) {
            byte[] fileData = s3FIleStorage.download(resource.getFilePath());
            resource.setData(fileData);
        }

        return resource;
    }

    @Override
    public Boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Long upload(MP3Resource audioFile) {

        String filePath = "mp3/" + UUID.randomUUID() + ".mp3";
        s3FIleStorage.upload(
                filePath,
                audioFile.getData(),
                audioFile.getContentType()
        );

        audioFile.setFilePath(filePath);


        /*
        TODO: The service call has been commented out to comply with the task requirements
        SongMetadataRecordDto recordDto = audioMetadataService.extract(audioFile.getData());
        recordDto.setId(createdMP3ResourceId)
        songsApi.createSong(recordDto, RESOURCE_SERVICE);
        */

        Long resourceId = repository.save(audioFile)
                .map(MP3Resource::getId)
                .orElseThrow(() -> new IllegalStateException("Failed to save audio file"));

        eventPublisher.publishResourceUploaded(resourceId);

        return resourceId;
    }

    @Override
    public List<Long> deleteByIds(String ids) {

        List<Long> resourceIds = parseIds(ids);

        List<MP3Resource> existingResources = repository.findByIds(resourceIds);
        List<Long> existingIds = new ArrayList<>();

        for (MP3Resource resource: existingResources) {
            String filePath = resource.getFilePath();

            if(filePath != null && !filePath.isBlank()) {
                s3FIleStorage.delete(resource.getFilePath());
            }

            existingIds.add(resource.getId());
        }

        if(!existingIds.isEmpty()) {
            repository.deleteByIds(existingIds);
            songsApi.deleteSongs(ids);
        }

        return existingIds;
    }

    private List<Long> parseIds(String ids) {
        if (ids == null || ids.isBlank()) {
            throw new InvalidResourceIdsFormatException(ids);
        }

        if(ids.length() > 200) {
            throw new ResourceIdsTooLongExcepion(ids.length());
        }

        List<Long> resourceIds = new ArrayList<>();
        for (String value : ids.split(",", -1)) {
            String trimmedValue = value.trim();
            if (trimmedValue.isBlank()) {
                throw new InvalidResourceIdsFormatException(trimmedValue);
            }
            try {
                Long id = Long.parseLong(trimmedValue);
                if (id <= 0) {
                    throw new InvalidResourceIdsFormatException(trimmedValue);
                }
                resourceIds.add(id);
            } catch (NumberFormatException e) {
                throw new InvalidResourceIdsFormatException(trimmedValue);
            }
        }
        return resourceIds;
    }
}
