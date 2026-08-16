package com.svysk.resource_service.controller.error_handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex
    ) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                String.format("Invalid value '%s' for ID. Must be a positive integer", ex.getValue())
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(InvalidResourceIdValueException.class)
    public ResponseEntity<ErrorResponse> handleInvalidResourceIdException(
            InvalidResourceIdValueException ex
    ) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(ResourceIdsTooLongExcepion.class)
    public ResponseEntity<ErrorResponse> handleResourceIdsTooLong(
            ResourceIdsTooLongExcepion ex
    ) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(InvalidResourceIdsFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidResourceIds(
            InvalidResourceIdsFormatException ex
    ) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(AudioResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            AudioResourceNotFoundException ex
    ) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler({
            HttpMediaTypeNotSupportedException.class
    })
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMediaTypeNotSupportedException ex) {

        String contentType = ex.getContentType() != null
                ? ex.getContentType().toString()
                : "unknown";

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                String.format("Invalid file format: %s. Only MP3 files are allowed", contentType)

        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception ex
    ) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An error occured on the server"
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleHttpClientError(
            HttpClientErrorException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ex.getResponseBodyAsString());
    }
}
