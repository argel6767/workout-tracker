package com.pxbzi.workout_tracker.gemini.models;

import java.time.LocalDateTime;

public record ChatResponseDto(String body, LocalDateTime timestamp) {
}
