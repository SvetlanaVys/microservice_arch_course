package com.svysk.e2e;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.io.File;
import java.time.Duration;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class AudioProcessingSteps {

    private File mp3File;
    private Response uploadResponse;
    private String resourceId;

    private final String gatewayUrl = System.getProperty(
        "gateway.url", "http://localhost:8080"
    );

    private final String songServiceUrl = System.getProperty(
        "song.url", "http://localhost:8082"
    );

    @Given("a valid MP3 test file is available")
    public void validMp3FileIsAvailable() {
        mp3File = new File(
            "src/test/resources/fixtures/valid-sample-with-required-tags.mp3"
        );

        assertTrue(
            mp3File.isFile(),
            "Test MP3 file not found: " + mp3File.getAbsolutePath()
        );
    }

    @When("the client uploads the file through the API Gateway")
    public void uploadFileThroughGateway() throws Exception {
        uploadResponse = given()
            .baseUri(gatewayUrl)
            .contentType("audio/mpeg")
            .body(mp3File)
            .when()
            .post("/resources");

        if (uploadResponse.statusCode() == 200) {
            resourceId = uploadResponse.jsonPath().getString("id");
        }
    }

    @Then("the upload request is successful")
    public void uploadRequestIsSuccessful() {
        assertNotNull(uploadResponse, "Upload request was not executed");

        assertTrue(
            uploadResponse.statusCode() >= 200
                && uploadResponse.statusCode() < 300,
            "Upload failed. HTTP status: "
                + uploadResponse.statusCode()
                + ", response: " + uploadResponse.asString()
        );

        assertNotNull(
            resourceId,
            "Upload response must contain 'resourceId' or 'id'. "
                + "Response: " + uploadResponse.asString()
        );
    }
}

