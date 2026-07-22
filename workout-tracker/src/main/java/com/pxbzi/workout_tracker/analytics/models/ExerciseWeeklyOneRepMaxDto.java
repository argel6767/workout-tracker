package com.pxbzi.workout_tracker.analytics.models;

import java.util.List;

public record ExerciseWeeklyOneRepMaxDto(
        Long exerciseId,
        String exerciseName,
        List<WeeklyOneRepMaxChangeDto> weeklyChanges
) {
}
