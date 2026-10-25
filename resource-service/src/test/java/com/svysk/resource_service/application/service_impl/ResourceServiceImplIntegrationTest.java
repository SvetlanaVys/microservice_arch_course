package com.svysk.resource_service.application.service_impl;

import com.svysk.resource_service.application.port.out.ResourceEventPublisher;
import com.svysk.resource_service.domain.MP3Resource;
import com.svysk.resource_service.domain.repository.ResourceRepository;
import com.svysk.resource_service.domain.service.ResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
public class ResourceServiceImplIntegrationTest {

    @MockitoBean
    private S3Client s3Client;

    @MockitoBean
    private ResourceEventPublisher eventPublisher;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceRepository resourceRepository;

    @Test
    void upload_shouldSaveResourceAndReturnId() {
        // Arrange
        MP3Resource resource = new MP3Resource();
        resource.setData("face mp3 content".getBytes(StandardCharsets.UTF_8));
        resource.setContentType("audio/mpeg");
        resource.setSize(500L);
        resource.setCreatedAt(LocalDateTime.now());

        // Act
        Long resourceId = resourceService.upload(resource);
        MP3Resource savedResource = resourceRepository.findById(resourceId)
                .orElseThrow();

        // Assert
        assertEquals(resourceId, savedResource.getId());
        assertEquals("audio/mpeg", savedResource.getContentType());
        assertNotNull(resourceId);
        assertTrue(resourceId > 0);
        verify(s3Client).putObject(
                any(PutObjectRequest.class), any(RequestBody.class)
        );
        verify(eventPublisher)
                .publishResourceUploaded(resourceId);
    }
}