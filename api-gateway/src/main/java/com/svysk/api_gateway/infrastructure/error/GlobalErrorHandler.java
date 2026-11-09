package com.svysk.api_gateway.infrastructure.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.net.URI;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, @NonNull Throwable ex) {

        var response = exchange.getResponse();

        if(response.isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus status = determineHttpStatus(ex);

        String service = getServiceName(exchange);

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                getUserFriendlyMessage(status, ex, service),
                exchange.getRequest().getPath().value(),
                service
        );

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);

            return response.writeWith(
                    Mono.just(response.bufferFactory().wrap(bytes))
            );
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    private HttpStatus determineHttpStatus(Throwable ex) {

        if (hasCause(ex, ConnectException.class)
                || hasCause(ex, WebClientException.class)) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }

        if (hasCause(ex, TimeoutException.class)) {
            return HttpStatus.GATEWAY_TIMEOUT;
        }

        if (hasCause(ex, ResponseStatusException.class)) {

            ResponseStatusException exception =
                    (ResponseStatusException) findCause(ex, ResponseStatusException.class);

            assert exception != null;
            return HttpStatus.valueOf(exception.getStatusCode().value());
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String getUserFriendlyMessage(
            HttpStatus status,
            Throwable ex,
            String service
    ) {
        String serviceName = service != null
                ? service
                : "requested service";

        if(hasCause(ex, ConnectException.class)) {
            return serviceName + " is currently unavailable. "
                    + "Please try again later.";
        }

        if(hasCause(ex, TimeoutException.class)) {
            return serviceName + " did not respond in time. "
                    + "Please try again later.";
        }

        if(hasCause(ex, WebClientException.class)) {
            return "Unable to establish communication with "
                    + serviceName + ". Please try again later.";
        }

        return switch (status) {
            case NOT_FOUND ->
                "The requested endpoint was not found.";
            case SERVICE_UNAVAILABLE ->
                serviceName + " is currently unavailable.";
            case GATEWAY_TIMEOUT ->
                    serviceName + " did not respond in time.";
            default ->
                "An unexpected error occurred while processing your request.";
        };
    }

    private String getServiceName(ServerWebExchange exchange) {
        Route route = exchange.getAttribute(
                ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR
        );

        if(route == null) {
            return null;
        }

        URI uri = route.getUri();

        if("lb".equals(uri.getScheme())) {
            return uri.getHost();
        }

        return route.getId();
    }

    private boolean hasCause(Throwable ex, Class<? extends Throwable> type) {
        Throwable current = ex;

        while (current != null) {
            if(type.isInstance(current)) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }

    private Object findCause(Throwable ex, Class<ResponseStatusException> type) {
        Throwable current = ex;

        while (current != null) {
            if(type.isInstance(current)) {
                return current;
            }

            current = current.getCause();
        }

        return null;
    }
}
