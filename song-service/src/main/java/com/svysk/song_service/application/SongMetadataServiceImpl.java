package com.svysk.song_service.application;

import com.svysk.openapi.client.ResourcesApi;
import com.svysk.openapi.dto.SongMetadataRecordDto;
import com.svysk.song_service.controller.error_handler.*;
import com.svysk.song_service.domain.SongMetadataRecord;
import com.svysk.song_service.domain.repository.SongMetadataRepository;
import com.svysk.song_service.domain.service.SongMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SongMetadataServiceImpl implements SongMetadataService {

    private static final String RESOURCE_SERVICE = "resource-srv";

    private final SongMetadataRepository repository;

    private final ResourcesApi resourcesApi;

    @Override
    public SongMetadataRecord getById(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidRecordIdValueException(id);
        }

        return repository.findById(id)
                .orElseThrow(() ->
                        new SongNotFoundException(id)
                );
    }

    @Override
    public Long upload(SongMetadataRecord record, String xSourceService) {

        Optional<SongMetadataRecord> existingRecord = repository.findById(record.getId());

        if(existingRecord.isPresent()) {
            throw new SongMetadataAlreadyExists(record.getId());
        }

        // TODO: First postman's test 'Create Test Song Metadata' doesn't work with this part because 'test_id' is generated randomly on fly
        //  and resource with this id does not exist
        /*if(!RESOURCE_SERVICE.equals(xSourceService) && !resourcesApi.resourceExists(record.getId())) {
                throw new AudioResourceNotFoundException(record.getId());
        }*/

        return repository.save(record)
                .map(SongMetadataRecord::getId)
                .orElseThrow(() -> new IllegalStateException("Failed to save song metadata record"));
    }

    @Override
    public List<Long> deleteByIds(String ids) {
        List<Long> resourceIds = parseIds(ids);

        List<Long> existingIds = repository.findByIds(resourceIds);

        repository.deleteByIds(existingIds);

        return existingIds;
    }

    private List<Long> parseIds(String ids) {
        if (ids == null || ids.isBlank()) {
            throw new InvalidRecordIdsFormatException(ids);
        }

        if(ids.length() > 200) {
            throw new RecordIdsTooLongException(ids.length());
        }

        List<Long> resourceIds = new ArrayList<>();
        for (String value : ids.split(",", -1)) {
            String trimmedValue = value.trim();
            if (trimmedValue.isBlank()) {
                throw new InvalidRecordIdsFormatException(trimmedValue);
            }
            try {
                long id = Long.parseLong(trimmedValue);
                if (id <= 0) {
                    throw new InvalidRecordIdsFormatException(trimmedValue);
                }
                resourceIds.add(id);
            } catch (NumberFormatException e) {
                throw new InvalidRecordIdsFormatException(trimmedValue);
            }
        }
        return resourceIds;
    }
}
