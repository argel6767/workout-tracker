package com.pxbzi.workout_tracker.gemini.models;

import com.pxbzi.workout_tracker.exercises.models.ExerciseDTO;
import com.pxbzi.workout_tracker.weights.models.WeightDto;
import com.pxbzi.workout_tracker.workout_sets.models.SetDto;
import com.pxbzi.workout_tracker.workouts.models.WorkoutDto;

import java.time.LocalDate;
import java.util.List;

public record ExerciseProgressionDto(ExerciseDTO exercise, List<SetDto> sets, LocalDate workoutDate, Double weight, int age, String sex) {
    public static ExerciseProgressionDto getExerciseProgressionDto(WorkoutDto workoutDto, WeightDto weightDto, int age, String sex) {
        return new ExerciseProgressionDto(workoutDto.exercise(), workoutDto.sets(), workoutDto.workoutDate(), weightDto.weight(), age, sex);
    }
}
