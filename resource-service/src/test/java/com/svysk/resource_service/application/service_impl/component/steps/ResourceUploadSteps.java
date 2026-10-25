package com.svysk.resource_service.application.service_impl.component.steps;

import com.svysk.resource_service.application.port.out.ResourceEventPublisher;
import com.svysk.resource_service.domain.MP3Resource;
import com.svysk.resource_service.domain.service.ResourceService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

public class ResourceUploadSteps {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private ResourceEventPublisher resourceEventPublisher;

    private MP3Resource audioFile;
    private Long resourceId;

    @Given("an MP3 resource with valid content")
    public void anMp3ResourceWithValidContent() {
        audioFile = new MP3Resource();
        audioFile.setData("fake-mp3-content".getBytes());
        audioFile.setContentType("audio/mpeg");
        audioFile.setCreatedAt(LocalDateTime.now());
        audioFile.setSize(500L);
    }

    @When("the resource is uploaded")
    public void theResourceIsUploaded() {
        resourceId = resourceService.upload(audioFile);
    }

    @Then("the resource should be saved and an identifier returned")
    public void theResourceShouldBeSavedAndAnIdentifierReturned() {
        assertNotNull(resourceId);

        verify(s3Client).putObject(
          any(PutObjectRequest.class),
          any(RequestBody.class)
        );

        verify(resourceEventPublisher).publishResourceUploaded(resourceId);
    }
}
