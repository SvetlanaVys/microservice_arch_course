package com.svysk.song_service.adapters.storage.impl;

import com.svysk.song_service.adapters.storage.SongMetadataRecordDao;
import com.svysk.song_service.adapters.storage.entity.SongMetadataRecordEntity;
import com.svysk.song_service.adapters.storage.mapper.SongRecordEntityMapper;
import com.svysk.song_service.domain.SongMetadataRecord;
import com.svysk.song_service.domain.repository.SongMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SongMetadataRepositoryImpl implements SongMetadataRepository {

    private final SongMetadataRecordDao recordDao;

    private final SongRecordEntityMapper mapper;

    @Override
    public Optional<SongMetadataRecord> findById(Long id) {
        Optional<SongMetadataRecordEntity> resourceEntity = recordDao.findById(id);

        return resourceEntity
                .map(mapper::toRecord);
    }

    @Override
    public List<Long> findByIds(List<Long> ids) {
        return recordDao.findAllById(ids)
                .stream()
                .map(SongMetadataRecordEntity::getId)
                .toList();
    }

    @Override
    public Optional<SongMetadataRecord> save(SongMetadataRecord record) {
        SongMetadataRecordEntity recordEntity = mapper.toEntity(record);

        return Optional.of(
                recordDao.save(recordEntity)
        ).map(mapper::toRecord);
    }

    @Override
    public void deleteByIds(List<Long> resourceIds) {
        recordDao.deleteAllById(resourceIds);
    }
}
