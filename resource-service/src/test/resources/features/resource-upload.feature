Feature: Resource upload

    Scenario: Successfully upload an MP3 resource
    Given an MP3 resource with valid content
    When the resource is uploaded
    Then the resource should be saved and an identifier returned