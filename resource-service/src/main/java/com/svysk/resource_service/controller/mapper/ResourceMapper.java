package com.svysk.resource_service.controller.mapper;


import org.springframework.core.io.Resource;
import org.mapstruct.Mapper;

import com.svysk.resource_service.domain.MP3Resource;

import java.io.IOException;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring") 
public interface ResourceMapper {

    default MP3Resource toMP3Resource(Resource resource) {
        if(resource == null) {
            return null;
        }

        try {
            MP3Resource mp3Resource = new MP3Resource();

            mp3Resource.setSize(resource.contentLength());
            mp3Resource.setContentType("audio/mpeg"); // TODO
            mp3Resource.setData(resource.getContentAsByteArray());
            mp3Resource.setCreatedAt(LocalDateTime.now());

            return mp3Resource;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to read audio resource",
                    e
            );
        }
    }

}

