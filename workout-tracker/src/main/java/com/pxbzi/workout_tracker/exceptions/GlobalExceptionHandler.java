package com.pxbzi.workout_tracker.exceptions;

import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<FailedRequestDto> handleNoSuchElementException(NoSuchElementException nsee, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return ResponseEntity.status(404).body(new FailedRequestDto(nsee.getMessage(), 404, requestUri));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<FailedRequestDto> handleIllegalArgumentException(IllegalArgumentException iae, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return ResponseEntity.status(400).body(new FailedRequestDto(iae.getMessage(), 400, requestUri));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<FailedRequestDto> handleException(Exception e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return ResponseEntity.status(500).body(new FailedRequestDto(e.getMessage(), 500, requestUri));
    }
}
