package com.pxbzi.workout_tracker.analytics.models;

import java.util.List;

public record StrongestExercisesOverviewDto(
        List<StrongestExerciseByMuscleGroupDto> muscleGroups,
        List<StrongestExerciseForMuscleDto> muscles
) {
}
