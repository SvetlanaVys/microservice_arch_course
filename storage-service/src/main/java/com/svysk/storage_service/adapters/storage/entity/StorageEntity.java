package com.svysk.storage_service.adapters.storage.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table(
    name = "storages",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_storages_bucket_key_prefix",
            columnNames = {"bucket_name", "key_prefix"}
        )
    }
)
@Data
@ToString
@RequiredArgsConstructor
public class StorageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type", nullable = false, length = 50)
    private StorageType storageType;

    @Column(name = "bucket_name", nullable = false)
    private String bucket;

    @Column(name = "key_prefix", nullable = false)
    private String path;
}

