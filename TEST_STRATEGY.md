# Test Strategy

## 1. Purpose

This document defines the testing strategy for the microservices-based audio metadata processing system.

The strategy establishes testing levels, scope, responsibilities, execution stages, and quality gates to ensure that the system behaves correctly under normal operating conditions, handles failures predictably, and maintains data consistency across service boundaries.

The primary objectives are to:

- Verify the correctness of individual service components.
- Validate communication between microservices.
- Ensure reliable asynchronous event processing.
- Verify retry, dead-letter queue (DLQ), and idempotency behavior.
- Detect integration and configuration issues early.
- Maintain a reasonable balance between test coverage, execution time, and maintenance cost.

## 2. System Under Test

The system consists of the following components:

### 2.1. Resource Service

Responsibilities:

- Accept MP3 file uploads.
- Store audio files and associated resource information.
- Publish `ResourceUploadedEvent` messages after successful resource creation.
- Expose a REST endpoint for retrieving a resource by ID.

Primary interfaces:

- HTTP API for uploading audio files.
- `GET /resources/{id}` for retrieving stored audio content.
- RabbitMQ message publication through Spring Cloud Stream.

### 2.2. Resource Processor

Responsibilities:

- Consume `ResourceUploadedEvent` messages.
- Retrieve audio content from the Resource Service.
- Extract metadata from MP3 files.
- Send extracted metadata to the Song Service.
- Handle transient failures, retries, duplicate messages, and exhausted processing attempts.

Primary interfaces:

- RabbitMQ consumer binding `resourceUploaded-in-0`.
- Resource Service REST API.
- Song Service REST API.

### 2.3. Song Service

Responsibilities:

- Accept extracted audio metadata.
- Validate incoming metadata.
- Persist metadata in its own database.
- Enforce data integrity and prevent duplicate records where required.

The Song Service is the only service responsible for direct access to its database.

### 2.4. Supporting Infrastructure

The system also depends on:

- RabbitMQ for asynchronous messaging.
- PostgreSQL for persistent application data.
- MinIO for object storage.
- Eureka for service discovery.
- API Gateway for external HTTP routing.
- Docker Compose for local and integration environments.

Infrastructure configuration is part of the test scope because service discovery, network connectivity, message routing, and container configuration can directly affect application behavior.

## 3. Testing Principles

The strategy follows these principles:

1. **Test at the lowest effective level.** Business logic should be verified through unit tests whenever external infrastructure is not required.
2. **Validate service boundaries explicitly.** REST APIs, message contracts, and persistence behavior require dedicated integration or contract tests.
3. **Prioritize critical business flows.** Audio upload, metadata extraction, and metadata persistence receive the highest priority.
4. **Test failure scenarios, not only successful execution.** Timeouts, unavailable dependencies, malformed messages, and repeated delivery must be covered.
5. **Treat asynchronous processing as eventually consistent.** Tests must account for message delivery and processing delays without relying on arbitrary fixed sleeps.
6. **Verify idempotency and data integrity.** Duplicate events must not create unintended duplicate metadata records.
7. **Keep tests deterministic.** Test data, external dependencies, and environment configuration must be controlled.
8. **Separate application defects from infrastructure defects.** Test reports should provide enough information to identify the failing component or integration boundary.
9. **Use production-representative dependencies where practical.** Integration tests should use real infrastructure components rather than relying exclusively on mocks.
10. **Automate repeatable verification.** Tests suitable for continuous integration should run automatically as part of the development workflow.

## 4. Initial Test Distribution

The initial target distribution of automated test cases is:

| Test level | Target share |
|---|---:|
| Unit tests | 55% |
| Integration tests | 25% |
| Component tests | 10% |
| Contract tests | 7% |
| End-to-end tests | 3% |

These percentages represent approximate proportions of the automated test suite, not code coverage targets or allocations of engineering effort.

The distribution is an initial guideline. It should be reviewed as the application evolves, particularly when integration defects, production incidents, or test maintenance costs indicate that a different balance would be more effective.

## 5. Test Levels and Scope

## 5. Test Levels and Scope

### 5.1. Unit Tests — JUnit 5

**Objective:** Verify individual business logic components in isolation.

**Framework:** JUnit 5, with Mockito for dependency mocking where appropriate.

**Scope:**

- MP3 metadata extraction and mapping logic.
- Input validation and DTO conversion.
- Business rules and exception handling.
- Event creation and payload construction.
- Retry-related application logic.
- Idempotency decision logic.
- Boundary conditions and invalid inputs.

**Approach:**

- Execute tests without external infrastructure.
- Mock external dependencies when testing isolated logic.
- Use deterministic test data and focused assertions.
- Keep unit tests fast and independent of execution order.

**Success criteria:**

- Business logic behaves according to defined requirements.
- Tests are deterministic and provide clear failure information.

### 5.2. Integration Tests — JUnit 5

**Objective:** Verify interactions between application components and external dependencies.

**Framework:** JUnit 5 with Spring Boot Test and Testcontainers where appropriate.

**Scope:**

- Database persistence and retrieval.
- MinIO object storage operations.
- RabbitMQ message publication and consumption.
- Spring Cloud Stream binding configuration.
- REST communication between Resource Processor, Resource Service, and Song Service.
- Serialization and deserialization of messages and REST payloads.
- Database constraints and transactional behavior.

**Approach:**

- Use real infrastructure dependencies in isolated test environments.
- Verify actual HTTP responses, message payloads, and persisted data.
- Test Spring configuration and dependency wiring.
- Avoid mocking dependencies whose integration behavior is being tested.

**Success criteria:**

- Application components communicate correctly with their dependencies.
- Persistence, message delivery, and REST integration behave as expected.



### 5.3. Component Tests — Cucumber

**Objective:** Verify the externally observable behavior of an individual microservice through business-readable scenarios.

**Framework:** Cucumber with Gherkin feature files and Java step definitions. JUnit integration may be used as the test runner.

**Scope:**

- Resource Service upload and retrieval behavior.
- Resource Processor event handling and metadata processing.
- Song Service metadata validation and persistence.
- Service-level error handling.
- Service behavior when dependencies return errors or become unavailable.

**Approach:**

- Define scenarios using `Given`, `When`, and `Then`.
- Execute scenarios against a running instance or test application context of the target service.
- Use controlled test data and appropriate test doubles for dependencies outside the component boundary.
- Verify observable outcomes rather than internal implementation details.

**Example scenario:**

```gherkin
Feature: Audio resource upload

  Scenario: Upload a valid MP3 file
    Given a valid MP3 file is available
    When the client uploads the file to the Resource Service
    Then the upload request is successful
    And the resource is stored
    And a resource uploaded event is published
```

**Success criteria:**

- Each component satisfies its defined behavioral requirements.
- Scenarios remain readable, maintainable, and focused on a single service boundary.

### 5.4. Contract Tests — Spring Cloud Contract

**Objective:** Verify compatibility between service producers and consumers before deployment.

**Framework:** Spring Cloud Contract.

**Scope:**

- Resource Service REST API contracts.
- Song Service REST API contracts.
- Resource uploaded event payload structure.
- Required fields, data types, response status codes, and error formats.
- Compatibility between contract definitions and service implementations.

**Approach:**

- Define REST and messaging contracts using Spring Cloud Contract DSL.
- Generate contract tests for producers.
- Use generated stubs to validate consumers where applicable.
- Version-control contract definitions.
- Execute contract verification in the CI pipeline.

**Contracts to validate:**

1. Resource Service:
   - `GET /resources/{id}`.
   - Successful retrieval of audio content.
   - Expected behavior when a resource does not exist.

2. Song Service:
   - Metadata submission request schema.
   - Required and optional fields.
   - Successful persistence response.
   - Validation and error responses.

3. RabbitMQ messaging:
   - Event payload containing `resourceId`.
   - Message serialization and deserialization.
   - Compatibility between the event producer and consumer.
   - Expected routing and messaging behavior.

**Success criteria:**

- Producers and consumers remain compatible with their agreed contracts.
- Breaking API or message changes are detected before deployment.


### 5.5. End-to-End Tests — Cucumber

**Objective:** Validate complete business workflows across the microservices architecture.

**Framework:** Cucumber with Gherkin feature files and Java step definitions.

**Scope:**

- Audio upload through the external API.
- Resource persistence and event publication.
- RabbitMQ message delivery.
- Audio retrieval by the Resource Processor.
- MP3 metadata extraction.
- Metadata submission to the Song Service.
- Final metadata persistence.
- Selected failure, retry, and duplicate-event scenarios.

**Approach:**

- Execute scenarios against the deployed application stack and required infrastructure.
- Interact with the system through externally accessible APIs where practical.
- Verify final persistent state and other observable outcomes.
- Use bounded polling for asynchronous processing.
- Isolate test data and clean up after execution.

**Example scenario:**

```gherkin
Feature: End-to-end audio metadata processing

  Scenario: Process an uploaded MP3 file
    Given the application infrastructure is available
    And a valid MP3 file is available
    When the client uploads the file through the API Gateway
    Then the upload request is successful
    And the Resource Service publishes a resource uploaded event
    And the Resource Processor extracts the audio metadata
    And the Song Service eventually persists the metadata
```

**Additional scenarios:**

- A duplicate event does not create duplicate metadata records.
- A transient downstream failure is recovered through retries.
- A message that exhausts retries follows the configured dead-letter path.
- A missing resource does not result in incorrect metadata persistence.

**Success criteria:**

- Critical workflows complete successfully across service boundaries.
- Persistent data is correct and consistent.
- Failure scenarios produce the expected recovery or error-handling behavior.


## 6. Reliability and Failure-Mode Testing

Reliability testing focuses on the behavior of the system when dependencies fail, messages are redelivered, or processing cannot complete.

### 6.1. Retry Behavior

The Resource Processor uses Spring Retry with the following intended configuration:

- Maximum attempts: 3.
- Initial backoff interval: 1,000 ms.
- Exponential multiplier: 2.0.
- Maximum backoff interval: 5,000 ms.

Tests must verify:

- Transient failures are retried.
- Retry attempts follow the configured policy.
- Processing stops after the maximum number of attempts.
- Successful retry results in the expected downstream operation.
- Exhausted retries propagate an exception to the message-processing infrastructure.

The effective retry count must be verified against the actual configuration. Application-level retries and Spring Cloud Stream binder retries must not unintentionally multiply the number of processing attempts.

### 6.2. Dead-Letter Queue

The intended configuration uses application-level retries and Rabbit binder dead-letter support, including `max-attempts: 1` for the consumer binding and `auto-bind-dlq: true`.

Tests must verify:

- A message that cannot be processed after application-level retries is rejected or failed as expected.
- The message reaches the configured dead-letter queue when the broker and binder configuration support that behavior.
- The original message payload remains available for diagnosis.
- Dead-letter routing is configured for the correct exchange and queue.
- A successful message is not sent to the DLQ.

The test environment must use the actual binder configuration. DLQ behavior must not be inferred solely from the presence of `auto-bind-dlq: true`.

### 6.3. Idempotency

The current processor-level idempotency approach uses a Caffeine cache keyed by `resourceId`, with a maximum size of 10,000 entries and an expiration time of one hour.

The cache prevents repeated processing of a resource within the lifetime and capacity of a single processor instance. It is not a durable, cluster-wide idempotency mechanism.

Tests must verify:

- A duplicate event received by the same processor instance is handled according to the idempotency policy.
- Failed processing removes or releases the temporary reservation so that a later delivery can be retried.
- Successful processing marks the resource as processed.
- Cache expiration and eviction do not violate business requirements.
- Concurrent deliveries do not cause unintended duplicate processing within the supported scope.

Durable idempotency must be enforced at the persistence boundary where required. The Song Service should use a database uniqueness constraint or an equivalent transactional mechanism for the resource identifier if the business requirement is one metadata record per resource.

The cache and the database constraint serve different purposes: the cache reduces redundant work, while persistent uniqueness protects data integrity across restarts, multiple instances, and cache expiration.

### 6.4. Dependency Failures

Tests must cover:

- Resource Service unavailable.
- Song Service unavailable.
- Database connection failures.
- RabbitMQ connection interruptions.
- MinIO failures during resource access.
- HTTP timeouts and unsuccessful responses.
- Malformed or incomplete messages.

Each scenario must have a defined expected outcome, including whether the operation is retried, rejected, or reported as a permanent failure.

### 6.5. Data Integrity

Tests must verify:

- Audio content corresponds to the requested resource.
- Metadata is associated with the correct resource identifier.
- Failed processing does not create incomplete or incorrect records.
- Repeated delivery does not violate uniqueness constraints.
- Persistent state remains consistent after application restarts.

## 7. Test Execution Strategy

| Pipeline stage | Test types | Frameworks |
|---|---|---|
| Pull request | Unit and selected contract tests | JUnit 5, Spring Cloud Contract |
| Continuous integration | Unit, integration, and contract tests | JUnit 5, Spring Cloud Contract |
| Component validation | Component tests | Cucumber |
| Pre-release validation | Full automated suite and end-to-end tests | JUnit 5, Spring Cloud Contract, Cucumber |
| Post-deployment | Smoke and critical workflow tests | Cucumber where appropriate |

### 7.1. Pull Request Validation

Execute unit tests and relevant contract verification to provide fast feedback and detect defects before merging.

### 7.2. Continuous Integration

Execute the complete unit test suite, integration tests, and contract verification. Infrastructure-dependent tests should use isolated, reproducible environments.

### 7.3. Component Validation

Execute Cucumber scenarios for the affected microservice. Scenarios should validate its observable behavior and relevant error-handling paths.

### 7.4. Pre-Release Validation

Execute the full required automated test suite, including critical Cucumber end-to-end scenarios for upload, event processing, metadata extraction, and persistence.

### 7.5. Post-Deployment Validation

Execute a limited set of smoke tests to verify service availability, connectivity, and critical business functionality. Test data must be controlled, especially in production environments.


## 8. Test Data and Environment Management

### 8.1. Test Data

The test suite should maintain a small set of version-controlled test fixtures, including:

- A valid MP3 file with known metadata.
- An MP3 file with incomplete metadata.
- An invalid or unsupported file.
- A file that exercises relevant boundary conditions.

Fixtures must be small enough for efficient execution while remaining representative of the application's supported inputs.

### 8.2. Environment Isolation

- Each test run must use isolated or uniquely identifiable test data.
- Tests must not depend on execution order.
- Integration tests should provision their dependencies automatically where practical.
- Shared environments must be cleaned up after test execution.
- Credentials and environment-specific configuration must be provided securely.

### 8.3. Container Validation

Because application behavior depends on Docker networking and container configuration, containerized execution must be validated explicitly.

Tests and deployment checks should verify:

- Correct service hostnames and ports.
- Correct environment-variable values.
- Correct Spring profiles and application configuration.
- Required dependencies are available before dependent workflows execute.
- Container images contain the expected application artifacts.
- RabbitMQ bindings and queues are configured as expected.

A successful local execution does not replace validation in the Docker Compose environment.

## 9. Quality Gates and Release Criteria

A release is eligible for approval when the following conditions are satisfied:

- All required automated tests pass.
- No unresolved critical or high-severity defects remain without explicit risk acceptance.
- Critical REST and messaging contracts are validated.
- The primary upload-to-metadata workflow succeeds.
- Retry and dead-letter behavior has been verified.
- Idempotency and persistence constraints protect against unintended duplicate records.
- Required containers start and communicate successfully.
- Test failures are investigated and are not dismissed as intermittent without evidence.
- Test reports are available for review.

Code coverage may be used as a diagnostic metric, but coverage percentage alone is not sufficient to establish correctness. Priority should be given to meaningful assertions, critical-path coverage, and validation of failure behavior.

## 10. Defect Management and Reporting

Test failures should be classified according to their likely source:

- Application logic.
- API or messaging contract.
- Database or persistence.
- Infrastructure or environment configuration.
- Test implementation or test data.

Failure reports should include, where applicable:

- Test name and test level.
- A concise description of the expected and actual behavior.
- Relevant logs and exception details.
- The affected service and interface.
- Reproduction steps.
- Evidence of persisted state or message routing when relevant.

Intermittent failures should be investigated and tracked. Repeatedly rerunning a failing test without identifying the cause is not an acceptable substitute for resolving the defect.

## 11. Risks and Trade-Offs

The testing strategy accounts for the following risks:

| Risk | Mitigation |
|---|---|
| Excessively slow CI execution | Keep unit tests fast and reserve full-stack tests for appropriate pipeline stages. |
| Inaccurate tests caused by excessive mocking | Use real infrastructure in integration tests. |
| Duplicate message processing | Combine processor-level idempotency with persistent uniqueness constraints where required. |
| Misconfigured retry behavior | Verify the effective retry configuration and total attempt count. |
| Messages not reaching the DLQ | Test the actual Rabbit binder configuration and dead-letter routing. |
| Environment-specific failures | Validate Docker Compose configuration and container startup behavior. |
| Fragile asynchronous tests | Use condition-based polling with bounded timeouts. |
| High test maintenance cost | Test observable behavior and avoid unnecessary coupling to implementation details. |
| False confidence from coverage metrics | Prioritize assertions, critical workflows, and failure scenarios. |

## 12. Review and Evolution

The strategy should be reviewed when:

- A new service or integration is introduced.
- REST or messaging contracts change.
- Retry, DLQ, or idempotency behavior changes.
- Production incidents reveal gaps in the test suite.
- CI execution time or test maintenance cost becomes excessive.
- Deployment architecture or infrastructure changes materially.

Production defects should lead to new automated regression tests at the lowest appropriate test level. End-to-end tests should be added when the defect involves behavior across multiple service boundaries that cannot be adequately verified by lower-level tests alone.

The test distribution, execution stages, and quality gates should be adjusted based on observed defect patterns, reliability requirements, and delivery constraints.

## 13. Summary

The strategy combines fast unit tests with infrastructure-backed integration tests, service-level component tests, contract validation, and a small set of end-to-end workflows.

Particular emphasis is placed on asynchronous event processing, retry and dead-letter behavior, idempotency, persistent data integrity, and Docker-based deployment configuration.

This approach provides layered verification of the microservices architecture while keeping the automated test suite maintainable and suitable for continuous integration.
