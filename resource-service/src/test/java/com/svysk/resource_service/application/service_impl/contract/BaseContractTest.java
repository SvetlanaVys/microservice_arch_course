package com.svysk.resource_service.application.service_impl.contract;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BaseContractTest {

    @LocalServerPort
    protected int port;

    @MockitoBean
    private S3Client s3Client;

    @BeforeEach
    void setUp() {
        System.setProperty("test.server.port", String.valueOf(port));
    }
}
