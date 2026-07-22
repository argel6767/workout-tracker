package com.pxbzi.workout_tracker.analytics.models;

public record StrongestExerciseForMuscleDto(
        Long muscleId,
        String muscleName,
        Long exerciseId,
        String exerciseName,
        Double oneRepMax,
        Double avgWeightPerRep
) {
}
