package com.pxbzi.workout_tracker.analytics.models;

import com.pxbzi.workout_tracker.weights.models.WeightDto;
import java.util.List;

public record WeeklyOneRepMaxAnalysisDto(
        Long muscleId,
        String muscleName,
        int age,
        WeightDto bodyWeight,
        int numWeeksBack,
        List<ExerciseWeeklyOneRepMaxDto> exercises
) {
}
