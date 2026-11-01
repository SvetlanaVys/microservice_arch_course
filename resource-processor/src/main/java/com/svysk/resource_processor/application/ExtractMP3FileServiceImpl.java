package com.svysk.resource_processor.application;

import com.svysk.openapi.dto.SongMetadataRecordDto;
import com.svysk.resource_processor.controller.error_handler.InvalidAudioFileException;
import com.svysk.resource_processor.domain.SongDataRecord;
import com.svysk.resource_processor.domain.service.ExtractMP3FileService;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.springframework.stereotype.Service;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class ExtractMP3FileServiceImpl implements ExtractMP3FileService {

    @Override
    public SongMetadataRecordDto extract(SongDataRecord dataRecord) {
        try (InputStream inputStream =
                     new ByteArrayInputStream(dataRecord.data())) {
            Metadata metadata = new Metadata();
            Parser parser = new AutoDetectParser();
            parser.parse(
                    inputStream,
                    new DefaultHandler(),
                    metadata,
                    new ParseContext()
            );

            String contentType = metadata.get(HttpHeaders.CONTENT_TYPE);
            if(!"audio/mpeg".equals(contentType)) {
                throw new InvalidAudioFileException();
            }

            SongMetadataRecordDto dto =
                    new SongMetadataRecordDto();
            dto.setName(
                    metadata.get(TikaCoreProperties.TITLE)
            );
            dto.setArtist(
                    metadata.get(XMPDM.ARTIST)
            );
            dto.setAlbum(
                    metadata.get(XMPDM.ALBUM)
            );
            dto.setDuration(
                    formatDuration(metadata.get(XMPDM.DURATION))
            );
            dto.setYear(
                    metadata.get(XMPDM.RELEASE_DATE)
            );
            dto.setId(dataRecord.id());
            return dto;
        } catch (Exception e) {
            throw new InvalidAudioFileException();
        }
    }

    private String formatDuration(String duration) {
        if (duration == null || duration.isBlank()) {
            return null;
        }
        try {
            long totalSeconds = (long) Double.parseDouble(duration);
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            if (minutes > 99) {
                throw new InvalidAudioFileException();
            }
            return String.format(
                    "%02d:%02d",
                    minutes,
                    seconds
            );
        } catch (NumberFormatException e) {
            throw new InvalidAudioFileException();
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
