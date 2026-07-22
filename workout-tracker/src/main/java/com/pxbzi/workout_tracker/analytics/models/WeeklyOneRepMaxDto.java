package com.pxbzi.workout_tracker.analytics.models;

import com.pxbzi.workout_tracker.workout_sets.models.SetDto;
import java.time.LocalDate;

public record WeeklyOneRepMaxDto(
        LocalDate startDate,
        LocalDate endDate,
        Double oneRepMax,
        SetDto topSet,
        Long workoutId,
        LocalDate workoutDate
) {
}
