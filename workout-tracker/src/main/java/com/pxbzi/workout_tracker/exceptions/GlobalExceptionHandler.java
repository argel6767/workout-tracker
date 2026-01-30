package com.pxbzi.workout_tracker.exceptions;

import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<FailedRequestDto> handleNoSuchElementException(NoSuchElementException nsee, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return ResponseEntity.status(404).body(new FailedRequestDto(nsee.getMessage(), 404, requestUri));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<FailedRequestDto> handleException(Exception e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return ResponseEntity.status(500).body(new FailedRequestDto(e.getMessage(), 500, requestUri));
    }
}