Feature: End-to-end audio metadata processing

  Scenario: Process an uploaded MP3 file
    Given a valid MP3 test file is available
    When the client uploads the file through the API Gateway
    Then the upload request is successful