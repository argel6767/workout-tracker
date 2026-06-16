package com.pxbzi.workout_tracker.weights.models;

import java.time.LocalDate;

public record NewWeightDto(Double weight, LocalDate entryDate) {
}
