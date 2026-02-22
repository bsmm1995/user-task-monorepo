package com.example.taskmgmt.infrastructure.adapter.in.rest.exception;

import com.example.taskmgmt.infrastructure.adapter.in.rest.dto.ErrorBody;
import com.example.taskmgmt.infrastructure.adapter.in.rest.dto.ErrorDetail;
import com.example.taskmgmt.infrastructure.adapter.in.rest.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({TaskNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), request, null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", "Resource not found: " + ex.getResourcePath(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    ErrorDetail detail = new ErrorDetail();
                    detail.setField(error.getField());
                    detail.setCode(error.getCode());
                    detail.setMessage(error.getDefaultMessage());
                    return detail;
                })
                .toList();

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Validation failed", request, details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        List<ErrorDetail> details = ex.getConstraintViolations().stream()
                .map(violation -> {
                    ErrorDetail detail = new ErrorDetail();
                    detail.setField(violation.getPropertyPath().toString());
                    detail.setCode("CONSTRAINT_VIOLATION");
                    detail.setMessage(violation.getMessage());
                    return detail;
                })
                .collect(Collectors.toList());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Constraint violation", request, details);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = String.format("Method parameter '%s': Failed to convert value of type '%s' to required type '%s'",
                ex.getName(), (ex.getValue() != null ? ex.getValue().getClass().getSimpleName() : "null"),
                (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH", message, request, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, "DATA_INTEGRITY_ERROR", "Database integrity violation", request, null);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "DATABASE_ERROR", "A database error occurred", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred", request, null);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String code, String message, HttpServletRequest request, List<ErrorDetail> details) {
        ErrorBody body = new ErrorBody();
        body.setCode(code);
        body.setMessage(message);
        body.setPath(request.getRequestURI());
        body.setTimestamp(OffsetDateTime.now());
        body.setDetails(details);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(body);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
