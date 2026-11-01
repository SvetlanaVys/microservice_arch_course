package contracts.song

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Creates song metadata successfully"

    request {
        method POST()
        urlPath("/songs")
        headers {
            contentType(applicationJson())
            header("X-Source-Service", "resource-srv")
        }
        body(
            "id": 42L,
            "name": "We are the champions",
            "artist": "Queen",
            "album": "News of the world",
            "duration": "02:55",
            "year": "1977"
        )
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(
            id: 42
        )
    }
}