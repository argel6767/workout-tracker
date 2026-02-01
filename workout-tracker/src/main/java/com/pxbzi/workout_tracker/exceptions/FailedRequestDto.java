package com.pxbzi.workout_tracker.exceptions;


public record FailedRequestDto(String message, Integer statusCode, String requestUri) {
}