package com.svysk.resource_processor.adapters.messaging.rabbitmq;

import com.github.benmanes.caffeine.cache.Cache;
import com.svysk.openapi.client.ResourcesApi;
import com.svysk.openapi.client.SongsApi;
import com.svysk.openapi.dto.SongMetadataRecordDto;
import com.svysk.resource_processor.application.ExtractMP3FileServiceImpl;
import com.svysk.resource_processor.domain.SongDataRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.retry.support.RetryTemplate;

import java.io.IOException;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ResourceUploadedConsumer {

    private static final String RESOURCE_SERVICE = "resource-srv";

    private final ResourcesApi resourcesApi;
    private final SongsApi songsApi;
    private final ExtractMP3FileServiceImpl extractMP3Service;
    private final Cache<Long, Boolean> processedResourcesCache;

    @Bean
    public Consumer<ResourceUploadedEvent> resourceUploaded(RetryTemplate retryTemplate) {
        return event -> {
            Long resourceId = event.resourceId();

            Boolean existing = processedResourcesCache.asMap().putIfAbsent(resourceId, true);

            if(existing != null) {
                return;
            }

            try {
                retryTemplate.execute(context -> {
                    processEvent(resourceId);
                    return null;
                });
                processedResourcesCache.put(resourceId, true);
            } catch (RuntimeException e) {
                processedResourcesCache.invalidate(resourceId);
                throw e;
            }

        };
    }

    private void processEvent(Long  resourceId) {

        log.debug("Received resourceId: {}", resourceId);

        Resource audioResource = resourcesApi.getAudio(resourceId);
        byte[] audioBytes = null;

        try {
            audioBytes = audioResource.getContentAsByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        log.debug("Extracting file with resource id: {}...", resourceId);
        SongMetadataRecordDto metadataDto = extractMP3Service.extract(new SongDataRecord(resourceId, audioBytes));

        log.debug("Create song metadata wit resource id: {}...", resourceId);
        songsApi.createSong(metadataDto, RESOURCE_SERVICE);
    }
}
