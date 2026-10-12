package com.svysk.resource_service.domain.service;


public interface FileStorageService {

    void upload(
            String key,
            byte[] content,
            String contentType);

    byte[] download(String key);

    void delete(String key);
}
