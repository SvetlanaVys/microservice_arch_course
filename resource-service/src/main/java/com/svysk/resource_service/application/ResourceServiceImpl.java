package com.svysk.resource_service.application;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.svysk.openapi.client.SongsApi;
import com.svysk.openapi.dto.SongMetadataRecordDto;
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
    private final AudioMetadataService audioMetadataService;
    private final SongsApi songsApi;

    @Override
    public MP3Resource getById(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidResourceIdValueException(id);
        }

        return repository.findById(id)
            .orElseThrow(() ->
                    new AudioResourceNotFoundException(id)
            );
    }

    @Override
    public Boolean isExists(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Long upload(MP3Resource audioFile) {
        SongMetadataRecordDto recordDto = audioMetadataService.extract(audioFile.getData());
        Long createdMP3ResourceId = repository.save(audioFile)
                .map(MP3Resource::getId)
                .orElseThrow(() -> new IllegalStateException("Failed to save audio file"));

        recordDto.setId(createdMP3ResourceId);

        songsApi.createSong(recordDto, RESOURCE_SERVICE);

        return createdMP3ResourceId;
    }

    @Override
    public List<Long> deleteByIds(String ids) {

        List<Long> resourceIds = parseIds(ids);

        List<Long> existingIds = repository.findByIds(resourceIds);

        repository.deleteByIds(existingIds);

        songsApi.deleteSongs(ids);

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
