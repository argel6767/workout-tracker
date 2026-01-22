package com.pxbzi.workout_tracker.exercises.models;

import com.pxbzi.workout_tracker.muscles.models.MuscleDto;

import java.util.List;

public record ExerciseDTO(Long id, String name, String description, List<MuscleDto> musclesWorked) {
    public static ExerciseDTO getExerciseDTO(Exercise exercise) {
        List<MuscleDto> musclesWorked = exercise.getMusclesWorked().stream()
                .map(exerciseMuscle -> MuscleDto.getMuscleDTO(exerciseMuscle.getMuscle()))
                .toList();
        return new ExerciseDTO(exercise.getId(), exercise.getName(), exercise.getDescription(), musclesWorked);
    }
}
