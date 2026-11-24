package com.svysk.storage_service.application;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.svysk.storage_service.controller.error_handler.InvalidResourceIdsFormatException;
import com.svysk.storage_service.controller.error_handler.ResourceIdsTooLongExcepion;
import com.svysk.storage_service.domain.Storage;
import com.svysk.storage_service.domain.repository.StorageRepository;
import com.svysk.storage_service.domain.service.StorageService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final StorageRepository repository;

    @Override
    @Transactional
    public Long createStorage(Storage storage) {

        if (repository.existsByBucketAndPath(
            storage.getBucket(),
            storage.getPath())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Storage with this bucket and path already exists"
            );
        }

        try {
            return repository.createStorage(storage);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Storage with this bucket and path already exists",
                    ex
            );
        }
    }

    @Override
    public List<Storage> getStorages() {
        return repository.getStorages();
    }

    @Override
    public List<Long> deleteByIds(String ids) {

        List<Long> storageIds = parseIds(ids);

        List<Storage> existingStorages = repository.findByIds(storageIds);
        List<Long> existingIds = new ArrayList<>();

        for (Storage storage: existingStorages) {
            existingIds.add(storage.getId());
        }

        if(!existingIds.isEmpty()) {
            repository.deleteByIds(existingIds);
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
