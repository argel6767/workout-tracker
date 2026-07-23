package com.pxbzi.workout_tracker.weights.models;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record NewWeightDto(@NotNull @Positive Double weight, LocalDate entryDate) {
}
