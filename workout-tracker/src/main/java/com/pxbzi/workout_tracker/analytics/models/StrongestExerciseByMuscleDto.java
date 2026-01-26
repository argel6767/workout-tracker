package com.pxbzi.workout_tracker.analytics.models;

public record StrongestExerciseByMuscleDto(Long exerciseId, String exerciseName, Double oneRepMax, Double avgWeightPerRep) {
}
