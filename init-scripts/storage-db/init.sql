CREATE TABLE IF NOT EXISTS storages
(
    id               BIGINT PRIMARY KEY,
    storage_type     VARCHAR(50) NOT NULL,
    bucket_name      VARCHAR(255) NOT NULL,
    key_prefix       VARCHAR(255) NOT NULL,
    CHECK (storage_type IN ('STAGING', 'PERMANENT'))
);

INSERT INTO storages (id, storage_type, bucket_name, key_prefix)
VALUES
    (1, 'STAGING', 'staging-bucket', 'staging/'),
    (2, 'PERMANENT', 'permanent-bucket', 'permanent/');