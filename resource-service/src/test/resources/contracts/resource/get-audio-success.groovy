package contracts.resource

import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Should return MP3 audio bytes for an existing resource"

    request {
        method GET()
        urlPath("/resources/42")
    }

    response {
        status OK()
        headers {
            contentType("audio/mpeg")
        }
        body("fake-mp3-content")
    }
}