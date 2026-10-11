package com.svysk.resource_processor.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SongDataRecord {

    private Long id;
    private byte[] data;
}
