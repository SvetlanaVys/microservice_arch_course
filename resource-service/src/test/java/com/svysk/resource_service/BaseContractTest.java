package com.svysk.resource_service;

import com.svysk.resource_service.domain.MP3Resource;
import com.svysk.resource_service.domain.service.ResourceService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;
import software.amazon.awssdk.services.s3.S3Client;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = ResourceServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@ActiveProfiles("test")
public class BaseContractTest {

    @Autowired
    protected WebApplicationContext context;

    @MockitoBean
    private S3Client s3Client;

    @MockitoBean
    private ResourceService service;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.webAppContextSetup(context);

        MP3Resource resource = new MP3Resource();
        resource.setData("fake-mp3-content".getBytes(StandardCharsets.UTF_8));
        resource.setContentType("audio/mpeg");
        resource.setSize(500L);
        resource.setCreatedAt(LocalDateTime.now());

        when(service.getById(42L)).thenReturn(resource);
    }
}

