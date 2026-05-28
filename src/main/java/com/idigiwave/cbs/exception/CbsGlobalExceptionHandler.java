package com.idigiwave.cbs.exception;

import com.idigiwave.cbs.dto.response.CbsApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CbsGlobalExceptionHandler {

    @ExceptionHandler(CbsResourceNotFoundException.class)
    public ResponseEntity<CbsApiResponse<Void>> handleNotFound(CbsResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CbsApiResponse.failure("CBS-404", ex.getMessage()));
    }

    @ExceptionHandler(CbsDuplicateResourceException.class)
    public ResponseEntity<CbsApiResponse<Void>> handleDuplicate(CbsDuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(CbsApiResponse.failure("CBS-409", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CbsApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CbsApiResponse.<Map<String, String>>builder()
                        .status("FAILURE")
                        .statusCode("CBS-400")
                        .message("Validation failed")
                        .data(errors)
                        .build());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CbsApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(CbsApiResponse.failure("CBS-409", "Duplicate or conflicting CBS data. Please retry with unique values."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CbsApiResponse<Void>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CbsApiResponse.failure("CBS-500", "Internal CBS error: " + ex.getMessage()));
    }
}
