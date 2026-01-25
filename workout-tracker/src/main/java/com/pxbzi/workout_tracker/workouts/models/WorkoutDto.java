package com.pxbzi.workout_tracker.workouts.models;

import com.pxbzi.workout_tracker.exercises.models.ExerciseDTO;
import com.pxbzi.workout_tracker.workout_sets.models.SetDto;

import java.time.LocalDate;
import java.util.List;

public record WorkoutDto(Long id, ExerciseDTO exercise, List<SetDto> sets, LocalDate workoutDate) {
    public static WorkoutDto getWorkoutDto(Workout workout) {
        ExerciseDTO exercise = ExerciseDTO.getExerciseDTO(workout.getExercise());
        List<SetDto> sets = workout.getWorkoutSets().stream()
                .map(SetDto::getSetDto)
                .toList();
        return new WorkoutDto(workout.getId(), exercise, sets, workout.getWorkoutDate());
    }
}
