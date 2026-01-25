package com.pxbzi.workout_tracker.analytics.models;

import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;

public record StrongestExerciseByMuscleDto(Long exerciseId, String exerciseName, Double oneRepMax, Double avgWeightPerRep) {
}
