package com.svysk.resource_service.adapters.s3.impl;

import com.svysk.resource_service.domain.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;


@Component
public class S3FIleStorageAdapter implements FileStorageService {

    private final S3Client s3Client;

    public S3FIleStorageAdapter(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void upload(String bucket, String key, byte[] content, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(content));
    }

    @Override
    public byte[] download(String bucket, String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(request);

        return response.asByteArray();
    }

    @Override
    public void delete(String bucket, String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    @Override
    public void move(String sourceBucket, String sourceKey, String targetBucket, String targetKey) {

        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(sourceBucket)
                .sourceKey(sourceKey)
                .destinationBucket(targetBucket)
                .destinationKey(targetKey)
                .build();

        s3Client.copyObject(copyRequest);
        delete(sourceBucket, sourceKey);
    }
}
