package com.com.svysk.recource_processor.application;

import com.svysk.openapi.dto.SongMetadataRecordDto;
import com.svysk.resource_processor.application.ExtractMP3FileServiceImpl;
import com.svysk.resource_processor.controller.error_handler.InvalidAudioFileException;
import com.svysk.resource_processor.domain.SongDataRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class ExtractMP3FileServiceImplTest {

    private final ExtractMP3FileServiceImpl service =
            new ExtractMP3FileServiceImpl();

    @Test
    void shouldExtractMetadataFromValidMp3() throws IOException {
        // Arrange
        byte[] mp3Bytes;

        try (InputStream input = getClass()
                .getResourceAsStream(
                    "/audio/valid-sample-with-required-tags.mp3"
                )) {

            assertNotNull(input, "Test MP3 file must exist");
            mp3Bytes = input.readAllBytes();
        }

        Long expectedId = 123L;

        SongDataRecord dataRecord =
                new SongDataRecord(expectedId, mp3Bytes);

        // Act
        SongMetadataRecordDto result = service.extract(dataRecord);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expectedId);
        assertThat(result.getName()).isEqualTo("Test Title");
        assertThat(result.getArtist()).isEqualTo("Test Artist");
        assertThat(result.getAlbum()).isEqualTo("Test Album");
        assertThat(result.getDuration()).isEqualTo("00:07");
        assertThat(result.getYear()).isEqualTo("2025");
        assertThat(result.getDuration()).matches("\\d{2}:\\d{2}");
    }

    @Test
    void shouldThrowInvalidAudioFileExceptionWhenFileIsNotMp3() throws IOException {

        //Arrange
        byte[] invalidMP3Bytes;

        try (InputStream input = getClass()
                .getResourceAsStream(
                        "/audio/invalid-sample-with-missed-tags.mp3.png"
                )) {

            assertNotNull(input, "Test MP3 file must exist");
            invalidMP3Bytes = input.readAllBytes();
        }

        SongDataRecord dataRecord =
                new SongDataRecord(123L, invalidMP3Bytes);

        //Act & Assert
        assertThatThrownBy(() -> service.extract(dataRecord))
                .isInstanceOf(InvalidAudioFileException.class);
    }
}