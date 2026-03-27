package com.pxbzi.workout_tracker.gemini.models;

import com.pxbzi.workout_tracker.exercises.models.ExerciseDTO;
import com.pxbzi.workout_tracker.weights.models.WeightDto;
import com.pxbzi.workout_tracker.workout_sets.models.SetDto;
import com.pxbzi.workout_tracker.workouts.models.WorkoutDto;

import java.time.LocalDate;
import java.util.List;


public record ExerciseProgressionDto(ExerciseDTO exercise, SetDto topSet, LocalDate workoutDate, List<WorkoutDto> lastMonthsWorkouts, Double weight, int age, String sex) {
    public static ExerciseProgressionDto getExerciseProgressionDto(WorkoutDto workoutDto, SetDto topSet, List<WorkoutDto> lastMonthsWorkouts, WeightDto weightDto, int age, String sex) {
        return new ExerciseProgressionDto(workoutDto.exercise(), topSet, workoutDto.workoutDate(), lastMonthsWorkouts, weightDto.weight(), age, sex);
    }
}
