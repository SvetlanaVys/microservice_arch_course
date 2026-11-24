package com.svysk.storage_service.controller;

import com.svysk.openapi.api.StoragesApi;
import com.svysk.openapi.dto.CreateStorage200ResponseDto;
import com.svysk.openapi.dto.CreateStorageRequestDto;
import com.svysk.openapi.dto.StorageResponseDto;
import com.svysk.storage_service.controller.mapper.StorageMapper;
import com.svysk.storage_service.domain.service.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/storages")
@RequiredArgsConstructor
public class StorageController implements StoragesApi {

    private final StorageService storageService;
    private final StorageMapper mapper;

    @PostMapping
    public ResponseEntity<CreateStorage200ResponseDto> createStorage(
            @Valid @RequestBody CreateStorageRequestDto request) {

        Long id = storageService.createStorage(mapper.toStorage(request));

        CreateStorage200ResponseDto response =
                new CreateStorage200ResponseDto();
        response.setId(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<StorageResponseDto>> getStorages() {
        return ResponseEntity.ok(mapper.toStorageResponseDtoList(storageService.getStorages()));
    }

    @DeleteMapping
    public ResponseEntity<List<Long>> deleteStorages(
            @RequestParam("id") String ids) {

        List<Long> deletedIds = storageService.deleteByIds(ids);

        return ResponseEntity.ok(deletedIds);
    }
}
