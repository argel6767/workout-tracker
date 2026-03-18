package com.pxbzi.workout_tracker.data_transfers.models;

import java.time.LocalDate;
import java.util.List;

import com.pxbzi.workout_tracker.workouts.models.Workout;


public record WorkoutTransferDto(String exercise, List<SetTransferDto> sets, LocalDate workoutDate) {
    
    public static WorkoutTransferDto of(Workout workout) {
        return new WorkoutTransferDto(
            workout.getExercise().getName(),
            workout.getWorkoutSets().stream().map(SetTransferDto::of).toList(),
            workout.getWorkoutDate()
        );
    }
}