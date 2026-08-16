package com.svysk.song_service.domain;

import lombok.Data;

@Data
public class SongMetadataRecord {
    private Long id;

    private String name;

    private String artist;

    private String album;

    private String duration;

    private String year;
}
