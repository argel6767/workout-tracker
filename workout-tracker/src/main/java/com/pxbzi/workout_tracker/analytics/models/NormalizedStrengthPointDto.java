package com.pxbzi.workout_tracker.analytics.models;

import java.time.LocalDate;

public record NormalizedStrengthPointDto(
        LocalDate weekStart,
        LocalDate weekEnd,
        Double averageStrengthIndex,
        Integer exerciseCount
) {
}
