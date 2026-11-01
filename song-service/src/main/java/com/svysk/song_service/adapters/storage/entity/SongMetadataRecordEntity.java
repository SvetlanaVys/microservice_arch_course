package com.svysk.song_service.adapters.storage.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "song_metadata_record")
@Data
@ToString
@RequiredArgsConstructor
public class SongMetadataRecordEntity {

    @Id
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String artist;

    @Column(nullable = false, length = 100)
    private String album;

    @Column(nullable = false, length = 5)
    private String duration;

    @Column(name = "release_year", nullable = false, length = 4)
    private String year;
}
