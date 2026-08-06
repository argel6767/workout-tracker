package com.pxbzi.workout_tracker.workout_sets.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record NewSetDto(@NotNull @Positive Integer reps, @NotNull Double weight) {
}
