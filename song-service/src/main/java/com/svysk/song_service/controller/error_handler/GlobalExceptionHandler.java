package com.svysk.song_service.controller.error_handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex
    ) {

        ErrorResponse error = new ErrorResponse(
                String.format("Invalid value '%s' for ID. Must be a positive integer", ex.getValue()),
                HttpStatus.BAD_REQUEST
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(InvalidRecordIdValueException.class)
    public ResponseEntity<ErrorResponse> handleInvalidResourceIdException(
            InvalidRecordIdValueException ex
    ) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(RecordIdsTooLongException.class)
    public ResponseEntity<ErrorResponse> handleResourceIdsTooLong(
            RecordIdsTooLongException ex
    ) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(InvalidRecordIdsFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidResourceIds(
            InvalidRecordIdsFormatException ex
    ) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(SongNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            SongNotFoundException ex
    ) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(AudioResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            AudioResourceNotFoundException ex
    ) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.CONFLICT
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(SongMetadataAlreadyExists.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            SongMetadataAlreadyExists ex
    ) {

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.CONFLICT
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetailsResponse> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        this::getValidationMessage,
                        (existing, replacement) -> existing
                ));
        return ResponseEntity
                .badRequest()
                .body(new ErrorDetailsResponse(
                        "Validation error",
                        details,
                        HttpStatus.BAD_REQUEST
                ));
    }

    private String getValidationMessage(FieldError error) {
        return switch (error.getField()) {
            case "id" -> "ID is required";
            case "name" -> switch (Objects.requireNonNull(error.getCode())) {
                case "NotNull", "NotBlank" ->
                        "Song name is required";
                case "Size" ->
                        "Song name must be between 1 and 100 characters";
                default ->
                        "Invalid name";
            };
            case "artist" -> switch (Objects.requireNonNull(error.getCode())) {
                case "NotNull", "NotBlank" ->
                        "Artist name is required";
                case "Size" ->
                        "Artist name must be between 1 and 100 characters";
                default ->
                        "Invalid artist";
            };
            case "album" -> switch (Objects.requireNonNull(error.getCode())) {
                case "NotNull", "NotBlank" ->
                        "Album name is required";
                case "Size" ->
                        "Album name must be between 1 and 100 characters";
                default ->
                        "Invalid album";
            };
            case "duration" -> switch (Objects.requireNonNull(error.getCode())) {
                case "NotNull", "NotBlank" ->
                        "Duration is required";
                case "Pattern" ->
                        "Duration must be in mm:ss format with leading zeros";
                default ->
                        "Invalid duration";
            };
            case "year" -> switch (Objects.requireNonNull(error.getCode())) {
                case "NotNull", "NotBlank" ->
                        "Year is required";
                case "Pattern" ->
                        "Year must be between 1900 and 2099";
                default ->
                        "Invalid year";
            };
            default -> "Validation error";
        };
    }
}
