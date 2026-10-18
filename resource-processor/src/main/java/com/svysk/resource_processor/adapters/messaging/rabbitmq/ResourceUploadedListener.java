package com.svysk.resource_processor.adapters.messaging.rabbitmq;

import com.svysk.openapi.client.ResourcesApi;
import com.svysk.openapi.client.SongsApi;
import com.svysk.openapi.dto.SongMetadataRecordDto;
import com.svysk.resource_processor.application.ExtractMP3FileServiceImpl;
import com.svysk.resource_processor.domain.SongDataRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResourceUploadedListener {

    private static final String RESOURCE_SERVICE = "resource-srv";

    private final ResourcesApi resourcesApi;
    private final SongsApi songsApi;
    private final ExtractMP3FileServiceImpl extractMP3Service;

    @RabbitListener(queues = "resource.uploaded.queue")
    public void handleResourceUploaded(ResourceUploadedEvent event) throws IOException {
        Long resourceId = event.resourceId();

        log.debug("Received resourceId: {}", resourceId);

        Resource audioResource = resourcesApi.getAudio(resourceId);
        byte[] audioBytes = audioResource.getContentAsByteArray();


        log.debug("Extracting file with resource id: {}...", resourceId);
        SongMetadataRecordDto metadataDto = extractMP3Service.extract(new SongDataRecord(resourceId, audioBytes));

        log.debug("Create song metadata wit resource id: {}...", resourceId);
        songsApi.createSong(metadataDto, RESOURCE_SERVICE);

    }

}
