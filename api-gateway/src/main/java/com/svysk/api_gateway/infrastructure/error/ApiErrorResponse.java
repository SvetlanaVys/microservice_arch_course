package com.svysk.api_gateway.infrastructure.error;

import java.time.LocalDateTime;

public record ApiErrorResponse (
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        String service
){}
