package com.apighost.agent.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import org.springframework.web.context.request.WebRequest;

/**
 * Handles exceptions globally across the application, mapping them to standardized error responses.
 *
 * <p>This class uses the {@link RestControllerAdvice} annotation to intercept exceptions thrown
 * during request processing and returns a consistent {@link ErrorResponse} with appropriate
 * HTTP status codes and error details.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * // When an IllegalArgumentException is thrown:
 * throw new IllegalArgumentException("scenarioName must not be null or empty");
 * // Returns:
 * // {
 * //   "code": "E4001",
 * //   "message": "scenarioName must not be null or empty",
 * //   "timestamp": "2025-05-15T09:43:00Z"
 * // }
 * </pre>
 *
 * @author oneweeeek
 * @version BETA-0.0.1
 */
@RestControllerAdvice(basePackages = "com.apighost.agent")
public class GlobalExceptionHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * Resolves the error message by selecting the custom message from the exception if available,
     * or the default message from the specified error code.
     *
     * @param ex the exception containing a potential custom message
     * @param errorCode the error code containing the default message
     * @return the resolved error message
     */
    private String resolveErrorMessage(Exception ex, ErrorCode errorCode) {
        String customMessage = ex.getMessage();
        return StringUtils.hasText(customMessage) ? customMessage : errorCode.getMessage();
    }

    /**
     * Handles IllegalArgumentException by returning a standardized error response.
     *
     * @param ex the exception indicating an invalid argument
     * @return a ResponseEntity containing the error response with HTTP status 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ErrorCode errorCode = ErrorCode.INVALID_PARAMETER;
        ErrorResponse errorResponse = new ErrorResponse.Builder()
            .code(errorCode.getCode())
            .message(resolveErrorMessage(ex, errorCode))
            .build();
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
    }

    /**
     * Handles IOException by returning a standardized error response.
     *
     * @param ex the exception indicating an input/output error
     * @return a ResponseEntity containing the error response with HTTP status 500
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex, WebRequest request) {
        ErrorCode errorCode = ErrorCode.IO_ERROR;
        ErrorResponse errorResponse = new ErrorResponse.Builder()
            .code(errorCode.getCode())
            .message(resolveErrorMessage(ex, errorCode))
            .build();
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
    }

    /**
     * Handles FileNotFoundException by returning a standardized error response.
     *
     * @param ex the exception indicating a file was not found
     * @return a ResponseEntity containing the error response with HTTP status 404
     */
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFoundException(FileNotFoundException ex, WebRequest request) {
        ErrorCode errorCode = ErrorCode.FILE_NOT_FOUND;
        ErrorResponse errorResponse = new ErrorResponse.Builder()
            .code(errorCode.getCode())
            .message(resolveErrorMessage(ex, errorCode))
            .build();
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
    }

    /**
     * Handles NoSuchFileException by returning a standardized error response.
     *
     * @param ex the exception indicating a file was not found
     * @return a ResponseEntity containing the error response with HTTP status 404
     */
    @ExceptionHandler(NoSuchFileException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchFileException(NoSuchFileException ex, WebRequest request) {
        ErrorCode errorCode = ErrorCode.FILE_NOT_FOUND;
        ErrorResponse errorResponse = new ErrorResponse.Builder()
            .code(errorCode.getCode())
            .message(resolveErrorMessage(ex, errorCode))
            .build();
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
    }

    /**
     * Handles ClassNotFoundException by returning a standardized error response.
     *
     * @param ex the exception indicating a class was not found
     * @return a ResponseEntity containing the error response with HTTP status 500
     */
    @ExceptionHandler(ClassNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleClassNotFoundException(ClassNotFoundException ex, WebRequest request) {
        ErrorCode errorCode = ErrorCode.CLASS_NOT_FOUND;
        ErrorResponse errorResponse = new ErrorResponse.Builder()
            .code(errorCode.getCode())
            .message(resolveErrorMessage(ex, errorCode))
            .build();
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
    }

    /**
     * Handles JsonProcessingException by returning a standardized error response.
     *
     * @param ex the exception indicating a JSON processing error
     * @return a ResponseEntity containing the error response with HTTP status 400
     */
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException ex, WebRequest request) {
        ErrorCode errorCode = ErrorCode.INVALID_JSON_FORMAT;
        ErrorResponse errorResponse = new ErrorResponse.Builder()
            .code(errorCode.getCode())
            .message(resolveErrorMessage(ex, errorCode))
            .build();
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
    }

    /**
     * Handles IllegalStateException by returning a standardized error response.
     *
     * @param ex the exception indicating an invalid state
     * @return a ResponseEntity containing the error response with HTTP status 409
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException ex, WebRequest request)
        throws JsonProcessingException {
        ErrorCode errorCode = ErrorCode.ILLEGAL_STATE;
        ErrorResponse errorResponse = new ErrorResponse.Builder()
            .code(errorCode.getCode())
            .message(resolveErrorMessage(ex, errorCode))
            .build();
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("text/event-stream")) {
            String sseError = "event: error\ndata: " + objectMapper.writeValueAsString(errorResponse) + "\n\n";
            return ResponseEntity
                .status(errorCode.getHttpStatus())
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(sseError);
        }

        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
    }

    /**
     * Handles NumberFormatException by returning a standardized error response.
     *
     * @param ex the exception indicating an invalid number format
     * @return a ResponseEntity containing the error response with HTTP status 400
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<?> handleNumberFormatException(NumberFormatException ex, WebRequest request)
        throws JsonProcessingException {
        ErrorCode errorCode = ErrorCode.INVALID_PARAMETER;
        ErrorResponse errorResponse = new ErrorResponse.Builder()
            .code(errorCode.getCode())
            .message(resolveErrorMessage(ex, errorCode))
            .build();
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
    }

    /**
     * Handles any uncaught Exception by returning a standardized error response.
     *
     * @param ex the uncaught exception
     * @param request the web request associated with the exception
     * @return a ResponseEntity containing the error response with HTTP status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex, WebRequest request)
        throws JsonProcessingException {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse.Builder()
            .code(errorCode.getCode())
            .message(resolveErrorMessage(ex, errorCode))
            .build();

        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("text/event-stream")) {
            String sseError = "event: error\ndata: " + objectMapper.writeValueAsString(errorResponse) + "\n\n";
            return ResponseEntity
                .status(errorCode.getHttpStatus())
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(sseError);
        }
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
    }

}