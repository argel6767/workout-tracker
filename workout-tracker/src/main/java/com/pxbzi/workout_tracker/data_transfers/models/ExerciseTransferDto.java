package com.pxbzi.workout_tracker.data_transfers.models;

import com.pxbzi.workout_tracker.exercises.models.Exercise;
import com.pxbzi.workout_tracker.exercises.models.ExerciseType;

import java.util.List;

public record ExerciseTransferDto(String name, String description, List<String> musclesWorked, ExerciseType exerciseType) {
    public static ExerciseTransferDto of(Exercise exercise) {
        return new ExerciseTransferDto(exercise.getName(), exercise.getDescription(), 
            exercise.getMusclesWorked().stream().map(m -> m.getMuscle().getName()).toList(), 
            exercise.getExerciseType());
    }
}