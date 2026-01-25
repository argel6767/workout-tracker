package com.pxbzi.workout_tracker.analytics.models;

import java.time.LocalDate;

public record RelativeStrengthDto(Double weight, Double oneRepMax, Double relativeStrength, LocalDate entryDate) {
}
