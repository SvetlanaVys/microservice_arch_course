# Audio Resource & Song Services

A microservice-based application for managing audio resources and song metadata.

The project consists of two services:

* **Resource Service** — stores and manages audio files and extracts their metadata.
* **Song Service** — stores and manages song metadata.

The services communicate through REST APIs defined with OpenAPI.

## Architecture

```text
                ┌──────────────┐
                │    Client    │
                └──────┬───────┘
                       │
              ┌────────┴────────┐
              ▼                 ▼
     ┌────────────────┐  ┌────────────────┐
     │ Resource       │  │ Song           │
     │ Service        │◄─┤ Service        │
     │                │  │                │
     │ Audio files    │  │ Song metadata  │
     │ Metadata       │  │ Validation     │
     └────────────────┘  └────────────────┘
```

## Technologies

* Java
* Spring Boot
* Spring Data JPA
* OpenAPI Generator
* REST API
* Apache Tika
* Maven
* Docker / Docker Compose

## Resource Service

Responsible for:

* uploading audio files;
* storing and retrieving resources;
* deleting resources;
* checking whether a resource exists;
* extracting audio metadata using Apache Tika;
* creating a corresponding Song after a resource is uploaded.

During upload, audio metadata is extracted and the duration is normalized to `mm:ss` format before being sent to the Song Service.

## Song Service

Responsible for:

* creating songs;
* retrieving songs;
* deleting songs;
* validating song metadata;
* checking that the referenced Resource exists when a Song is created directly.

Song validation includes:

* all fields are required;
* `name`, `artist`, and `album`: 1–100 characters;
* `duration`: `mm:ss` with leading zeros;
* `year`: `YYYY`, from 1900 to 2099;
* `id`: must reference an existing Resource.

## Service Communication

The services communicate using REST clients generated from OpenAPI specifications.

When a Resource is uploaded:

```text
Client
  │
  ▼
Resource Service
  │
  ├── Save Resource
  ├── Extract metadata with Tika
  ├── Normalize duration
  │
  ▼
Song Service
  │
  └── Create Song
```

When a Song is created directly:

```text
Client
  │
  ▼
Song Service
  │
  ├── Check Resource existence
  │
  └── Create Song
```

## Error Handling

Both services use a global exception handler for consistent error responses.

Typical responses include:

* `400 Bad Request` — validation or invalid request data;
* `404 Not Found` — requested Resource or Song does not exist;
* `409 Conflict` — resource conflict or duplicate data;
* `500 Internal Server Error` — unexpected server error.

## Running the Project

### 1. Start the databases

The project uses separate databases for the two services.

Start Docker Compose from the project root:

```bash
docker compose up -d
```

This creates and starts the databases required by **Resource Service** and **Song Service**.

Check running containers:

```bash
docker compose ps
```

### 2. Start Resource Service

```bash
cd resource-service
mvn spring-boot:run
```

### 3. Start Song Service

In another terminal:

```bash
cd song-service
mvn spring-boot:run
```

The services run on different ports configured in their respective `application.yml` / `application.properties` files.

## OpenAPI

API contracts are defined using OpenAPI.

OpenAPI Generator is used to generate:

* Spring controllers;
* REST API interfaces;
* Java REST clients.

After changing an OpenAPI specification, regenerate the sources:

```bash
mvn clean generate-sources
```
