package com.pxbzi.workout_tracker.analytics.models;

import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;

public record StrongestExerciseByMuscleGroupDto(Long exerciseId, String exerciseName, Double oneRepMax, MuscleGroup muscleGroup) {
}
