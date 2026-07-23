package com.pxbzi.workout_tracker.muscles.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewMuscleDto(@NotBlank String name, @NotNull MuscleGroup muscleGroup) {
}
