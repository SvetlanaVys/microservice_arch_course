package com.svysk.resource_service.application.service_impl.component.steps;


import com.svysk.resource_service.application.port.out.ResourceEventPublisher;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.s3.S3Client;

@CucumberContextConfiguration
@SpringBootTest
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

    @MockitoBean
    private S3Client s3Client;

    @MockitoBean
    private ResourceEventPublisher resourceEventPublisher;
}
