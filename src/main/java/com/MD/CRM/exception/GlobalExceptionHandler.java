package com.MD.CRM.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors (400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Validation failed");
        response.put("errors", errors);
        response.put("success", false);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle duplicate resource errors (409 Conflict)
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateResourceException(DuplicateResourceException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("success", false);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Handle resource not found errors (404 Not Found)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("success", false);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle JSON parse errors including invalid enum values (400 Bad Request)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, Object> response = new HashMap<>();
        
        String message = "Invalid request format";
        
        // Check if it's an InvalidFormatException (e.g., invalid enum value)
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            
            // Check if it's an enum type
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                String fieldName = ife.getPath().get(0).getFieldName();
                Object invalidValue = ife.getValue();
                
                // Get all valid enum values
                Object[] enumConstants = ife.getTargetType().getEnumConstants();
                String validValues = Arrays.stream(enumConstants)
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                
                message = String.format("Invalid value '%s' for field '%s'. Valid values are: %s", 
                        invalidValue, fieldName, validValues);
            }
        }
        
        response.put("message", message);
        response.put("error", ex.getMessage());
        response.put("success", false);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
