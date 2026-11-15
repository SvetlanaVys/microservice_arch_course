package com.svysk.api_gateway.infrastructure.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.core.annotation.Order;
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
@Order(-2)
@RequiredArgsConstructor
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, @NonNull Throwable ex) {

        var response = exchange.getResponse();

        if (response.isCommitted()) {
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

    private String getUserFriendlyMessage(HttpStatus status, Throwable ex, String service) {
        String readableService = formatServiceName(service);

        if (hasCause(ex, ConnectException.class) || hasCause(ex, WebClientException.class) || status == HttpStatus.SERVICE_UNAVAILABLE) {
            return "We are having trouble connecting to the " + readableService + ". Our team is on it, please try again in a few moments.";
        }

        if (hasCause(ex, TimeoutException.class) || status == HttpStatus.GATEWAY_TIMEOUT) {
            return "The " + readableService + " is taking too long to respond. Please refresh the page or try again shortly.";
        }

        return switch (status) {
            case NOT_FOUND ->
                    "The page or resource you are looking for doesn't exist. Please check the URL and try again.";
            case UNAUTHORIZED ->
                    "Your session has expired or you don't have permission to view this. Please log in again.";
            case FORBIDDEN ->
                    "You don't have access to this square. If you believe this is a mistake, please contact support.";
            case BAD_REQUEST -> "Something went wrong with the data sent. Please verify your input and try again.";
            default ->
                    "Something went wrong on our end. Please try again later, or contact support if the issue persists.";
        };
    }

    private String formatServiceName(String service) {
        if (service == null || service.isBlank() || "requested service".equalsIgnoreCase(service)) {
            return "requested service";
        }
        String formatted = service.replace("-", " ").replace("_", " ").toLowerCase();
        return formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
    }

    private String getServiceName(ServerWebExchange exchange) {
        Route route = exchange.getAttribute(
                ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR
        );

        if (route == null) {
            return null;
        }

        URI uri = route.getUri();

        if ("lb".equals(uri.getScheme())) {
            return uri.getHost();
        }

        return route.getId();
    }

    private boolean hasCause(Throwable ex, Class<? extends Throwable> type) {
        Throwable current = ex;

        while (current != null) {
            if (type.isInstance(current)) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }

    private Object findCause(Throwable ex, Class<ResponseStatusException> type) {
        Throwable current = ex;

        while (current != null) {
            if (type.isInstance(current)) {
                return current;
            }

            current = current.getCause();
        }

        return null;
    }
}
