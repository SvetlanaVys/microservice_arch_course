package com.svysk.storage_service.domain.service;

import com.svysk.storage_service.domain.Storage;
import jakarta.validation.Valid;

import java.util.List;

public interface StorageService {
    Long createStorage(@Valid Storage request);

    List<Storage> getStorages();

    List<Long> deleteByIds(String ids);
}
