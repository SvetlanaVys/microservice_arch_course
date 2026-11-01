package com.svysk.song_service;

import com.svysk.song_service.domain.SongMetadataRecord;
import com.svysk.song_service.domain.service.SongMetadataService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest (
        classes = SongServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseContractTest {

    @Autowired
    protected WebApplicationContext context;

    @MockitoBean
    private SongMetadataService service;

    @BeforeEach
    public void setUpMockMvc() {
        RestAssuredMockMvc.webAppContextSetup(context);
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

/*        SongMetadataRecord record = new SongMetadataRecord();
        record.setId(42L);
        record.setName("We are the champions");
        record.setArtist("Queen");
        record.setAlbum("News of the world");
        record.setDuration("02:77");
        record.setYear("01977");*/

        when(service.upload(any(SongMetadataRecord.class), anyString())).thenReturn(42L);
    }

    @Test
    void contextLoads() {
    }

}
