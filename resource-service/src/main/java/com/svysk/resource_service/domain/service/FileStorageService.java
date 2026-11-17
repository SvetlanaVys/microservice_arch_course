package com.svysk.resource_service.domain.service;


public interface FileStorageService {

    void upload(
            String bucket,
            String key,
            byte[] content,
            String contentType);

    byte[] download(String bucket, String key);

    void delete(String bucket, String key);

    void move(String sourceBucket, String sourceKey,
              String targetBucket, String targetKey);
}
