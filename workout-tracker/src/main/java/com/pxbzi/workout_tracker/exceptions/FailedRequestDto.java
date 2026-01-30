package com.pxbzi.workout_tracker.exceptions;

import java.net.URI;

public record FailedRequestDto(String message, Integer statusCode, String requestUri) {
}