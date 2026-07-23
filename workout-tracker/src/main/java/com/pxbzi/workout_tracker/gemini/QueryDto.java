package com.pxbzi.workout_tracker.gemini;

import jakarta.validation.constraints.NotBlank;

public record QueryDto(@NotBlank String query) {
}
